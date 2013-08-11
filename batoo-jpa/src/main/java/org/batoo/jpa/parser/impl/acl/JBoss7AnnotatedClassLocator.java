/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.parser.impl.acl;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.spi.PersistenceUnitInfo;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.spi.AnnotatedClassLocator;
import org.jboss.as.jpa.spi.PersistenceUnitMetadata;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * Annotated class locator for JBoss 7.x series.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class JBoss7AnnotatedClassLocator implements AnnotatedClassLocator {

	private static final BLogger LOG = BLoggerFactory.getLogger(JBoss7AnnotatedClassLocator.class);

	private static AnnotatedClassLocator INSTANCE = new JBoss7AnnotatedClassLocator();

	private static volatile ReentrantLock lock = new ReentrantLock();
	private static final Map<PersistenceUnitMetadata, Map<URL, Set<Class<?>>>> CLASS_CACHE = Maps.newHashMap();

	private static Map<URL, Set<Class<?>>> getClassCache(PersistenceUnitMetadata pu) {
		Map<URL, Set<Class<?>>> classMap = JBoss7AnnotatedClassLocator.CLASS_CACHE.get(pu);
		if (classMap == null) {
			classMap = Maps.newHashMap();
			JBoss7AnnotatedClassLocator.CLASS_CACHE.put(pu, classMap);
		}

		return classMap;
	}

	/**
	 * Returns the singleton {@link JBoss7AnnotatedClassLocator}.
	 * 
	 * @return the singleton {@link JBoss7AnnotatedClassLocator}
	 * 
	 * @since 2.0.0
	 */
	public static AnnotatedClassLocator getInstance() {
		return JBoss7AnnotatedClassLocator.INSTANCE;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	private JBoss7AnnotatedClassLocator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<?>> locateClasses(PersistenceUnitInfo persistenceUnitInfo, URL url) {
		JBoss7AnnotatedClassLocator.LOG.info("Checking persistence root {0} for persistence classes...", url.getFile());

		JBoss7AnnotatedClassLocator.lock.lock();

		final Set<Class<?>> classes = Sets.newHashSet();
		try {
			final PersistenceUnitMetadata pu = (PersistenceUnitMetadata) persistenceUnitInfo;

			if (pu.getAnnotationIndex() != null) {
				final Index index = pu.getAnnotationIndex().get(url);
				if (index == null) {
					JBoss7AnnotatedClassLocator.LOG.info("No classes present in the jar url {0}", url);
					return Collections.emptySet();
				}

				this.locateClassesFor(pu.getClassLoader(), index, Embeddable.class, classes);
				this.locateClassesFor(pu.getClassLoader(), index, MappedSuperclass.class, classes);
				this.locateClassesFor(pu.getClassLoader(), index, Entity.class, classes);
				JBoss7AnnotatedClassLocator.getClassCache(pu).put(url, classes);

				return classes;
			}
			else {
				return JBoss7AnnotatedClassLocator.getClassCache(pu).get(url);
			}
		}
		finally {
			JBoss7AnnotatedClassLocator.lock.unlock();

			JBoss7AnnotatedClassLocator.LOG.info("Found persistent classes {0}", classes.toString());
		}
	}

	private void locateClassesFor(ClassLoader classLoader, Index index, Class<? extends Annotation> annotation, Set<Class<?>> classes) {
		final DotName annotationName = DotName.createSimple(annotation.getName());
		final List<AnnotationInstance> classesWithAnnotation = index.getAnnotations(annotationName);

		for (final AnnotationInstance annotationInstance : classesWithAnnotation) {
			// verify that the annotation target is actually a class, since some frameworks
			// may generate bytecode with annotations placed on methods (see AS7-2559)
			if (annotationInstance.target() instanceof ClassInfo) {
				final String className = annotationInstance.target().toString();
				try {
					JBoss7AnnotatedClassLocator.LOG.info("Located persistent {0} class {1}", annotation.getSimpleName(), className);

					classes.add(classLoader.loadClass(className));
				}
				catch (final ClassNotFoundException e) {
					throw new RuntimeException("Cannot load persitent class: " + className);
				}
			}
		}
	}
}

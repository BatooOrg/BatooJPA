/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
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

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.spi.AnnotatedClassLocator;

import com.google.common.collect.Sets;

/**
 * The abstract implementation of {@link AnnotatedClassLocator}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class BaseAnnotatedClassLocator implements AnnotatedClassLocator {

	private static final BLogger LOG = BLoggerFactory.getLogger(BaseAnnotatedClassLocator.class);

	private static void locateClasses(PersistenceUnitInfo puInfo, final Set<Class<?>> classes, final URL jarUrl) {
		AnnotatedClassLocator locator = null;

		if (jarUrl.getProtocol().equals("file")) {
			final String path = jarUrl.getPath();
			final File file = new File(path);

			if (file.isDirectory()) {
				locator = ClassloaderAnnotatedClassLocator.getInstance();
			}
			else {
				locator = JarAnnotatedClassLocator.getInstance();
			}
		}
		else if (jarUrl.getProtocol().equals("vfs")) {
			locator = org.batoo.jpa.parser.impl.acl.JBoss7AnnotatedClassLocator.getInstance();
		}
		else {
			throw new IllegalArgumentException("Unknown jar url protocol: " + jarUrl);
		}

		classes.addAll(locator.locateClasses(puInfo, jarUrl));
	}

	/**
	 * Locates the classes available.
	 * 
	 * @param puInfo
	 *            the persistence unit info
	 * @return the array of classes located
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Set<Class<?>> locatePersistentClasses(PersistenceUnitInfo puInfo) {
		final ClassLoader classLoader = puInfo.getClassLoader();
		final List<URL> jarFiles = puInfo.getJarFileUrls();
		final List<String> managedClassNames = puInfo.getManagedClassNames();
		final boolean excludeUnlistedClasses = puInfo.excludeUnlistedClasses();

		final Set<Class<?>> classes = Sets.newHashSet();

		if ((jarFiles != null) && !jarFiles.isEmpty()) {
			for (final URL jarUrl : jarFiles) {
				BaseAnnotatedClassLocator.locateClasses(puInfo, classes, jarUrl);
			}
		}

		for (final String className : managedClassNames) {
			try {
				classes.add(classLoader.loadClass(className));
			}
			catch (final ClassNotFoundException e) {
				throw new PersistenceException("Unable to load listed persistent class " + className, e);
			}
		}

		if (!excludeUnlistedClasses) {
			BaseAnnotatedClassLocator.locateClasses(puInfo, classes, puInfo.getPersistenceUnitRootUrl());
		}

		return classes;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected BaseAnnotatedClassLocator() {
		super();
	}

	/**
	 * Returns if the class with then name <code>className</code> is an annotated persistent class.
	 * 
	 * @param classloader
	 *            the class loader
	 * @param className
	 *            the name of the class
	 * @return true if the class with then name <code>className</code> is an annotated persistent class, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Class<?> isPersistentClass(ClassLoader classloader, String className) {
		try {
			final Class<?> clazz = classloader.loadClass(className);

			if ((clazz.getAnnotation(Embeddable.class) != null) || //
				(clazz.getAnnotation(MappedSuperclass.class) != null) || //
				(clazz.getAnnotation(Entity.class) != null)) {

				return clazz;
			}
		}
		catch (final Throwable e) {
			// nasty eclipse JUnit fragment spits bogus class loading errors
			if (!className.startsWith("org.eclipse.jdt")) {
				BaseAnnotatedClassLocator.LOG.debug(e, "Unable to read class: {0}" + className);
			}
		}

		return null;
	}
}

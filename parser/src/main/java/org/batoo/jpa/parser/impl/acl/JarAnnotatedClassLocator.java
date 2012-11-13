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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.spi.AnnotatedClassLocator;

import com.google.common.collect.Sets;

/**
 * {@link AnnotatedClassLocator} implementation for JAR archives.
 * 
 * @author hceylan
 * @since $version
 */
public class JarAnnotatedClassLocator extends BaseAnnotatedClassLocator {

	private static final BLogger LOG = BLoggerFactory.getLogger(JarAnnotatedClassLocator.class);

	private static final JarAnnotatedClassLocator INSTANCE = new JarAnnotatedClassLocator();

	/**
	 * Returns the singleton {@link JarAnnotatedClassLocator} instance.
	 * 
	 * @return the singleton {@link JarAnnotatedClassLocator} instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static JarAnnotatedClassLocator getInstance() {
		return JarAnnotatedClassLocator.INSTANCE;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private JarAnnotatedClassLocator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<?>> locateClasses(PersistenceUnitInfo persistenceUnitInfo, URL url) {
		final Set<Class<?>> classes = Sets.newHashSet();

		try {
			final JarFile jarFile = new JarFile(url.getFile());

			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();

				if (entry.isDirectory()) {
					continue;
				}

				final String className = entry.getName().replace('/', '.').replace('\\', '.');

				if (className.endsWith(".class")) {
					final Class<?> clazz = this.isPersistentClass(persistenceUnitInfo.getClassLoader(), className.substring(0, className.length() - 6));
					if (clazz != null) {
						JarAnnotatedClassLocator.LOG.info("Found annotated class {0}", className);

						classes.add(clazz);
					}
				}

			}

			return classes;
		}
		catch (final IOException e) {
			throw new PersistenceException("Unable to read JAR url: " + url);
		}
	}
}

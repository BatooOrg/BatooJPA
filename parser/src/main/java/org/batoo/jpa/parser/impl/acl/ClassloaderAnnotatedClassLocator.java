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
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.spi.AnnotatedClassLocator;

import com.google.common.collect.Sets;

/**
 * {@link AnnotatedClassLocator} implementation for context classloader.
 * 
 * @author hceylan
 * @since $version
 */
public class ClassloaderAnnotatedClassLocator extends BaseAnnotatedClassLocator {

	private static final BLogger LOG = BLoggerFactory.getLogger(ClassloaderAnnotatedClassLocator.class);

	private static final ClassloaderAnnotatedClassLocator INSTANCE = new ClassloaderAnnotatedClassLocator();

	/**
	 * Returns the singleton {@link ClassloaderAnnotatedClassLocator}.
	 * 
	 * @return the singleton {@link ClassloaderAnnotatedClassLocator}
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static ClassloaderAnnotatedClassLocator getInstance() {
		return ClassloaderAnnotatedClassLocator.INSTANCE;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ClassloaderAnnotatedClassLocator() {
		super();
	}

	private void findClasses(ClassLoader cl, Set<Class<?>> classes, int rootLength, File file) throws IOException {
		if (file.isDirectory()) {
			for (final String child : file.list()) {
				this.findClasses(cl, classes, rootLength, new File(file.getCanonicalPath() + "/" + child));
			}
		}
		else {
			String path = file.getPath();

			if (path.endsWith(".class")) {
				// Windows compatibility
				if (System.getProperty("os.name").toUpperCase(Locale.ENGLISH).startsWith("WINDOWS")) {
					rootLength--;
				}

				path = path.substring(rootLength, path.length() - 6).replace("/", ".").replace("\\", ".");
				try {
					final Class<?> clazz = this.isPersistentClass(cl, path);
					if (clazz != null) {
						classes.add(clazz);
					}
				}
				catch (final Exception e) {
					ClassloaderAnnotatedClassLocator.LOG.warn("Cannot load class {0} in {1}", path, file.getPath());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<?>> locateClasses(PersistenceUnitInfo persistenceUnitInfo, URL url) {
		final Set<Class<?>> classes = Sets.newHashSet();

		final ClassLoader classLoader = persistenceUnitInfo.getClassLoader();

		try {
			final Enumeration<URL> resources = classLoader.getResources("");
			while (resources.hasMoreElements()) {
				String root = resources.nextElement().getFile();
				if (root.endsWith("WEB-INF/")) {
					root = root + "classes/";
				}

				if (!root.endsWith(File.separator) && !root.endsWith("/")) {
					root = root + File.separator;
				}

				this.findClasses(classLoader, classes, root.length(), new File(root));
			}

			return classes;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot scan the classpath", e);
		}
	}
}

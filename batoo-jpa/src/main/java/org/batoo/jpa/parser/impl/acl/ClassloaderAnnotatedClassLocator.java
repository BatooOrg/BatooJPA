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

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.spi.PersistenceUnitInfo;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.spi.AnnotatedClassLocator;

import com.google.common.collect.Sets;

/**
 * {@link AnnotatedClassLocator} implementation for context classloader.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ClassloaderAnnotatedClassLocator extends BaseAnnotatedClassLocator {

	private static final BLogger LOG = BLoggerFactory.getLogger(ClassloaderAnnotatedClassLocator.class);

	private static final ClassloaderAnnotatedClassLocator INSTANCE = new ClassloaderAnnotatedClassLocator();

	/**
	 * Returns the singleton {@link ClassloaderAnnotatedClassLocator}.
	 * 
	 * @return the singleton {@link ClassloaderAnnotatedClassLocator}
	 * 
	 * @since 2.0.0
	 */
	public static ClassloaderAnnotatedClassLocator getInstance() {
		return ClassloaderAnnotatedClassLocator.INSTANCE;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	private ClassloaderAnnotatedClassLocator() {
		super();
	}

	private Set<Class<?>> findClasses(ClassLoader cl, Set<Class<?>> classes, String root, String path) {
		final File file = new File(path);

		if (file.isDirectory()) {
			ClassloaderAnnotatedClassLocator.LOG.debug("Processing directory {0}", path);

			for (final String child : file.list()) {
				final String childPath = path + ( path.endsWith("/") ? child: "/" + child);
				this.findClasses(cl, classes, root, childPath);
			}
		}
		else {
			if (FilenameUtils.isExtension(path, "class")) {
				final String normalizedPath = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(path));

				final int rootLength = FilenameUtils.normalizeNoEndSeparator(root).length();
				String className = normalizedPath.substring(rootLength + 1).replaceAll("/", ".");
				className = StringUtils.left(className, className.length() - 6);

				final Class<?> clazz = this.isPersistentClass(cl, className);
				if (clazz != null) {
					ClassloaderAnnotatedClassLocator.LOG.debug("Found persistent class {0}", className);
					classes.add(clazz);
				}
			}
		}

		return classes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<?>> locateClasses(PersistenceUnitInfo persistenceUnitInfo, URL url) {
		final String root = FilenameUtils.separatorsToUnix(FilenameUtils.normalize(url.getFile()));

		ClassloaderAnnotatedClassLocator.LOG.info("Checking persistence root {0} for persistence classes...", root);

		final HashSet<Class<?>> classes = Sets.newHashSet();
		try {
			return this.findClasses(persistenceUnitInfo.getClassLoader(), classes, root, root);
		}
		finally {
			ClassloaderAnnotatedClassLocator.LOG.info("Found persistent classes {0}", classes.toString());
		}
	}
}

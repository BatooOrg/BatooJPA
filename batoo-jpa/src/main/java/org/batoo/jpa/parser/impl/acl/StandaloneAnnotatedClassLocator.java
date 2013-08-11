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

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.batoo.jpa.spi.AnnotatedClassLocator;

import com.google.common.collect.Sets;

/**
 * Implementation of {@link AnnotatedClassLocator} that utilizes the {@link JarAnnotatedClassLocator} and
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class StandaloneAnnotatedClassLocator implements AnnotatedClassLocator {

	private static final StandaloneAnnotatedClassLocator INSTANCE = new StandaloneAnnotatedClassLocator();

	/**
	 * Returns the singleton {@link StandaloneAnnotatedClassLocator}.
	 * 
	 * @return the singleton {@link StandaloneAnnotatedClassLocator}
	 * 
	 * @since 2.0.0
	 */
	public static StandaloneAnnotatedClassLocator getInstance() {
		return StandaloneAnnotatedClassLocator.INSTANCE;
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	private StandaloneAnnotatedClassLocator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<?>> locateClasses(PersistenceUnitInfo puInfo, URL url) {
		final ClassLoader classLoader = puInfo.getClassLoader();
		final List<URL> jarFiles = puInfo.getJarFileUrls();
		final List<String> managedClassNames = puInfo.getManagedClassNames();
		final boolean excludeUnlistedClasses = puInfo.excludeUnlistedClasses();

		final Set<Class<?>> classes = Sets.newHashSet();

		if ((jarFiles != null) && !jarFiles.isEmpty()) {
			for (final URL jarUrl : jarFiles) {
				classes.addAll(JarAnnotatedClassLocator.getInstance().locateClasses(puInfo, jarUrl));
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
			classes.addAll(ClassloaderAnnotatedClassLocator.getInstance().locateClasses(puInfo, null));
		}

		return classes;
	}
}

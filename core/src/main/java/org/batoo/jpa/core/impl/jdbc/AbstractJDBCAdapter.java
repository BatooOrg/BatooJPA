/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.jdbc;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Set;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractJDBCAdapter {

	private static final BLogger LOG = BLogger.getLogger(JDBCAdapter.class);

	private static final Map<String, JDBCAdapter> ADAPTERS = Maps.newHashMap();
	private static final Map<String, JDBCAdapter> INTERNAL_ADAPTERS = Maps.newHashMap();

	static {
		AbstractJDBCAdapter.scan(false);
	}

	/**
	 * Returns the adapter.
	 * 
	 * @param scanExternalDrivers
	 * 
	 * @param className
	 *            the class name of the JDBC Driver
	 * 
	 * @return the adapters
	 * @since $version
	 */
	public static JDBCAdapter getAdapter(boolean scanExternalDrivers, String className) {
		if (scanExternalDrivers) {
			if (ADAPTERS.isEmpty()) {
				AbstractJDBCAdapter.scan(true);
			}

			return ADAPTERS.get(className);
		}

		return INTERNAL_ADAPTERS.get(className);
	}

	private static void scan(boolean external) {
		final long start = System.currentTimeMillis();

		final Map<String, JDBCAdapter> adapters = external ? ADAPTERS : INTERNAL_ADAPTERS;

		final Set<URL> urls = external ? //
			ReflectHelper.getClasspathUrls() : //
			ClasspathHelper.forPackage(JDBCAdapter.class.getPackage().getName(), ClasspathHelper.defaultClassLoaders);

		final ConfigurationBuilder configuration = new ConfigurationBuilder() //
		.setUrls(urls) //
		.useParallelExecutor();

		final Set<Class<? extends JDBCAdapter>> adaptersClasses = new Reflections(configuration).getSubTypesOf(JDBCAdapter.class);

		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				for (final Class<? extends JDBCAdapter> clazz : adaptersClasses) {
					try {
						final JDBCAdapter adapter = clazz.newInstance();
						final String[] jdbcDriverClassNames = adapter.getJDBCDriverClassNames();
						for (final String jdbcDriverClassName : jdbcDriverClassNames) {
							adapters.put(jdbcDriverClassName, adapter);
						}
					}
					catch (final InstantiationException e) {
						LOG.error("JDBC Adapter " + clazz + " does not have default constructor");
					}
					catch (final IllegalAccessException e) {}
				}

				return null;
			}
		});

		LOG.trace("JDBC Driver discovery took {0} msecs", System.currentTimeMillis() - start);
	}

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractJDBCAdapter() {
		super();
	}

	/**
	 * @return the JDBC Driver this adapter works with
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String[] getJDBCDriverClassNames();
}

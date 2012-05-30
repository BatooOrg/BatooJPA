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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractJdbcAdaptor {

	private static final BLogger LOG = BLoggerFactory.getLogger(AbstractJdbcAdaptor.class);

	private static final Map<String, JdbcAdaptor> ADAPTERS = Maps.newHashMap();
	private static final Map<String, JdbcAdaptor> INTERNAL_ADAPTERS = Maps.newHashMap();

	static {
		AbstractJdbcAdaptor.scan(false);
	}

	/**
	 * Returns the JDBC Adapter.
	 * 
	 * @param scanExternalDrivers
	 *            whether external drivers should be scanned
	 * @param className
	 *            the class name of the JDBC Driver
	 * 
	 * @return the adapters
	 * @since $version
	 */
	public static JdbcAdaptor getAdapter(boolean scanExternalDrivers, String className) {
		if (scanExternalDrivers) {
			if (AbstractJdbcAdaptor.ADAPTERS.isEmpty()) {
				AbstractJdbcAdaptor.scan(true);
			}

			return AbstractJdbcAdaptor.ADAPTERS.get(className);
		}

		return AbstractJdbcAdaptor.INTERNAL_ADAPTERS.get(className);
	}

	private static void scan(boolean external) {
		final long start = System.currentTimeMillis();

		final Map<String, JdbcAdaptor> adapters = external ? AbstractJdbcAdaptor.ADAPTERS : AbstractJdbcAdaptor.INTERNAL_ADAPTERS;

		final Set<URL> urls = external ? //
			ReflectHelper.getClasspathUrls() : //
			ClasspathHelper.forPackage(JdbcAdaptor.class.getPackage().getName(), ClasspathHelper.defaultClassLoaders);

		final ConfigurationBuilder configuration = new ConfigurationBuilder() //
		.setUrls(urls) //
		.useParallelExecutor();

		final Set<Class<? extends JdbcAdaptor>> adaptersClasses = new Reflections(configuration).getSubTypesOf(JdbcAdaptor.class);

		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				for (final Class<? extends JdbcAdaptor> clazz : adaptersClasses) {
					try {
						final JdbcAdaptor adapter = clazz.newInstance();
						final String[] jdbcDriverClassNames = adapter.getJdbcDriverClassNames();
						for (final String jdbcDriverClassName : jdbcDriverClassNames) {
							adapters.put(jdbcDriverClassName, adapter);
						}
					}
					catch (final InstantiationException e) {
						AbstractJdbcAdaptor.LOG.error("JDBC Adapter " + clazz + " does not have default constructor");
					}
					catch (final IllegalAccessException e) {}
				}

				return null;
			}
		});

		AbstractJdbcAdaptor.LOG.debug("JDBC Driver discovery took {0} msecs", System.currentTimeMillis() - start);
	}

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractJdbcAdaptor() {
		super();
	}

	/**
	 * Creates the PhysicalColumn Definition DDL For the column.
	 * 
	 * @param column
	 *            the column definition to create the DDL from
	 * @return the PhysicalColumn Definition DDL For the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String createColumnDDL(AbstractColumn column);

	/**
	 * Returns the SQL to create the table.
	 * 
	 * @param jdbcAdapter
	 *            the JDBC Adapter to use
	 * @return the SQL to create the table
	 * 
	 * @since $version
	 * @author hceylan
	 * @param tableDefinition
	 */
	private String createCreateTableStatement(AbstractTable table) {
		final List<String> ddlColumns = Lists.newArrayList();
		final List<String> pkColumns = Lists.newArrayList();

		for (final AbstractColumn column : table.getColumns()) {
			ddlColumns.add(this.createColumnDDL(column));

			if (column instanceof PkPhysicalColumn) {
				pkColumns.add(column.getName());
			}
		}

		return this.createCreateTableStatement(table, ddlColumns, pkColumns);
	}

	/**
	 * Composes the SQL to create the table.
	 * 
	 * @param table
	 *            the table
	 * @param ddlColumns
	 *            the column DDL fragments
	 * @param pkColumns
	 *            the list of primary key columns
	 * @return the SQL to create the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String createCreateTableStatement(AbstractTable table, List<String> ddlColumns, List<String> pkColumns);

	/**
	 * Recreates the schema.
	 * <p>
	 * if DDL mode is DDLMode#DROP then first the schema is dropped. To keep track of whether a schema is previously dropped, schemas
	 * consulted.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param schemas
	 *            the set of schemas already dropped / created
	 * @param ddlMode
	 *            the DDL mode
	 * @param schema
	 *            the name of the current schema, may be null to indicate the default schema
	 * @return the name of the schema
	 * @throws SQLException
	 *             throw in case of of underlying SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String dropAndCreateSchemaIfNecessary(DataSource datasource, Set<String> schemas, DDLMode ddlMode, String schema)
		throws SQLException;

	/**
	 * @return the JDBC Driver this adapter works with
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String[] getJdbcDriverClassNames();

	/**
	 * Performs the DDL operation for the table.
	 * 
	 * @param schemas
	 *            the set of schemas created
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the DDL Mode
	 * @param table
	 *            the table
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final void performTableDdl(Set<String> schemas, DataSourceImpl datasource, DDLMode ddlMode, AbstractTable table)
		throws SQLException {

		this.dropAndCreateSchemaIfNecessary(datasource, schemas, ddlMode, table.getSchema());

		new QueryRunner(datasource).update(this.createCreateTableStatement(table));
	}
}

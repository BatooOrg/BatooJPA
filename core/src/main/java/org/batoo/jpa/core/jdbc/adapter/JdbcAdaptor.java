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
package org.batoo.jpa.core.jdbc.adapter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Locale;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.PkPhysicalColumn;
import org.batoo.jpa.core.impl.metamodel.SequenceGenerator;
import org.batoo.jpa.core.impl.metamodel.TableGenerator;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.parser.MappingException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Base class for JDBC Adapters.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class JdbcAdaptor extends AbstractJdbcAdaptor {

	private List<String> words;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public JdbcAdaptor() {
		super();

		this.loadReservedWords();
	}

	/**
	 * Creates the PhysicalColumn Definition DDL For the column.
	 * 
	 * @param columnDefinition
	 *            the column definition to create the DDL from
	 * @return the PhysicalColumn Definition DDL For the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public abstract String createColumnDDL(AbstractColumn columnDefinition);

	/**
	 * Returns the SQL to create the table.
	 * 
	 * @param table
	 *            the table
	 * @return the SQL to create the table
	 * 
	 * @since $version
	 * @author hceylan
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
	 * Creates the create table statement
	 * 
	 * @param table
	 *            the definition of the table
	 * @param ddlColumns
	 *            the DDL for the columns
	 * @param pkColumns
	 *            the list of primary key column names
	 * @return the generated column fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String createCreateTableStatement(AbstractTable table, List<String> ddlColumns, List<String> pkColumns) {
		final String columns = Joiner.on(",\n\t").join(ddlColumns) + ",";
		final String keys = Joiner.on(", ").join(pkColumns);

		return "CREATE TABLE " + table.getQName() + " (\n\t" // table part
			+ columns // columns part
			+ "\nPRIMARY KEY(" + keys + "))"; // primary key part
	}

	/**
	 * Creates the schema if necessary. The adapter should check if schema exists and create the schema if it does not exist.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param schema
	 *            the name of the schema
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void createSchemaIfNecessary(DataSource datasource, String schema) throws SQLException;

	/**
	 * Creates the sequence if not exists.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param sequence
	 *            the sequence to create
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) throws SQLException;

	/**
	 * @param table
	 *            the
	 * @param datasource
	 *            the datasource
	 * @throws SQLException
	 *             thrown if DDL table creation fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void createTable(AbstractTable table, DataSourceImpl datasource) throws SQLException {
		final String ddlSql = this.createCreateTableStatement(table);

		new QueryRunner(datasource).update(ddlSql);
	}

	/**
	 * Creates the table generator if not exists.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param table
	 *            the table generator
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void createTableGeneratorIfNecessary(DataSource datasource, TableGenerator table) throws SQLException;

	/**
	 * Drops the schema if exists
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param schema
	 *            the name of the schema
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void dropSchema(DataSource datasource, String schema) throws SQLException;

	/**
	 * Escapes an SQL name
	 * 
	 * @param name
	 *            the original name
	 * @return the escaped name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String escape(String name) {
		if (name == null) {
			return null;
		}

		if (this.words.contains(name.toUpperCase(Locale.ENGLISH))) {
			return name + "_";
		}

		return name;
	}

	/**
	 * Returns the data type of the column.
	 * 
	 * @param cd
	 *            the column definition
	 * @param sqlType
	 *            the sql type
	 * @return the data type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getColumnType(AbstractColumn cd, int sqlType) {
		switch (cd.getSqlType()) {
			case Types.BLOB:
				return "BLOB(" + cd.getLength() + ")";
			case Types.CLOB:
				return "CLOB(" + cd.getLength() + ")";
			case Types.VARCHAR:
				return "VARCHAR(" + cd.getLength() + ")";
			case Types.TIME:
				return "TIME";
			case Types.DATE:
				return "DATE";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.CHAR:
				return "CHAR";
			case Types.TINYINT:
			case Types.BOOLEAN:
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
				return "FLOAT" + (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + ")" : "");
			case Types.DOUBLE:
				return "DOUBLE" + (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + ")" : "");
			case Types.DECIMAL:
				return "DECIMAL"
					+ (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + (cd.getScale() > 0 ? "," + cd.getScale() : "") + ")" : "");
		}

		throw new IllegalArgumentException("Unhandled sql type: " + sqlType);
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String getDatabaseName();

	/**
	 * Returns the name of the default schema.
	 * 
	 * @param dataSource
	 *            the datasource to use
	 * @return the name of the default schema or null if database does not support schemas
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final String getDefaultSchema(DataSource dataSource) throws SQLException {
		final Connection connection = dataSource.getConnection();
		try {
			return connection.getSchema();
		}
		finally {
			DbUtils.closeQuietly(connection);
		}
	}

	/**
	 * Returns next sequence number from the database.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param sequenceName
	 *            the name of the sequence
	 * @return the next sequence number
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Integer getNextSequence(DataSourceImpl datasource, String sequenceName) throws SQLException;

	/**
	 * Returns the qualified name for the table.
	 * 
	 * @param catalog
	 *            the catalog of the table, maybe null or blank
	 * @param schema
	 *            the schema the table, maybe null or blank
	 * @param name
	 *            the name of the table, must be provided
	 * @return the qualified name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQualifiedName(String catalog, String schema, String name) {
		final List<String> segments = Lists.newArrayList();

		if (StringUtils.isNotBlank(catalog)) {
			segments.add(this.escape(catalog));
		}

		if (StringUtils.isNotBlank(schema)) {
			segments.add(this.escape(schema));
		}

		segments.add(this.escape(name));

		return Joiner.on(".").skipNulls().join(segments);
	}

	/**
	 * Returns the SQL to select the last identity generated.
	 * 
	 * @return the SQL to select the last identity generated
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getSelectLastIdentitySql();

	private void loadReservedWords() throws MappingException {
		final String packageName = this.getClass().getPackage().getName().replaceAll("\\.", "/");
		final String name = this.getDatabaseName();

		try {
			final String fileName = packageName + "/" + name.toLowerCase(Locale.ENGLISH) + ".words";
			final List<String> words = IOUtils.readLines(this.getClass().getClassLoader().getResourceAsStream(fileName));
			this.words = Lists.transform(words, new Function<String, String>() {

				@Override
				public String apply(String input) {
					return input.toUpperCase(Locale.ENGLISH);
				}
			});
		}
		catch (final IOException e) {
			throw new MappingException("Broken JDBC Adapter " + this.getClass().getSimpleName()
				+ ". Reserved words for the adapter cannot be loaded");
		}
	}

	/**
	 * Returns the schema if it is set otherwise falls back to the default schema.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param schema
	 *            the schema name
	 * @return the proper schema name
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String schemaOf(DataSource datasource, String schema) throws SQLException {
		if (StringUtils.isBlank(schema)) {
			schema = this.getDefaultSchema(datasource);
		}

		return schema;
	}

	/**
	 * Returns the id type supported.
	 * <p>
	 * If the idType is null, the adapter should suggest an {@link IdType} in return.
	 * <p>
	 * If the idType is some other value than null then the adapter should either return the same {@link IdType} if supported or null value
	 * in case the adapter does not support the specified {@link IdType}.
	 * 
	 * @param type
	 *            the id type
	 * @return the {@link IdType} selected, the {@link Id} passed or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract IdType supports(GenerationType type);
}

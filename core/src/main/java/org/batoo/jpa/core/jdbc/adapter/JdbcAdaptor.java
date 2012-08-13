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
package org.batoo.jpa.core.jdbc.adapter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.model.SequenceGenerator;
import org.batoo.jpa.core.impl.model.TableGenerator;
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
	 * Applies the concat operation to the arguments.
	 * 
	 * @param arguments
	 *            the arguments
	 * @return the concat SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String applyConcat(List<String> arguments);

	/**
	 * Appends the escape pattern.
	 * 
	 * @param escapePattern
	 *            the escape pattern
	 * @return the sql fragment to escape
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String applyLikeEscape(String escapePattern);

	/**
	 * Applies the lock to SQL string.
	 * 
	 * @param sql
	 *            the SQL to apply
	 * @param lockMode
	 *            the lock mode
	 * @return the modified SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String applyLock(String sql, LockModeType lockMode);

	/**
	 * Applies the pagination to SQL string.
	 * 
	 * @param sql
	 *            the SQL to apply
	 * @param startPosition
	 *            the start position
	 * @param maxResult
	 *            the max number of results
	 * @return the modified SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String applyPagination(String sql, int startPosition, int maxResult);

	/**
	 * Creates the BasicColumn Definition DDL For the column.
	 * 
	 * @param columnDefinition
	 *            the column definition to create the DDL from
	 * @return the BasicColumn Definition DDL For the column
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

		final Collection<AbstractColumn> columns = this.getColumns(table);

		for (final AbstractColumn column : columns) {
			ddlColumns.add(this.createColumnDDL(column));

			if (column instanceof PkColumn) {
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
	public String createCreateTableStatement(AbstractTable table, List<String> ddlColumns, List<String> pkColumns) {
		final String columns = Joiner.on(",\n\t").join(ddlColumns);
		final String keys = Joiner.on(", ").join(pkColumns);

		final StringBuilder statement = new StringBuilder();
		statement.append("CREATE TABLE ").append(table.getQName()).append(" (\n\t"); // table part
		statement.append(columns); // columns part

		if (StringUtils.isNotBlank(keys)) {
			statement.append(",");
			statement.append("\nPRIMARY KEY(").append(keys).append(")");
		}

		statement.append(")");

		return statement.toString();
	}

	/**
	 * Creates a foreign key on the table
	 * 
	 * @param datasource
	 *            the datasource
	 * @param foreignKey
	 *            the foreign key
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void createForeignKey(DataSource datasource, ForeignKey foreignKey) throws SQLException;

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
	 * Returns the sorted columns.
	 * 
	 * @param table
	 *            the table
	 * @return the sorted columns
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Collection<AbstractColumn> getColumns(AbstractTable table) {
		final List<AbstractColumn> columns = Lists.newArrayList(table.getColumns());

		Collections.sort(columns, new Comparator<AbstractColumn>() {

			@Override
			public int compare(AbstractColumn o1, AbstractColumn o2) {
				if ((o1 instanceof PkColumn) && !(o2 instanceof PkColumn)) {
					return -1;
				}

				if ((o2 instanceof PkColumn) && !(o1 instanceof PkColumn)) {
					return 1;
				}

				return o1.getName().compareTo(o2.getName());
			}
		});

		return columns;
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
		switch (sqlType) {
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
			case Types.BOOLEAN:
				return "BOOLEAN";
			case Types.TINYINT:
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
				return "DECIMAL" + (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + (cd.getScale() > 0 ? "," + cd.getScale() : "") + ")" : "");
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
	public abstract long getNextSequence(DataSourceImpl datasource, String sequenceName) throws SQLException;

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
	 * @param identityColumn
	 *            the identity column
	 * @return the SQL to select the last identity generated
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getSelectLastIdentitySql(PkColumn identityColumn);

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
			throw new MappingException("Broken JDBC Adapter " + this.getClass().getSimpleName() + ". Reserved words for the adapter cannot be loaded");
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

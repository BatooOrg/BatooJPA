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
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder.Trimspec;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.expression.NumericFunctionExpression.NumericFunctionType;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
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

	private static final BLogger LOG = BLoggerFactory.getLogger(JdbcAdaptor.class);

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
	 * Returns the sub string function.
	 * 
	 * @param innerFragment
	 *            the inner fragment
	 * @param startFragment
	 *            the start fragment
	 * @param endFragment
	 *            the end fragment
	 * @return the sub string function
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String applySubStr(String innerFragment, String startFragment, String endFragment) {
		return "SUBSTR(" + Joiner.on(", ").skipNulls().join(new Object[] { innerFragment, startFragment, endFragment }) + ")";
	}

	/**
	 * Applies the trim to argument
	 * 
	 * @param trimspec
	 *            the trim spec
	 * @param trimChar
	 *            the trim character
	 * @param argument
	 *            the argument
	 * @return the trim SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String applyTrim(Trimspec trimspec, String trimChar, String argument) {
		final StringBuilder builder = new StringBuilder("TRIM(");

		if (trimspec != null) {
			builder.append(trimspec.toString()).append(" ");
		}

		if (trimChar != null) {
			builder.append(trimChar).append(" ");
		}

		if ((trimspec != null) || (trimChar != null)) {
			builder.append("FROM ");
		}

		return builder.append(argument).append(")").toString();
	}

	/**
	 * Casts the expression to boolean if necessary.
	 * 
	 * @param sqlFragment
	 *            the SQL Fragment
	 * @return the cast SQL fragment
	 */
	public String castBoolean(String sqlFragment) {
		return sqlFragment;
	}

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

			if (column.isPrimaryKey()) {
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void createForeignKey(DataSource datasource, ForeignKey foreignKey) {
		final String referenceTableName = foreignKey.getReferencedTableName();
		final String tableName = foreignKey.getTable().getName();

		final String foreignKeyColumns = Joiner.on(", ").join(Lists.transform(foreignKey.getJoinColumns(), new Function<JoinColumn, String>() {

			@Override
			public String apply(JoinColumn input) {
				return input.getReferencedColumnName();
			}
		}));

		final String keyColumns = Joiner.on(", ").join(Lists.transform(foreignKey.getJoinColumns(), new Function<JoinColumn, String>() {

			@Override
			public String apply(JoinColumn input) {
				return input.getName();
			}
		}));

		final String sql = "ALTER TABLE " + tableName //
			+ "\n\tADD CONSTRAINT " + foreignKey.getName() + " FOREIGN KEY (" + keyColumns + ")" //
			+ "\n\tREFERENCES " + referenceTableName + "(" + foreignKeyColumns + ")";

		try {
			new QueryRunner(datasource, this.isPmdBroken()).update(sql);
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create foreign key.");
		}
	}

	/**
	 * Creates the index for the table.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param table
	 *            the table
	 * @param indexName
	 *            the name of the index
	 * @param columns
	 *            the columns
	 * @throws SQLException
	 *             throw in case index creation fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void createIndex(DataSourceImpl datasource, EntityTable table, String indexName, BasicColumn[] columns) throws SQLException {
		final String columnNames = Joiner.on(", ").join(Lists.transform(Lists.newArrayList(columns), new Function<BasicColumn, String>() {

			@Override
			public String apply(BasicColumn input) {
				return input.getName();
			}
		}));

		new QueryRunner(datasource, this.isPmdBroken()).update("CREATE INDEX " + indexName + " ON " + table.getQName() + "(" + columnNames + ")");
	}

	/**
	 * Creates the sequence if not exists.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param sequence
	 *            the sequence to create
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence);

	/**
	 * @param table
	 *            the
	 * @param datasource
	 *            the datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void createTable(AbstractTable table, DataSourceImpl datasource) {
		try {
			new QueryRunner(datasource, this.isPmdBroken()).update(this.createCreateTableStatement(table));
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create table " + table.getName());
		}

		if (table instanceof EntityTable) {
			final Map<String, BasicColumn[]> indexes = ((EntityTable) table).getIndexes();
			for (final Entry<String, BasicColumn[]> entry : indexes.entrySet()) {
				try {
					this.createIndex(datasource, (EntityTable) table, entry.getKey(), entry.getValue());
				}
				catch (final SQLException e) {
					JdbcAdaptor.LOG.warn(e, "Cannot create index {0}", entry.getKey());
				}
			}
		}
	}

	/**
	 * Creates the table generator if not exists.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * @param table
	 *            the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final void createTableGeneratorIfNecessary(DataSource datasource, TableGenerator table) {
		final String sql = "CREATE TABLE " + table.getQName() + " ("//
			+ "\n\t" + table.getPkColumnName() + " VARCHAR(255)," //
			+ "\n\t" + table.getValueColumnName() + " INT," //
			+ "\nPRIMARY KEY(" + table.getPkColumnName() + "))";

		try {
			new QueryRunner(datasource, this.isPmdBroken()).update(sql);
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create tabe generator " + table.getTable());
		}
	}

	/**
	 * @param datasource
	 *            the datasource
	 * @param foreignKeys
	 *            the foreign keys
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void dropAllForeignKeys(DataSourceImpl datasource, Set<ForeignKey> foreignKeys) {
		for (final ForeignKey key : foreignKeys) {
			this.dropForeignKey(datasource, key);
		}
	}

	/**
	 * @param datasource
	 *            the datasource
	 * @param sequences
	 *            the sequences
	 * @throws SQLException
	 *             thrown if the SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void dropAllSequences(DataSourceImpl datasource, Collection<SequenceGenerator> sequences) throws SQLException {
		final QueryRunner runner = new QueryRunner(datasource, this.isPmdBroken());

		for (final SequenceGenerator sequence : sequences) {
			try {
				this.dropSequence(runner, sequence);
			}
			catch (final SQLException e) {
				this.logRelaxed(e, "Cannot drop sequence.");
			}
		}
	}

	/**
	 * Drops the tables in the database
	 * 
	 * @param datasource
	 *            the datasource
	 * @param tables
	 *            the set of tables to drop
	 * @throws SQLException
	 *             thrown if the SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void dropAllTables(DataSource datasource, Collection<AbstractTable> tables) throws SQLException {
		final QueryRunner runner = new QueryRunner(datasource, this.isPmdBroken());

		for (final AbstractTable table : tables) {
			try {
				runner.update("DROP TABLE " + table.getQName());
			}
			catch (final SQLException e) {
				this.logRelaxed(e, "Cannot drop table " + table.getName());
			}
		}
	}

	/**
	 * @param datasource
	 *            the datasource
	 * @param foreignKey
	 *            the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void dropForeignKey(DataSourceImpl datasource, ForeignKey foreignKey) {
		try {
			new QueryRunner(datasource, this.isPmdBroken()).update("ALTER TABLE " + foreignKey.getTable().getQName() + " DROP FOREIGN KEY "
				+ foreignKey.getName());
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot drop foreign key " + foreignKey.getName());
		}
	}

	/**
	 * Drops the sequence.
	 * 
	 * @param runner
	 *            the runner
	 * @param sequence
	 *            the sequence
	 * @throws SQLException
	 *             thrown if SQL fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void dropSequence(final QueryRunner runner, final SequenceGenerator sequence) throws SQLException {
		runner.update("DROP SEQUENCE " + sequence.getQName());
	}

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
				if (o1.isPrimaryKey() && !o2.isPrimaryKey()) {
					return -1;
				}

				if (o2.isPrimaryKey() && !o1.isPrimaryKey()) {
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
	 * Returns the current date literal
	 * 
	 * @return the current date literal.
	 * @since $version
	 * @author hceylan
	 */
	public String getCurrentDate() {
		return "CURRENT_DATE";
	}

	/**
	 * Returns the current time literal
	 * 
	 * @return the current time literal.
	 * @since $version
	 * @author hceylan
	 */
	public String getCurrentTime() {
		return "CURRENT_TIME";
	}

	/**
	 * Returns the current time stamp literal
	 * 
	 * @return the current time stamp literal.
	 * @since $version
	 * @author hceylan
	 */
	public String getCurrentTimeStamp() {
		return "CURRENT_TIMESTAMP";
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String getDatabaseName();

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
	 * Returns the numeric function template.
	 * 
	 * @param type
	 *            the id type
	 * @return the {@link IdType} selected, the {@link Id} passed or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getNumericFunctionTemplate(NumericFunctionType type) {
		switch (type) {
			case ABS:
				return "ABS({0})";
			case LENGTH:
				return "LENGTH({0})";
			case MOD:
				return "MOD({0}, {1})";
			default:
				return "SQRT({0})";
		}
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

	/**
	 * Returns if the PMD is Broken for the adaptor.
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public boolean isPmdBroken() {
		return false;
	}

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
	 * Logs the message as warning and with exception as debug.
	 * 
	 * @param e
	 *            the sql exception
	 * @param message
	 *            the message
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void logRelaxed(SQLException e, String message) {
		JdbcAdaptor.LOG.warn(message + " Check debug log for details: " + e.getMessage());
		JdbcAdaptor.LOG.debug(e, message);
	}

	/**
	 * Returns the schema if it is set otherwise falls back to the default schema.
	 * 
	 * @param schema
	 *            the schema name
	 * @param jdbcClassName
	 *            the name of the table or sequence
	 * @return the proper schema name
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String qualified(String schema, String jdbcClassName) {
		if (StringUtils.isBlank(schema)) {
			return jdbcClassName;
		}

		return schema + "." + jdbcClassName;
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

	/**
	 * Returns if the adaptor supports the start position.
	 * 
	 * @return true if the adaptor supports the start position, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean supportsStartPosition() {
		return true;
	}
}

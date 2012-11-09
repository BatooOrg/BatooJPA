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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.common.util.BatooUtils;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.expression.DateTimeFunctionType;
import org.batoo.jpa.core.impl.criteria.expression.NumericFunctionType;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.CollectionTable;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.model.SequenceGenerator;
import org.batoo.jpa.core.impl.model.TableGenerator;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.parser.MappingException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Base class for JDBC Adapters.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class JdbcAdaptor extends AbstractJdbcAdaptor {

	/**
	 * The order of pagination paramaters embedded into the SQL.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	@SuppressWarnings("javadoc")
	public enum PaginationParamsOrder {
		SQL_START_MAX(true),

		SQL_MAX_START(true),

		SQL_START_END(true),

		SQL_END_START(true),

		MAX_START_SQL(false),

		START_MAX_SQL(false);

		private final boolean afterMainSql;

		/**
		 * @since $version
		 * @author hceylan
		 */
		private PaginationParamsOrder(boolean afterMainSql) {
			this.afterMainSql = afterMainSql;
		}

		/**
		 * Returns if the pagination params come after the main SQL.
		 * 
		 * @return the if if the pagination params come after the main SQL, false otherwise
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public boolean isAfterMainSql() {
			return this.afterMainSql;
		}
	}

	private static final String[] TABLE_OR_VIEW = new String[] { "TABLE", "VIEW" };
	private static final String TABLE_NAME = "TABLE_NAME";

	private static final BLogger LOG = BLoggerFactory.getLogger(JdbcAdaptor.class);

	private List<String> words;

	private final Map<AbstractTable, JdbcTable> tables = Maps.newHashMap();

	private int insertBatchSize;
	private int removeBatchSize;

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
	 * Returns the SQL to alter the table.
	 * 
	 * @param table
	 *            the table
	 * @param columnsToAdd
	 *            the list of columns to add
	 * @return the SQL to alter the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String createAlterTableStatement(AbstractTable table, List<AbstractColumn> columnsToAdd) {
		final List<String> ddlColumns = Lists.newArrayList();

		for (final AbstractColumn column : columnsToAdd) {
			ddlColumns.add("ADD COLUMN " + this.createColumnDDL(column));
		}

		final StringBuilder statement = new StringBuilder();
		statement.append("ALTER TABLE ").append(table.getQName()).append("\n\t"); // table part
		statement.append(Joiner.on("\n\t").join(ddlColumns)); // columns part

		return statement.toString();
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
	 * @throws SQLException
	 *             thrown if there is repetitive column names with different DDL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String createCreateTableStatement(AbstractTable table) throws SQLException {
		final Map<String, String> ddlColumns = Maps.newHashMap();
		final List<String> pkColumns = Lists.newArrayList();

		final Collection<AbstractColumn> columns = this.getColumns(table);

		for (final AbstractColumn column : columns) {
			final String columnDDL = this.createColumnDDL(column);
			final String existingColumnDDL = ddlColumns.get(column.getName());
			if ((existingColumnDDL != null) && !existingColumnDDL.equals(columnDDL)) {
				throw new SQLException("Table " + table.getName() + " has two columns with same name '" + column.getName() + "' but different DDL");
			}
			ddlColumns.put(column.getName(), columnDDL);

			if (column.isPrimaryKey()) {
				pkColumns.add(column.getName());
			}
		}

		return this.createCreateTableStatement(table, ddlColumns.values(), pkColumns);
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
	public String createCreateTableStatement(AbstractTable table, Collection<String> ddlColumns, List<String> pkColumns) {
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
		final QueryRunner runner = new QueryRunner(datasource, this.isPmdBroken());

		try {
			// locate the foreign key metada
			final JdbcTable tableMetadata = this.getTableMetadata(datasource, foreignKey.getTable());
			if (tableMetadata == null) {
				JdbcAdaptor.LOG.warn("Foreign key {0} cannot be created, table not found: ", foreignKey);
				return;
			}

			final JdbcForeignKey foreignKeyMetadata = tableMetadata.getForeignKey(foreignKey.getName());

			// if it exists, then if there is no change then bail out, otherwise drop and continue with the creation
			if (foreignKeyMetadata != null) {
				if (!foreignKeyMetadata.matches(foreignKey)) {
					final String sql = this.getDropForeignKeySql(tableMetadata.getSchema(), tableMetadata.getName(), foreignKey.getName());

					runner.update(sql);
				}
				else {
					return;
				}
			}

			// create the foreign key
			final String referenceTableName = foreignKey.getReferencedTableQName();
			final String tableName = foreignKey.getTable().getQName();

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

			runner.update(sql);
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot (re)create foreign key.");
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
	protected void createIndex(DataSource datasource, EntityTable table, String indexName, BasicColumn[] columns) throws SQLException {
		final String columnNames = Joiner.on(", ").join(Lists.transform(Lists.newArrayList(columns), new Function<BasicColumn, String>() {

			@Override
			public String apply(BasicColumn input) {
				return input.getName();
			}
		}));

		new QueryRunner(datasource, this.isPmdBroken()).update("CREATE INDEX " + indexName + " ON " + table.getQName() + "(" + columnNames + ")");
	}

	private void createIndexes(DataSource datasource, AbstractTable table) {
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
	 * Creates or update the table.
	 * 
	 * @param table
	 *            the table
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the ddl mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void createOrUpdateTable(AbstractTable table, DataSource datasource, DDLMode ddlMode) {
		try {
			if ((ddlMode == DDLMode.DROP) || (ddlMode == DDLMode.CREATE)) {
				final JdbcTable tableMetadata = this.getTableMetadata(datasource, table);
				if (tableMetadata == null) {
					this.createTable(datasource, table);
				}
			}
			else if (ddlMode == DDLMode.UPDATE) {
				final JdbcTable tableMetadata = this.getTableMetadata(datasource, table);
				if (tableMetadata == null) {
					this.createTable(datasource, table);
				}
				else {
					this.updateTable(datasource, table);
				}
			}
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Table DDL Failed for table " + table.getQName());
		}
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

	private void createTable(DataSource datasource, AbstractTable table) {
		try {
			new QueryRunner(datasource, this.isPmdBroken()).update(this.createCreateTableStatement(table));
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create table " + table.getQName());
		}

		this.createIndexes(datasource, table);
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
		try {
			if (this.getTableMetadata(datasource, table.getCatalog(), table.getSchema(), table.getTable()) == null) {

				final String sql = "CREATE TABLE " + table.getQName() + " ("//
					+ "\n\t" + table.getPkColumnName() + " VARCHAR(255)," //
					+ "\n\t" + table.getValueColumnName() + " INT," //
					+ "\nPRIMARY KEY(" + table.getPkColumnName() + "))";

				new QueryRunner(datasource, this.isPmdBroken()).update(sql);
			}
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create table generator " + table.getTable());
		}
	}

	/**
	 * @param datasource
	 *            the datasource
	 * @param tableSet
	 *            the foreign keys
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void dropAllForeignKeys(DataSource datasource, Set<AbstractTable> tableSet) {
		final AbstractTable[] tables = tableSet.toArray(new AbstractTable[tableSet.size()]);

		try {
			// Order tables by dependency
			Arrays.sort(tables, new Comparator<AbstractTable>() {

				@Override
				public int compare(AbstractTable o1, AbstractTable o2) {
					if ((o1 instanceof JoinTable) && !(o2 instanceof JoinTable)) {
						return -1;
					}

					if ((o2 instanceof JoinTable) && !(o1 instanceof JoinTable)) {
						return 1;
					}

					if ((o1 instanceof CollectionTable) && !(o2 instanceof CollectionTable)) {
						return -1;
					}

					if ((o2 instanceof CollectionTable) && !(o1 instanceof CollectionTable)) {
						return 1;
					}

					if ((o1 instanceof SecondaryTable) && !(o2 instanceof SecondaryTable)) {
						return -1;
					}

					if ((o2 instanceof SecondaryTable) && !(o1 instanceof SecondaryTable)) {
						return 1;
					}

					for (final ForeignKey key : o1.getForeignKeys()) {
						if (key.getReferencedTableQName().equals(o2.getQName())) {
							return 1;
						}
					}

					for (final ForeignKey key : o2.getForeignKeys()) {
						if (key.getReferencedTableQName().equals(o2.getQName())) {
							return -1;
						}
					}

					return 0;
				}
			});
		}
		catch (final IllegalArgumentException e) {
			JdbcAdaptor.LOG.warn(e, "");
		}

		for (final AbstractTable table : tables) {
			JdbcTable tableMetadata = null;
			try {
				tableMetadata = this.getTableMetadata(datasource, table);
			}
			catch (final SQLException e) {
				this.logRelaxed(e, "Cannot drop foreign keys for table " + table.getName());
			}

			if (tableMetadata != null) {
				for (final JdbcForeignKey foreignKey : tableMetadata.getForeignKeys()) {
					try {
						new QueryRunner(datasource, this.isPmdBroken()).update(this.getDropForeignKeySql(table.getSchema(), table.getName(),
							foreignKey.getName()));
					}
					catch (final SQLException e) {
						this.logRelaxed(e, "Cannot drop foreign key " + foreignKey.getName());
					}
				}
			}
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
	public void dropAllSequences(DataSource datasource, Collection<SequenceGenerator> sequences) throws SQLException {
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
				final JdbcTable tableMetadata = this.getTableMetadata(datasource, table);
				this.tables.remove(table);

				if (tableMetadata != null) {
					this.dropTable(runner, table);
				}
			}
			catch (final SQLException e) {
				this.logRelaxed(e, "Cannot drop table " + table.getName());
			}
		}

		try {
			runner.update("DROP TABLE BATOO_ID");
		}
		catch (final SQLException e) {}
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
	 * Drops the table
	 * 
	 * @param runner
	 *            the runner
	 * @param table
	 *            the table
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void dropTable(final QueryRunner runner, final AbstractTable table) throws SQLException {
		runner.update("DROP TABLE " + table.getQName());
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
	 * Returns the date time function template for the type <code>type</code>.
	 * 
	 * @param type
	 *            the type of the function
	 * @return the date time function template for the type <code>type</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getDateTimeFunctionTemplate(DateTimeFunctionType type) {
		switch (type) {
			case SECOND:
				return "SECOND({0})";

			case MINUTE:
				return "MINUTE({0})";

			case HOUR:
				return "HOUR({0})";

			case DAYOFMONTH:
				return "DAY_OF_MONTH({0})";

			case DAYOFWEEK:
				return "DAY_OF_WEEK({0})";

			case DAYOFYEAR:
				return "DAY_OF_YEAR({0})";

			case MONTH:
				return "MONTH({0})";

			case WEEK:
				return "WEEK({0})";

			default: // YEAR
				return "YEAR({0})";
		}
	}

	/**
	 * Returns the SQL to drop the foreign key.
	 * 
	 * @param schema
	 *            the name of the schema
	 * @param table
	 *            the name of the table
	 * @param foreignKey
	 *            the name of the foreign key
	 * @return the SQL to drop the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getDropForeignKeySql(String schema, String table, String foreignKey) {
		final String qualifiedName = Joiner.on(".").skipNulls().join(schema, table);

		return "ALTER TABLE " + qualifiedName + " DROP FOREIGN KEY " + foreignKey;
	}

	/**
	 * Returns the insertBatchSize of the JdbcAdaptor.
	 * 
	 * @return the insertBatchSize of the JdbcAdaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getInsertBatchSize() {
		return this.insertBatchSize;
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
	public abstract long getNextSequence(DataSource datasource, String sequenceName) throws SQLException;

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
	 * Returns the pagination params order for the adaptor.
	 * 
	 * @return the pagination params order for the adaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract PaginationParamsOrder getPaginationParamsOrder();

	/**
	 * Returns the priary key drop SQL.
	 * 
	 * @param schema
	 *            the name of the schema
	 * @param table
	 *            the name of the table
	 * @param pkColumns
	 *            the set of the primary key column names
	 * @return the priary key drop SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getPkCreateSql(String schema, String table, Set<String> pkColumns) {
		final String qualifiedName = Joiner.on(".").skipNulls().join(new String[] { schema, table });

		return "ALTER TABLE " + qualifiedName + " ADD PRIMARY KEY (" + Joiner.on(", ").join(pkColumns) + ")";
	}

	/**
	 * Returns the priary key drop SQL.
	 * 
	 * @param schema
	 *            the name of the schema
	 * @param table
	 *            the name of the table
	 * @param pkName
	 *            the name of the primary key
	 * @return the priary key drop SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getPkDropSql(String schema, String table, String pkName) {
		final String qualifiedName = Joiner.on(".").skipNulls().join(new String[] { schema, table });

		return "ALTER TABLE " + qualifiedName + " DROP PRIMARY KEY";
	}

	/**
	 * Returns the removeBatchSize of the JdbcAdaptor.
	 * 
	 * @return the removeBatchSize of the JdbcAdaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getRemoveBatchSize() {
		return this.removeBatchSize;
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

	private synchronized JdbcTable getTableMetadata(DataSource datasource, AbstractTable table) throws SQLException {
		JdbcTable tableMetadata = this.tables.get(table);
		if (tableMetadata != null) {
			return tableMetadata;
		}

		tableMetadata = this.getTableMetadata(datasource, table.getCatalog(), table.getSchema(), table.getName());
		if (tableMetadata != null) {
			this.tables.put(table, tableMetadata);
		}

		return tableMetadata;
	}

	private JdbcTable getTableMetadata(DataSource datasource, String catalog, String schema, String table) throws SQLException {
		final Connection connection = datasource.getConnection();

		ResultSet tables = null;
		try {
			final DatabaseMetaData dbMetadata = connection.getMetaData();
			if (StringUtils.isBlank(catalog)) {
				catalog = null;
			}
			if (StringUtils.isBlank(schema)) {
				schema = null;
			}

			if (dbMetadata.storesUpperCaseIdentifiers()) {
				tables = dbMetadata.getTables(//
					BatooUtils.upper(catalog), //
					BatooUtils.upper(schema), //
					BatooUtils.upper(table),//
					JdbcAdaptor.TABLE_OR_VIEW);
			}
			else if (dbMetadata.storesLowerCaseIdentifiers()) {
				tables = dbMetadata.getTables(//
					BatooUtils.lower(catalog), //
					BatooUtils.lower(schema), //
					BatooUtils.lower(table),//
					JdbcAdaptor.TABLE_OR_VIEW);
			}
			else {
				tables = dbMetadata.getTables(catalog, schema, table, JdbcAdaptor.TABLE_OR_VIEW);
			}

			if (tables.next()) {
				final String tableName = tables.getString(JdbcAdaptor.TABLE_NAME);
				if (table.equalsIgnoreCase(tableName)) {
					return new JdbcTable(dbMetadata, tables);
				}
			}
		}
		finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(tables);
		}

		return null;
	}

	/**
	 * Executes the initial import sql.
	 * 
	 * @param classLoader
	 *            the class loader
	 * @param dataSource
	 *            the datasource
	 * @param importSqlFileName
	 *            the name of the import sql
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void importSql(ClassLoader classLoader, DataSource dataSource, String importSqlFileName) {
		// no import sql
		if (StringUtils.isBlank(importSqlFileName)) {
			return;
		}

		final InputStream is = classLoader.getResourceAsStream(importSqlFileName);
		if (is == null) {
			JdbcAdaptor.LOG.error("Cannot load the import sql resource: {0}", importSqlFileName);

			return;
		}

		final String sql;
		try {
			sql = IOUtils.toString(is);
		}
		catch (final Exception e) {
			JdbcAdaptor.LOG.error(e, "Cannot load the import sql resource: {0}", importSqlFileName);

			return;
		}
		finally {
			try {
				is.close();
			}
			catch (final IOException e) {}
		}

		JdbcAdaptor.LOG.info("Executing import sql: {0}", importSqlFileName);

		try {
			final Connection connection = dataSource.getConnection();

			try {
				connection.setAutoCommit(false);

				final Statement statement = connection.createStatement();
				try {
					final BufferedReader reader = new BufferedReader(new StringReader(sql));

					String line = null;
					while ((line = reader.readLine()) != null) {
						final String sqlLine = line.trim();

						if (sqlLine.startsWith("--")) {
							continue;
						}
						else if (sqlLine.startsWith("//")) {
							continue;
						}
						else if (sqlLine.startsWith("/*")) {
							continue;
						}

						final Iterator<String> statements = Splitter.on(";").omitEmptyStrings().split(sqlLine).iterator();

						while (statements.hasNext()) {
							try {
								statement.execute(statements.next());
							}
							catch (final SQLException e) {
								JdbcAdaptor.LOG.error("Error executing sql import fragment: {0}", sqlLine);

								throw e;
							}
						}
					}

					connection.setAutoCommit(true);
				}
				finally {
					DbUtils.closeQuietly(statement);
				}
			}
			finally {
				DbUtils.closeQuietly(connection);
			}
		}
		catch (final Exception e) {
			JdbcAdaptor.LOG.error(e, "Error executing import sql: {0}", importSqlFileName);
		}

		JdbcAdaptor.LOG.info("Import successful.");
	}

	/**
	 * Returns if the PMD is Broken for the adaptor.
	 * 
	 * @return true if the PMD is Broken for the adaptor, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isPmdBroken() {
		return true;
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
	 * Returns if pagination always needs the max results paramater.
	 * 
	 * @return true if pagination always needs the max results paramater, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean paginationNeedsMaxResultsAlways();

	/**
	 * Returns if pagination always needs the start paramater.
	 * 
	 * @return true if pagination always needs the start paramater, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean paginationNeedsStartAlways();

	/**
	 * Returns if the paginated sql uses parameters instead of hard coded pagination variables.
	 * 
	 * @return true if the paginated sql uses parameters instead of hard coded pagination variables, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean parameterizedPagination() {
		return true;
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
	 * Sets the insertBatchSize of the JdbcAdaptor.
	 * 
	 * @param insertBatchSize
	 *            the insertBatchSize to set for JdbcAdaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setInsertBatchSize(int insertBatchSize) {
		this.insertBatchSize = insertBatchSize;
	}

	/**
	 * Sets the removeBatchSize of the JdbcAdaptor.
	 * 
	 * @param removeBatchSize
	 *            the removeBatchSize to set for JdbcAdaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setRemoveBatchSize(int removeBatchSize) {
		this.removeBatchSize = removeBatchSize;
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

	private void updateTable(DataSource datasource, AbstractTable table) {
		final QueryRunner runner = new QueryRunner(datasource, this.isPmdBroken());

		try {
			// locate the existing table metadata
			final JdbcTable tableMetadata = this.getTableMetadata(datasource, table);

			final Set<String> columnsFound = Sets.newHashSet();
			final List<AbstractColumn> columnsToAdd = Lists.newArrayList();

			// iterate over columns to find out the columns to add
			for (final AbstractColumn column : table.getColumns()) {
				final JdbcColumn columnMetadata = tableMetadata.getColumn(column.getName());
				if (columnMetadata == null) {
					columnsToAdd.add(column);
				}
				else {
					columnsFound.add(column.getName());
				}
			}

			// log warning for the non null columns that are not in the persistence unit
			tableMetadata.logNotNullExtraColumns(table.getColumnNames());

			boolean pkDropped = false;

			// if primary key has changed then drop the primary key
			if (tableMetadata.requiresPkDrop(table.getPkColumnNames())) {
				try {
					runner.update(this.getPkDropSql(tableMetadata.getSchema(), table.getName(), tableMetadata.getPkName()));
					pkDropped = true;
				}
				catch (final SQLException e) {
					JdbcAdaptor.LOG.error(e, "Cannot drop the primary key for table {0}. Primary key changes will not be reflected!", table.getName());
				}
			}

			// add the new columns
			if (columnsToAdd.size() > 0) {
				runner.update(this.createAlterTableStatement(table, columnsToAdd));
			}

			// create the missing indexes
			this.createIndexes(datasource, table);

			// if primary key is dropped then recreate the primary key if necessary
			if (pkDropped) {
				runner.update(this.getPkCreateSql(tableMetadata.getSchema(), table.getName(), table.getPkColumnNames()));
			}
		}
		catch (final SQLException e) {
			JdbcAdaptor.LOG.error(e, "Unable to update table {0}", table.getName());
		}
	}
}

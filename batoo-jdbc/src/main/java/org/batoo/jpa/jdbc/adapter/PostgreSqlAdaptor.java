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
package org.batoo.jpa.jdbc.adapter;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.persistence.GenerationType;
import javax.persistence.LockModeType;
import javax.sql.DataSource;

import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.AbstractTable;
import org.batoo.jpa.jdbc.BasicColumn;
import org.batoo.jpa.jdbc.IdType;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.jdbc.generator.SequenceGenerator;

import com.google.common.base.Joiner;

/**
 * JDBC Adapter for PostgreSQL.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PostgreSqlAdaptor extends JdbcAdaptor {

	private static final String[] PRODUCT_NAMES = new String[] { "PostgreSQL" };

	/**
	 * 
	 * @since 2.0.0
	 */
	public PostgreSqlAdaptor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyConcat(List<String> arguments) {
		return Joiner.on(" || ").join(arguments);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyLikeEscape(String escapePattern) {
		return " {ESCAPE " + escapePattern + "}";

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyLock(String sql, LockModeType lockMode) {
		switch (lockMode) {
			case PESSIMISTIC_FORCE_INCREMENT:
			case PESSIMISTIC_READ:
				return sql + "\nFOR READ ONLY";
			case PESSIMISTIC_WRITE:
				return sql + "\nFOR UPDATE";
			default:
				break;
		}

		return sql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyPagination(String sql, int startPosition, int maxResult) {
		if (startPosition != 0) {
			sql = sql + "\nOFFSET " + startPosition + " ROWS";
		}

		if (maxResult != Integer.MAX_VALUE) {
			sql = sql + "\nFETCH FIRST " + maxResult + " ROWS ONLY";
		}

		return sql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String createColumnDDL(AbstractColumn column) {
		return column.getName() + " " // name part
			+ this.getColumnType(column, column.getSqlType()) // data type part
			+ (!column.isNullable() ? " NOT NULL" : "") // not null part
			+ (column.isUnique() ? " UNIQUE" : ""); // not null part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) {
		final String sql = "CREATE SEQUENCE " //
			+ sequence.getSequenceName() // ;
			+ " START WITH " + sequence.getInitialValue() //
			+ " INCREMENT BY " + sequence.getAllocationSize();

		try {
			new QueryRunner(datasource).update(sql);
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create sequence " + sequence.getName());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void dropTable(QueryRunner runner, AbstractTable table) throws SQLException {
		runner.update("DROP TABLE " + table.getQName() + " CASCADE");
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
	 * @since 2.0.0
	 */
	@Override
	protected String getColumnType(AbstractColumn cd, int sqlType) {
		if (cd.getIdType() == IdType.IDENTITY) {
			return "SERIAL";
		}

		switch (sqlType) {
			case Types.BLOB:
			case Types.CLOB:
				return "BYTEA";
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
				return "DOUBLE PRECISION" + (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + ")" : "");
			case Types.DECIMAL:
				return "DECIMAL" + (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + (cd.getScale() > 0 ? "," + cd.getScale() : "") + ")" : "");
		}

		throw new IllegalArgumentException("Unhandled sql type: " + sqlType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDatabaseName() {
		return "PgSql";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDropForeignKeySql(String schema, String table, String foreignKey) {
		final String qualifiedName = Joiner.on(".").skipNulls().join(schema, table);

		return "ALTER TABLE " + qualifiedName + " DROP CONSTRAINT " + foreignKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public long getNextSequence(DataSource datasource, String sequenceName) throws SQLException {
		return new QueryRunner(datasource) //
		.query("SELECT NEXTVAL('" + sequenceName + "')", new SingleValueHandler<Number>()).longValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PaginationParamsOrder getPaginationParamsOrder() {
		return PaginationParamsOrder.SQL_START_MAX;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String[] getProductNames() {
		return PostgreSqlAdaptor.PRODUCT_NAMES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSelectLastIdentitySql(BasicColumn identityColumn) {
		return "SELECT CURRVAL('" + identityColumn.getTable().getQName() + "_" + identityColumn.getName() + "_seq')";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean paginationNeedsMaxResultsAlways() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean paginationNeedsStartAlways() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean parameterizedPagination() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdType supports(GenerationType type) {
		if (type == null) {
			return IdType.SEQUENCE;
		}

		switch (type) {
			case IDENTITY:
				return IdType.IDENTITY;
			case SEQUENCE:
				return IdType.SEQUENCE;
			case TABLE:
				return IdType.TABLE;
			default:
				return IdType.SEQUENCE;
		}
	}
}

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

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.persistence.GenerationType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder.Trimspec;
import javax.sql.DataSource;

import org.batoo.jpa.core.impl.criteria.expression.NumericFunctionType;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.model.SequenceGenerator;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.util.BatooUtils;

import com.google.common.base.Joiner;

/**
 * JDBC Adapter for MySQL.
 * 
 * @author hceylan
 * @since $version
 */
public class MsSqlAdaptor extends JdbcAdaptor {

	private static final String[] PRODUCT_NAMES = new String[] { "Microsoft SQL Server" };

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MsSqlAdaptor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyConcat(List<String> arguments) {
		return "(" + Joiner.on(" + ").join(arguments) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyLikeEscape(String escapePattern) {
		return " ESCAPE " + escapePattern;
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
		if (startPosition == 0) {
			return sql.replaceFirst("SELECT", "SELECT TOP " + maxResult);
		}

		sql = BatooUtils.indent(sql);

		final StringBuffer sqlStr = new StringBuffer(sql);
		final int orderIndex = sqlStr.indexOf("ORDER BY");
		final CharSequence orderby = orderIndex > -1 ? sqlStr.subSequence(orderIndex, sqlStr.length()) : "ORDER BY CURRENT_TIMESTAMP";
		if (orderIndex > -1) {
			sqlStr.delete(orderIndex, sqlStr.length());
		}

		final int fromIndex = sqlStr.indexOf("FROM");
		sqlStr.insert(fromIndex, "\t, ROW_NUMBER() OVER (" + orderby + ") AS ROW_NUM__INTERNAL ");

		if (maxResult == Integer.MAX_VALUE) {
			return "SELECT * FROM (\n" + sqlStr + "\n) AS PAGINATED_RESULT WHERE ROW_NUM__INTERNAL > ? ORDER BY ROW_NUM__INTERNAL";
		}

		return "SELECT * FROM (\n" + sqlStr + ")\nAS PAGINATED_RESULT WHERE ROW_NUM__INTERNAL > ? AND ROW_NUM__INTERNAL < ? ORDER BY ROW_NUM__INTERNAL";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applySubStr(String innerFragment, String startFragment, String endFragment) {
		if (endFragment != null) {
			return "SUBSTRING(" + Joiner.on(", ").skipNulls().join(new Object[] { innerFragment, startFragment, endFragment }) + ")";
		}

		return "SUBSTRING(" + Joiner.on(", ").skipNulls().join(new Object[] { innerFragment, startFragment, Integer.toString(Integer.MAX_VALUE) }) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyTrim(Trimspec trimspec, String trimChar, String argument) {
		if (trimChar != null) {
			throw new PersistenceException("MSSQL Server does not support trim character");
		}

		if (trimspec == null) {
			trimspec = Trimspec.BOTH;
		}

		switch (trimspec) {
			case LEADING:
				return "LTRIM(" + argument + ")";
			case TRAILING:
				return "RTRIM(" + argument + ")";
			default:
				return "RTRIM(LTRIM(" + argument + "))";
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String castBoolean(String sqlFragment) {
		return super.castBoolean(sqlFragment) + " = 1";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String createColumnDDL(AbstractColumn column) {
		final boolean identity = (column instanceof PkColumn) && (((PkColumn) column).getIdType() == IdType.IDENTITY);

		return column.getName() + " " // name part
			+ this.getColumnType(column, column.getSqlType()) // data type part
			+ (!column.isNullable() ? " NOT NULL" : "") // not null part
			+ (column.isUnique() ? " UNIQUE" : "") // not null part
			+ (identity ? " IDENTITY(1,1)" : ""); // auto increment part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) {
		throw new UnsupportedOperationException("MSSQL does not support sequences");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getColumnType(AbstractColumn cd, int sqlType) {
		switch (sqlType) {
			case Types.BLOB:
				return "VARBINARY(" + cd.getLength() + ")";
			case Types.CLOB:
				return "NVARCHAR(" + cd.getLength() + ")";
			case Types.VARCHAR:
				return "NVARCHAR(" + cd.getLength() + ")";
			case Types.TIME:
			case Types.DATE:
			case Types.TIMESTAMP:
				return "DATE";
			case Types.CHAR:
				return "CHAR";
			case Types.BOOLEAN:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
			case Types.DOUBLE:
				return "FLOAT" + (cd.getPrecision() > 0 ? "(" + cd.getPrecision() + ")" : "");
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
	public String getCurrentDate() {
		return "GETDATE()";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCurrentTime() {
		return "GETDATE()";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCurrentTimeStamp() {
		return "CURRENT_TIMESTAMP";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDatabaseName() {
		return "MsSql";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public long getNextSequence(DataSource datasource, String sequenceName) throws SQLException {
		throw new UnsupportedOperationException("MSSQL does not support sequences");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getNumericFunctionTemplate(NumericFunctionType type) {
		if (type == NumericFunctionType.MOD) {
			return "{0} % {1}";
		}

		if (type == NumericFunctionType.LENGTH) {
			return "LEN({0})";
		}

		return super.getNumericFunctionTemplate(type);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PaginationParamsOrder getPaginationParamsOrder() {
		return PaginationParamsOrder.SQL_START_END;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String[] getProductNames() {
		return MsSqlAdaptor.PRODUCT_NAMES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSelectLastIdentitySql(PkColumn identityColumn) {
		return "SELECT @@IDENTITY";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPmdBroken() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean paginationNeedsMaxResultsAlways() {
		return true;
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
	public IdType supports(GenerationType type) {
		if (type == GenerationType.TABLE) {
			return IdType.TABLE;
		}

		return IdType.IDENTITY;
	}
}

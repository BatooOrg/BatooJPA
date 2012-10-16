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
import java.util.List;

import javax.persistence.GenerationType;
import javax.persistence.LockModeType;
import javax.sql.DataSource;

import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.dbutils.SingleValueHandler;
import org.batoo.jpa.core.impl.model.SequenceGenerator;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.base.Joiner;

/**
 * JDBC Adapter for Derby.
 * 
 * @author hceylan
 * @since $version
 */
public class DerbyAdaptor extends JdbcAdaptor {

	private static final String[] PRODUCT_NAMES = new String[] { "Apache Derby" };

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DerbyAdaptor() {
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
			sql = sql + "\nOFFSET ? ROWS";
		}

		if (maxResult != Integer.MAX_VALUE) {
			sql = sql + "\nFETCH FIRST ? ROWS ONLY";
		}

		return sql;
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
			+ (identity ? " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" : ""); // auto increment part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) {
		try {
			final String sql = "CREATE SEQUENCE " + sequence.getQName() //
				+ " START WITH " + sequence.getInitialValue() //
				+ " INCREMENT BY " + sequence.getAllocationSize();

			new QueryRunner(datasource).update(sql);
		}
		catch (final SQLException e) {
			this.logRelaxed(e, "Cannot create sequence" + sequence.getSequenceName());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void dropSequence(QueryRunner runner, SequenceGenerator sequence) throws SQLException {
		runner.update("DROP SEQUENCE " + sequence.getQName() + " RESTRICT");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDatabaseName() {
		return "Derby";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public long getNextSequence(DataSource datasource, String sequenceName) throws SQLException {
		return new QueryRunner(datasource) //
		.query("VALUES (NEXT VALUE FOR " + sequenceName + ")", new SingleValueHandler<Number>()).longValue();
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
		return DerbyAdaptor.PRODUCT_NAMES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSelectLastIdentitySql(PkColumn identityColumn) {
		return "VALUES IDENTITY_VAL_LOCAL()";
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

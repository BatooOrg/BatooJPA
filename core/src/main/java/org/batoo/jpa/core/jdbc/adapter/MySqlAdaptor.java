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
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.impl.model.SequenceGenerator;
import org.batoo.jpa.core.impl.model.TableGenerator;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * JDBC Adapter for MySQL.
 * 
 * @author hceylan
 * @since $version
 */
public class MySqlAdaptor extends JdbcAdaptor {

	private static final String[] DRIVER_CLASSES = new String[] { "com.mysql.jdbc.Driver" };

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MySqlAdaptor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyConcat(List<String> arguments) {
		return "CONCAT(" + Joiner.on(", ").join(arguments) + ")";
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
		}

		return sql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String applyPagination(String sql, int startPosition, int maxResult) {
		if ((startPosition != 0) || (maxResult != Integer.MAX_VALUE)) {
			sql = sql + "\nLIMIT " + startPosition;

			if (maxResult != Integer.MAX_VALUE) {
				sql = sql + ", " + maxResult;
			}
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
			+ (identity ? " AUTO_INCREMENT" : ""); // auto increment part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized void createForeignKey(DataSource datasource, ForeignKey foreignKey) throws SQLException {
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
			+ "\n\tADD FOREIGN KEY (" + keyColumns + ")" //
			+ "\n\tREFERENCES " + referenceTableName + "(" + foreignKeyColumns + ")";

		new QueryRunner(datasource).update(sql);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) throws SQLException {
		throw new UnsupportedOperationException("Mysql does not support sequences");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createTableGeneratorIfNecessary(DataSource datasource, TableGenerator table) throws SQLException {
		if (new QueryRunner(datasource).query("SHOW TABLES LIKE '" + table.getName() + "';", new SingleValueHandler<String>()) == null) {

			final String sql = "CREATE TABLE `" + table.getName() + "` ("//
				+ "\n\t" + table.getPkColumnName() + " VARCHAR(255)," //
				+ "\n\t" + table.getValueColumnName() + " INT," //
				+ "\nPRIMARY KEY(" + table.getPkColumnName() + "))";

			new QueryRunner(datasource).update(sql);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getColumnType(AbstractColumn cd, int sqlType) {
		switch (sqlType) {
			case Types.BLOB:
			case Types.CLOB:
				return "BLOB(" + cd.getLength() + ")";
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDatabaseName() {
		return "MySql";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String[] getJdbcDriverClassNames() {
		return MySqlAdaptor.DRIVER_CLASSES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public long getNextSequence(DataSourceImpl datasource, String sequenceName) throws SQLException {
		throw new UnsupportedOperationException("Mysql does not support sequences");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSelectLastIdentitySql(PkColumn identityColumn) {
		return "SELECT LAST_INSERT_ID()";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdType supports(GenerationType type) {
		if (type == GenerationType.IDENTITY) {
			return IdType.IDENTITY;
		}

		return IdType.TABLE;
	}
}

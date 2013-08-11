/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.jdbc.generator;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.jdbc.dbutils.SingleValueHandler;

/**
 * A Queue that tops up the queue by allocation size sequences when its capacity drops to 1 / 2 of allocation size.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class TableIdQueue extends IdQueue {

	private static final long serialVersionUID = 1L;

	private final DataSource datasource;
	private transient final TableGenerator generator;
	private transient final JdbcAdaptor jdbcAdaptor;

	private String selectSql;
	private String insertSql;
	private String updateSql;

	private Long nextId;

	/**
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param datasource
	 *            the datasource to use
	 * @param idExecuter
	 *            the executor service to submit refill tasks
	 * @param generator
	 *            the table generator
	 * 
	 * @since 2.0.0
	 */
	public TableIdQueue(JdbcAdaptor jdbcAdaptor, DataSource datasource, ExecutorService idExecuter, TableGenerator generator) {
		super(idExecuter, generator.getName(), generator.getAllocationSize());

		this.jdbcAdaptor = jdbcAdaptor;
		this.datasource = datasource;
		this.generator = generator;
	}

	private String getInsertSql() {
		if (this.insertSql == null) {
			this.insertSql = "INSERT INTO " + this.generator.getTable() + "\nVALUES (?, ?)";
		}

		return this.insertSql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected synchronized Long getNextId() throws SQLException {
		final QueryRunner runner = new QueryRunner(this.datasource, this.jdbcAdaptor.isPmdBroken());

		final Number nextId = runner.query(this.getSelectSql(), new SingleValueHandler<Number>(), this.generator.getPkColumnValue());
		if (nextId == null) {
			runner.update(this.getInsertSql(), this.generator.getPkColumnValue(), this.generator.getAllocationSize() + 1);
			this.nextId = 1l;
		}
		else {
			this.nextId = nextId.longValue();
			this.nextId += this.generator.getAllocationSize();
			runner.update(this.getUpdateSql(), this.nextId, this.generator.getPkColumnValue());
		}

		return this.nextId;
	}

	private String getSelectSql() {
		if (this.selectSql == null) {
			this.selectSql = "SELECT " + this.generator.getValueColumnName() + //
				"\nFROM " + this.generator.getTable() + //
				"\nWHERE " + this.generator.getPkColumnName() + " = ?";
		}

		return this.selectSql;
	}

	/**
	 * Returns the query to update the id table.
	 * 
	 * @return the query to update the id table
	 * 
	 * @since 2.0.0
	 */
	private String getUpdateSql() {
		if (this.updateSql == null) {
			this.updateSql = "UPDATE " + this.generator.getTable() + //
				"\nSET " + this.generator.getValueColumnName() + " = ?" + //
				"\nWHERE " + this.generator.getPkColumnName() + " = ?";
		}

		return this.updateSql;
	}
}

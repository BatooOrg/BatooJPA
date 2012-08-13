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
package org.batoo.jpa.core.impl.model;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;

/**
 * A Queue that tops up the queue by allocation size sequences when its capacity drops to 1 / 2 of allocation size.
 * 
 * @author hceylan
 * @since $version
 */
public class TableIdQueue extends IdQueue {

	private static final long serialVersionUID = 1L;

	private final TableGenerator generator;

	private String selectSql;
	private String insertSql;
	private String updateSql;

	private Long nextId;

	private final DataSourceImpl datasource;

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
	 * @since $version
	 * @author hceylan
	 */
	public TableIdQueue(JdbcAdaptor jdbcAdaptor, DataSourceImpl datasource, ExecutorService idExecuter, TableGenerator generator) {
		super(idExecuter, generator.getPkColumnValue(), generator.getAllocationSize());

		this.datasource = datasource;
		this.generator = generator;
	}

	private String getInsertSql() {
		if (this.insertSql == null) {
			this.insertSql = "INSERT INTO " + this.generator.getQName() + "\nVALUES (?, ?)";
		}

		return this.insertSql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Long getNextId() throws SQLException {
		final QueryRunner runner = new QueryRunner(this.datasource);

		this.nextId = runner.query(this.getSelectSql(), new SingleValueHandler<Long>(), this.generator.getPkColumnValue());
		if (this.nextId == null) {
			runner.update(this.getInsertSql(), this.generator.getPkColumnValue(), this.generator.getInitialValue() + 1);
			this.nextId = 1l;
		}
		else {
			this.nextId += this.generator.getAllocationSize();
			runner.update(this.getUpdateSql(), this.nextId, this.generator.getPkColumnValue());
		}

		return this.nextId;
	}

	private String getSelectSql() {
		if (this.selectSql == null) {
			this.selectSql = "SELECT " + this.generator.getValueColumnName() + //
				"\nFROM " + this.generator.getQName() + //
				"\nWHERE " + this.generator.getPkColumnName() + " = ?";
		}

		return this.selectSql;
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String getUpdateSql() {
		if (this.updateSql == null) {
			this.updateSql = "UPDATE " + this.generator.getQName() + //
				"\nSET " + this.generator.getValueColumnName() + " = ?" + //
				"\nWHERE " + this.generator.getPkColumnName() + " = ?";
		}

		return this.updateSql;
	}
}

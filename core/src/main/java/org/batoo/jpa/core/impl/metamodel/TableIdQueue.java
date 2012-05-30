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
package org.batoo.jpa.core.impl.metamodel;

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

	private Integer nextId;

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
		super(jdbcAdaptor, datasource, idExecuter, generator.getPkColumnValue(), generator.getAllocationSize());

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
	protected Integer getNextId() throws SQLException {
		final QueryRunner runner = new QueryRunner(this.datasource);

		this.nextId = runner.query(this.getSelectSql(), new SingleValueHandler<Integer>(), this.generator.getPkColumnValue());
		if (this.nextId == null) {
			runner.update(this.getInsertSql(), this.generator.getPkColumnValue(), this.generator.getInitialValue() + 1);
			this.nextId = 1;
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

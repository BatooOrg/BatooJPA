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
package org.batoo.jpa.core.jdbc.adapter;

import java.sql.SQLException;

import javax.persistence.SequenceGenerator;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.PhysicalTableGenerator;
import org.batoo.jpa.core.jdbc.Column;
import org.batoo.jpa.core.jdbc.IdType;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class MySqlAdapter extends JDBCAdapter {

	private static final String[] DRIVER_CLASSES = new String[] { "com.mysql.jdbc.Driver" };

	/**
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MySqlAdapter() throws MappingException {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String createColumnDDL(Column columnDefinition) {
		final boolean identity = (columnDefinition.getIdType() == IdType.IDENTITY);

		return columnDefinition.getPhysicalName() + " " // name part
			+ this.getColumnType(columnDefinition, columnDefinition.getSqlType()) // data type part
			+ (!columnDefinition.isNullable() ? " NOT NULL" : "") // not null part
			+ (columnDefinition.isUnique() ? " UNIQUE" : "") // not null part
			+ (identity ? " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)" : ""); // auto increment part
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createForeignKey(DataSource dataSource, ForeignKey foreignKey) throws SQLException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSchemaIfNecessary(DataSource datasource, String schema) throws SQLException {
		// final QueryRunner runner = new QueryRunner(datasource);
		// runner.update("DROP DATABASE test");
		// runner.update("CREATE DATABASE test");
		// runner.update("USE test");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createSequenceIfNecessary(DataSource datasource, SequenceGenerator sequence) throws SQLException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void createTableGeneratorIfNecessary(DataSource datasource, PhysicalTableGenerator table) throws SQLException {
		final String schema = this.schemaOf(datasource, table.getSchema());

		final String sql = "CREATE TABLE " + schema + "." + table.getTable() + " ("//
			+ "\n\t" + table.getPkColumnName() + " VARCHAR(255)," //
			+ "\n\t" + table.getValueColumnName() + " INT," //
			+ "\nPRIMARY KEY(" + table.getPkColumnName() + "))";

		new QueryRunner(datasource).update(sql);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void dropSchema(DataSource datasource, String schema) throws SQLException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getDatabaseName() {
		return "mysql";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDefaultSchema(DataSource dataSource) {
		return "test";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String[] getJDBCDriverClassNames() {
		return DRIVER_CLASSES;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getNextSequence(DataSourceImpl datasource, String sequenceName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSelectLastIdentitySql() {
		return "SELECT LAST_INSERT_ID()";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdType supports(IdType idType) {
		if (idType == null) {
			return IdType.TABLE;
		}

		return idType;
	}

}

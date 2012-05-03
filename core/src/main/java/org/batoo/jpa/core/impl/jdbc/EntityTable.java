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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.TableTemplate;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Template of a single table.
 * 
 * @author hceylan
 * @since $version
 */
public final class EntityTable extends AbstractTable {

	private PhysicalColumn identityColumn;
	private PhysicalColumn primaryKey;
	private final TableType tableType;
	private final QueryRunner runner;

	/**
	 * @param type
	 *            the owner entity
	 * @param template
	 *            the template for the table
	 * @param jdbcAdapter
	 *            the JDBC Adapter
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable(EntityTypeImpl<?> type, TableTemplate template, JDBCAdapter jdbcAdapter) throws MappingException {
		super(type, template.getSchema(), template.getName() != null ? template.getName() : type.getName(),
			template.getUniqueConstraints(), jdbcAdapter);

		if (type != type.getRoot()) {
			this.tableType = TableType.DEFAULT;
		}
		else if (template.isPrimary()) {
			this.tableType = TableType.PRIMARY;
		}
		else {
			this.tableType = TableType.SECONDARY;
		}

		this.runner = new QueryRunner();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addColumn(PhysicalColumn column) throws MappingException {
		super.addColumn(column);

		if (column.getIdType() == IdType.IDENTITY) {
			this.identityColumn = column;
		}
	}

	/**
	 * Returns the singleton primary key column.
	 * 
	 * @return the singleton primary key column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn getPrimaryKey() {
		if (this.primaryKey == null) {
			if (this.primaryKeys.size() == 1) {
				this.primaryKey = this.primaryKeys.get(0);
			}
		}

		return this.primaryKey;
	}

	@Override
	public TableType getTableType() {
		return this.tableType;
	}

	/**
	 * <ul>
	 * Links the table with the primary table of the type:
	 * <li>For secondary tables, the primary table is the primary table of the type.
	 * <li>For primary tables, the root type's primary table is the primary table.
	 * </ul>
	 * 
	 * @param primaryTable
	 *            the primary table for the table
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(EntityTypeImpl<?> type) throws MappingException {
		final List<PhysicalColumn> keyColumns = Lists.newArrayList();
		for (final PhysicalColumn column : type.getPrimaryTable().getPrimaryKeys()) {
			keyColumns.add(new PhysicalColumn(this, column));
		}

		this.addForeignKey(new ForeignKey(this.getName(), type.getPrimaryTable().getName(), keyColumns));
	}

	/**
	 * Performs inserts to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(Connection connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final SessionImpl session = managedInstance.getSession();
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the insert sql will initialize the insertColumns!
		final String insertSql = this.getInsertSql(managedInstance.getType());
		final PhysicalColumn[] insertColumns = this.insertColumns.get(managedInstance.getType());

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length];
		for (int i = 0; i < insertColumns.length; i++) {
			params[i] = insertColumns[i].getPhysicalValue(session, instance);
		}

		this.runner.update(connection, insertSql, params);

		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdapter.getSelectLastIdentitySql();
			final Number id = this.runner.query(connection, selectLastIdSql, new SingleValueHandler<Number>());

			this.identityColumn.getMapping().setValue(managedInstance.getInstance(), id);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String columns = Joiner.on(", ").join(Lists.transform(this.columns, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				final StringBuffer out = new StringBuffer();
				out.append(input.isId() ? "ID [" : "COL [");

				out.append("name=");
				out.append(input.getName());
				out.append(", type=");
				out.append(input.getSqlType());

				out.append("]");
				return out.toString();
			}
		}));

		return "EntityTable [owner=" + this.type.getName() + ", name=" + this.getQualifiedName() + ", type=" + this.tableType
			+ ", columns=[" + columns + "]]";
	}

}

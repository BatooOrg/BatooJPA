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
import org.batoo.jpa.core.jdbc.Table;
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
public class EntityTable extends AbstractTable implements Table {

	private final boolean primary;

	private PhysicalColumn identityColumn;

	private PhysicalColumn primaryKey;

	/**
	 * @param owner
	 *            the owner entity
	 * @param template
	 *            the template for the table
	 * @param jdbcAdapter
	 *            the JDBC Adapter
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable(EntityTypeImpl<?> owner, TableTemplate template, JDBCAdapter jdbcAdapter) throws MappingException {
		super(owner, template.getSchema(), template.getName() != null ? template.getName() : owner.getName(),
			template.getUniqueConstraints(), jdbcAdapter);

		this.primary = template.isPrimary();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addColumn(PhysicalColumn column) throws MappingException {
		super.addColumn(column);

		if (column.getIdType() == IdType.IDENTITY) {
			if (this.identityColumn != null) {
				throw new MappingException("Multiple identity columns: " + this.identityColumn.getName() + ", " + column.getName() + " on "
					+ this.owner.getJavaType().getCanonicalName());
			}

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
				this.primaryKey = this.primaryKeys.iterator().next();
			}
		}

		return this.primaryKey;
	}

	/**
	 * Returns the primary.
	 * 
	 * @return the primary
	 * @since $version
	 */
	@Override
	public boolean isPrimary() {
		return this.primary;
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

		final QueryRunner runner = new QueryRunner();

		// Do not inline, generation of the insert sql will initialize the insertColumns!
		final String insertSql = this.getInsertSql();

		final SessionImpl session = managedInstance.getSession();
		final Object instance = managedInstance.getInstance();
		final List<Object> params = Lists.transform(this.insertColumns, new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				return input.getPhysicalValue(session, instance);
			}
		});

		runner.update(connection, insertSql, params.toArray());

		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdapter.getSelectLastIdentitySql();
			final Number id = runner.query(connection, selectLastIdSql, new SingleValueHandler<Number>());

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

		return "EntityTable [owner=" + this.owner.getName() + ", name=" + this.getQualifiedName() + ", primary=" + this.primary
			+ ", columns=[" + columns + "]]";
	}

}

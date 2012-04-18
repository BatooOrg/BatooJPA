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
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * A Physical table to persist joins.
 * 
 * @author hceylan
 * @since $version
 */
public final class JoinTable extends AbstractTable {

	private final Association<?, ?> mapping;
	protected final List<PhysicalColumn> sourceKeys = Lists.newArrayList();
	protected final List<PhysicalColumn> destinationKeys = Lists.newArrayList();

	/**
	 * @param mapping
	 *            the mapping
	 * @param jdbcAdapter
	 *            the jdbc adapter
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinTable(Association<?, ?> mapping, JDBCAdapter jdbcAdapter) throws MappingException {
		super(mapping.getOwner(), mapping.getOwner().getPrimaryTable().getSchema(), mapping.getOwner().getName() + "_"
			+ mapping.getType().getName(), null, jdbcAdapter);

		this.mapping = mapping;

		// add source columns
		this.createColumns(this.owner, this.sourceKeys);

		// add destination columns
		this.createColumns(this.mapping.getType(), this.destinationKeys);

		this.owner.addJoinTable(this);
	}

	private void createColumns(EntityTypeImpl<?> type, List<PhysicalColumn> columns) throws MappingException {
		for (final PhysicalColumn column : type.getPrimaryTable().getPrimaryKeys()) {
			columns.add(new PhysicalColumn(this, type.getName(), column));
		}

		this.addForeignKey(new ForeignKey(this.getQualifiedName(), type.getPrimaryTable().getQualifiedName(), columns));
	}

	/**
	 * Returns the destinationKeys.
	 * 
	 * @return the destinationKeys
	 * @since $version
	 */
	public List<PhysicalColumn> getDestinationKeys() {
		return this.destinationKeys;
	}

	/**
	 * Returns the sourceKeys.
	 * 
	 * @return the sourceKeys
	 * @since $version
	 */
	public List<PhysicalColumn> getSourceKeys() {
		return this.sourceKeys;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPrimary() {
		return false;
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
	@SuppressWarnings("unchecked")
	public void performInsert(Connection connection, ManagedInstance<?> managedInstance) throws SQLException {
		final ManagedCollection<Object> children = (ManagedCollection<Object>) this.mapping.getValue(managedInstance.getInstance());
		for (final Object child : children.getCollection()) {
			this.performInsert(connection, managedInstance, child);
		}
	}

	/**
	 * Performs inserts to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @param child
	 *            the child instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void performInsert(Connection connection, final ManagedInstance<?> managedInstance, final Object child) throws SQLException {
		final QueryRunner runner = new QueryRunner();

		// Do not inline, generation of the insert sql will initialize the insertColumns!
		final String insertSql = this.getInsertSql();

		final SessionImpl session = managedInstance.getSession();
		final Object instance = managedInstance.getInstance();
		final List<Object> params = Lists.transform(this.insertColumns, new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				final Object object = JoinTable.this.sourceKeys.contains(input) ? instance : child;
				return input.getPhysicalValue(session, object);
			}
		});

		runner.update(connection, insertSql, params.toArray());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String sourceColumns = Joiner.on(", ").join(Lists.transform(this.sourceKeys, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				final StringBuffer out = new StringBuffer();
				out.append("COL [");

				out.append("name=");
				out.append(input.getName());
				out.append(", type=");
				out.append(input.getSqlType());

				out.append("]");
				return out.toString();
			}
		}));

		final String destinationColumns = Joiner.on(", ").join(
			Lists.transform(this.destinationKeys, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					final StringBuffer out = new StringBuffer();
					out.append("COL [");

					out.append("name=");
					out.append(input.getName());
					out.append(", type=");
					out.append(input.getSqlType());

					out.append("]");
					return out.toString();
				}
			}));

		return "JoinTable [owner=" + this.owner.getName() + ", inverse=" + this.mapping.getType().getName() + ", name="
			+ this.getQualifiedName() + ", sourceKeys=[" + sourceColumns + "], destinationKeys=[" + destinationColumns + "]]";
	}

}

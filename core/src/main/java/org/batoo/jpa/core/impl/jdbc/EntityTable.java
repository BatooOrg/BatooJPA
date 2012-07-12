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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.TableMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * Table representing an entity persistent storage.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTable extends AbstractTable {

	private final EntityTypeImpl<?> entity;
	private final Map<String, PkColumn> pkColumns = Maps.newHashMap();

	private final JdbcAdaptor jdbcAdaptor;
	private PkColumn identityColumn;
	private String removeSql;
	private PkColumn[] removeColumns;

	/**
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the table metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable(EntityTypeImpl<?> entity, TableMetadata metadata) {
		super(entity.getName(), metadata);

		this.entity = entity;

		this.jdbcAdaptor = entity.getMetamodel().getJdbcAdaptor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addColumn(AbstractColumn column) {
		super.addColumn(column);

		if (column instanceof PkColumn) {
			final PkColumn pkColumn = (PkColumn) column;

			this.pkColumns.put(pkColumn.getMappingName(), pkColumn);

			if (pkColumn.getIdType() == IdType.IDENTITY) {
				this.identityColumn = (PkColumn) column;
			}
		}
	}

	/**
	 * Returns the entity of the EntityTable.
	 * 
	 * @return the entity of the EntityTable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<?> getEntity() {
		return this.entity;
	}

	/**
	 * Returns the pkColumns of the EntityTable.
	 * 
	 * @return the pkColumns of the EntityTable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Map<String, PkColumn> getPkColumns() {
		return this.pkColumns;
	}

	private String getRemoveSql() {
		if (this.removeSql != null) {
			return this.removeSql;
		}

		synchronized (this) {
			if (this.removeSql != null) {
				return this.removeSql;
			}

			this.removeColumns = new PkColumn[this.pkColumns.size()];
			this.pkColumns.values().toArray(this.removeColumns);

			final Collection<String> restrictions = Collections2.transform(this.pkColumns.values(), new Function<PkColumn, String>() {

				@Override
				public String apply(PkColumn input) {
					return input.getName() + " = ?";
				}
			});

			return this.removeSql = "DELETE FROM " + this.getQName() + " WHERE " + Joiner.on(" AND ").join(restrictions);
		}
	}

	/**
	 * Performs inserts to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(ConnectionImpl connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final EntityTypeImpl<?> entityType = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the insert SQL will initialize the insertColumns!
		final String insertSql = this.getInsertSql(entityType);
		final AbstractColumn[] insertColumns = this.getInsertColumns(entityType);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length];
		for (int i = 0; i < insertColumns.length; i++) {
			final AbstractColumn column = insertColumns[i];
			if (column instanceof DiscriminatorColumn) {
				params[i] = managedInstance.getType().getDiscriminatorValue();
			}
			else {
				params[i] = column.getValue(instance);
			}
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner();
		runner.update(connection, insertSql, params);

		// if there is an identity column, extract the identity and set it back to the instance
		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdaptor.getSelectLastIdentitySql();
			final Number id = runner.query(connection, selectLastIdSql, new SingleValueHandler<Number>());

			this.identityColumn.setValue(managedInstance, id);
		}
	}

	/**
	 * Performs removes from the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRemove(ConnectionImpl connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final String removeSql = this.getRemoveSql();

		// prepare the parameters
		final Object[] params = new Object[this.removeColumns.length];
		for (int i = 0; i < this.removeColumns.length; i++) {
			final PkColumn column = this.removeColumns[i];
			params[i] = column.getValue(managedInstance.getInstance());
		}

		final QueryRunner runner = new QueryRunner();
		runner.update(connection, removeSql, params);
	}

	/**
	 * Performs update to the table for the managed instance or joins.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of underlying SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performUpdate(ConnectionImpl connection, final ManagedInstance<?> managedInstance) throws SQLException {
		final EntityTypeImpl<?> entityType = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the update SQL will initialize the insertColumns!
		final String updateSql = this.getUpdateSql(entityType, this.pkColumns);
		final AbstractColumn[] updateColumns = this.getUpdateColumns(entityType);

		// prepare the parameters
		final Object[] params = new Object[updateColumns.length];
		for (int i = 0; i < updateColumns.length; i++) {
			final AbstractColumn column = updateColumns[i];
			params[i] = column.getValue(instance);
		}

		// execute the insert
		final QueryRunner runner = new QueryRunner();
		runner.update(connection, updateSql, params);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String columns = Joiner.on(", ").join(Collections2.transform(this.getColumns(), new Function<AbstractColumn, String>() {

			@Override
			public String apply(AbstractColumn input) {
				final StringBuffer out = new StringBuffer();
				out.append(input instanceof PkColumn ? "ID [" : "COL [");

				out.append("name=");
				out.append(input.getName());
				out.append(", type=");
				out.append(input.getSqlType());

				out.append("]");
				return out.toString();
			}
		}));

		return "Table [owner=" + this.entity.getName() //
			+ ", name=" + this.getQName() //
			+ ", columns=[" + columns + "]]";
	}
}

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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.TableMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Table representing an entity persistent storage.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTable extends AbstractTable {

	private final EntityTypeImpl<?> entity;
	private final QueryRunner runner;

	private final HashMap<EntityTypeImpl<?>, String> insertSqls = Maps.newHashMap();
	private final HashMap<EntityTypeImpl<?>, PhysicalColumn[]> insertColumns = Maps.newHashMap();
	private final JdbcAdaptor jdbcAdaptor;
	private PkPhysicalColumn identityColumn;

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

		this.runner = new QueryRunner();
		this.jdbcAdaptor = entity.getMetamodel().getJdbcAdaptor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addColumn(PhysicalColumn column) {
		if ((column instanceof PkPhysicalColumn) && (((PkPhysicalColumn) column).getIdType() == IdType.IDENTITY)) {
			this.identityColumn = (PkPhysicalColumn) column;
		}
	}

	/**
	 * Generates the insert statement for the type.
	 * 
	 * @param type
	 *            the type to generate the insert statement for
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private synchronized void generateInsertSql(final EntityTypeImpl<?> type) {
		String sql = this.insertSqls.get(type);
		if (sql != null) { // other thread finished the job for us
			return;
		}

		final List<PhysicalColumn> insertColumns = Lists.newArrayList();
		// Filter out the identity physicalColumns
		final Collection<PhysicalColumn> filteredColumns = type == null ? this.getColumns() : Collections2.filter(this.getColumns(),
			new Predicate<PhysicalColumn>() {

				@Override
				public boolean apply(PhysicalColumn input) {
					if ((input instanceof PkPhysicalColumn) && (((PkPhysicalColumn) input).getIdType() == IdType.IDENTITY)) {
						return false;
					}

					// XXX implement with inheritance
					// if (input.isDiscriminator()) {
					// return true;
					// }

					return true;
					// XXX: inheritance
					// final AttributeImpl<?, ?> root = input.getMapping().getPath().getFirst();
					// final Class<?> parent = root.getDeclaringType().getJavaType();
					// final Class<?> javaType = type.getJavaType();
					//
					// return parent.isAssignableFrom(javaType);
				}
			});

		// prepare the names tuple in the form of "COLNAME [, COLNAME]*"
		final Collection<String> columnNames = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				insertColumns.add(input);

				return input.getName();
			}
		});

		// prepare the parameters in the form of "? [, ?]*"
		final Collection<String> parameters = Collections2.transform(filteredColumns, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return "?";
			}
		});

		final String columnNamesStr = Joiner.on(", ").join(columnNames);
		final String parametersStr = Joiner.on(", ").join(parameters);

		// INSERT INTO SCHEMA.TABLE
		// (COL [, COL]*)
		// VALUES (PARAM [, PARAM]*)
		sql = "INSERT INTO " + this.getQName() //
			+ "\n(" + columnNamesStr + ")"//
			+ "\nVALUES (" + parametersStr + ")";

		this.insertSqls.put(type, sql);
		this.insertColumns.put(type, insertColumns.toArray(new PhysicalColumn[insertColumns.size()]));
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
	 * Returns the insert statement for the table specifically.
	 * 
	 * @param type
	 *            the type to return insert statement for
	 * @return the insert statement
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getInsertSql(EntityTypeImpl<?> type) {
		String sql = this.insertSqls.get(type);
		if (sql == null) {
			this.generateInsertSql(type);

			sql = this.insertSqls.get(type);
		}

		return sql;
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
		managedInstance.getSession();

		final EntityTypeImpl<?> entityType = managedInstance.getType();
		final Object instance = managedInstance.getInstance();

		// Do not inline, generation of the insert SQL will initialize the insertColumns!
		final String insertSql = this.getInsertSql(entityType);
		final PhysicalColumn[] insertColumns = this.insertColumns.get(entityType);

		// prepare the parameters
		final Object[] params = new Object[insertColumns.length];
		for (int i = 0; i < insertColumns.length; i++) {
			params[i] = insertColumns[i].getValue(instance);
		}

		// execute the insert
		this.runner.update(connection, insertSql, params);

		// if there is an identity column, extract the identity and set it back to the instance
		if (this.identityColumn != null) {
			final String selectLastIdSql = this.jdbcAdaptor.getSelectLastIdentitySql();
			final Number id = this.runner.query(connection, selectLastIdSql, new SingleValueHandler<Number>());

			this.identityColumn.setValue(instance, id);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String columns = Joiner.on(", ").join(Collections2.transform(this.getColumns(), new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				final StringBuffer out = new StringBuffer();
				out.append(input instanceof PkPhysicalColumn ? "ID [" : "COL [");

				out.append("name=");
				out.append(input.getName());
				out.append(", type=");
				out.append(input.getSqlType());

				out.append("]");
				return out.toString();
			}
		}));

		return "EntityTable [owner=" + this.entity.getName() //
			+ ", name=" + this.getQName() //
			+ ", columns=[" + columns + "]]";
	}
}

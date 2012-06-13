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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * BasicColumn to persist basic attributes of the entity.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicColumn extends AbstractColumn {

	private EntityTable table;
	private final int sqlType;
	private final String name;
	private final String columnDefinition;
	private final int length;
	private final AbstractLocator locator;
	private final int precision;
	private final int scale;
	private final String tableName;
	private final boolean nullable;
	private final boolean insertable;
	private final boolean unique;
	private final boolean updatable;
	private final String mappingName;
	private final BasicMapping<?, ?> mapping;
	private final JdbcAdaptor jdbcAdaptor;

	/**
	 * @param mapping
	 *            the mapping
	 * @param sqlType
	 *            the SQL type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicColumn(BasicMapping<?, ?> mapping, int sqlType, ColumnMetadata metadata) {
		super();

		this.jdbcAdaptor = mapping.getEntity().getMetamodel().getJdbcAdaptor();
		this.mapping = mapping;
		this.locator = metadata != null ? metadata.getLocator() : null;
		this.sqlType = sqlType;

		this.mappingName = (metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName()
			: this.mapping.getAttribute().getName();
		this.name = this.jdbcAdaptor.escape(this.mappingName);

		this.tableName = metadata != null ? metadata.getTable() : "";
		this.columnDefinition = metadata != null ? metadata.getColumnDefinition() : "";
		this.length = metadata != null ? metadata.getLength() : 255;
		this.precision = metadata != null ? metadata.getPrecision() : 0;
		this.scale = metadata != null ? metadata.getScale() : 0;
		this.insertable = metadata != null ? metadata.isInsertable() : true;
		this.nullable = metadata != null ? metadata.isNullable() : true;
		this.unique = metadata != null ? metadata.isUnique() : false;
		this.updatable = metadata != null ? metadata.isUpdatable() : true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the mapping of the BasicColumn.
	 * 
	 * @return the mapping of the BasicColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public BasicMapping<?, ?> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMappingName() {
		return this.mappingName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getPrecision() {
		return this.precision;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getScale() {
		return this.scale;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getSqlType() {
		return this.sqlType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTable getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getValue(SessionImpl session, Object instance) {
		return this.mapping.get(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUnique() {
		return this.unique;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isUpdatable() {
		return this.updatable;
	}

	/**
	 * Sets the table of the column.
	 * 
	 * @param table
	 *            the owning table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public void setTable(AbstractTable table) {
		this.table = (EntityTable) table;

		this.table.addColumn(this);
	}

	/**
	 * Sets the value for the instance
	 * 
	 * @param managedInstance
	 *            the instance of which to set value
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void setValue(ManagedInstance managedInstance, Object value) {
		this.mapping.set(managedInstance, value);
	}
}

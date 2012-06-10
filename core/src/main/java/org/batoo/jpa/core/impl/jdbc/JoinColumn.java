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

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

/**
 * Mapping of two {@link BasicColumn}s.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinColumn extends AbstractColumn {

	private final AbstractLocator locator;

	private final String columnDefinition;
	private final String tableName;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean unique;
	private final boolean updatable;

	private AttributeImpl<?, ?> attribute;
	private String mappingName;
	private String name;
	private String referencedColumnName;
	private IdAttributeImpl<?, ?> referencedAttribute;

	private AbstractTable table;

	private int length;
	private int precision;
	private int sqlType;
	private int scale;

	/**
	 * Constructor with no metadata
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param attribute
	 *            the attribute
	 * @param idAttribute
	 *            the referenced primary key column name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, IdAttributeImpl<?, ?> idAttribute) {
		super();

		this.attribute = attribute;
		this.locator = null;

		this.columnDefinition = "";
		this.tableName = "";
		this.insertable = true;
		this.nullable = true;
		this.unique = false;;
		this.updatable = true;

		this.setColumnProperties(jdbcAdaptor, attribute, idAttribute);
	}

	/**
	 * Constructor with metadata
	 * 
	 * @param metadata
	 *            the referenced column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JoinColumnMetadata metadata) {
		super();

		this.locator = metadata.getLocator();

		this.referencedColumnName = metadata.getReferencedColumnName();
		this.columnDefinition = metadata.getColumnDefinition();
		this.tableName = metadata.getTable();
		this.insertable = metadata.isInsertable();
		this.nullable = metadata.isNullable();
		this.unique = metadata.isUnique();
		this.updatable = metadata.isUpdatable();
	}

	/**
	 * Returns the columnDefinition of the JoinColumn.
	 * 
	 * @return the columnDefinition of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
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
	 * Returns the locator of the JoinColumn.
	 * 
	 * @return the locator of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
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
	 * Returns the name of the JoinColumn.
	 * 
	 * @return the name of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
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
	 * Returns the name of the referenced column of the join column.
	 * 
	 * @return the name of the referenced column of the join column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getReferencedColumnName() {
		return this.referencedColumnName;
	}

	/**
	 * Returns the referenced table.
	 * 
	 * @return the referenced table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getReferencedTable() {
		return this.referencedAttribute.getColumn().getTable();
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
	public AbstractTable getTable() {
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
		if (this.attribute != null) {
			instance = this.attribute.get(instance);
		}

		return instance != null ? this.referencedAttribute.get(instance) : null;
	}

	/**
	 * Returns the insertable of the JoinColumn.
	 * 
	 * @return the insertable of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * Returns the nullable of the JoinColumn.
	 * 
	 * @return the nullable of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isNullable() {
		return this.nullable;
	}

	/**
	 * Returns the unique of the JoinColumn.
	 * 
	 * @return the unique of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isUnique() {
		return this.unique;
	}

	/**
	 * Returns the updatable of the JoinColumn.
	 * 
	 * @return the updatable of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public boolean isUpdatable() {
		return this.updatable;
	}

	private void setColumnProperties(IdAttributeImpl<?, ?> referencedAttribute) {
		this.referencedAttribute = referencedAttribute;
		final PkColumn referencedColumn = referencedAttribute.getColumn();

		this.referencedColumnName = referencedColumn.getName();
		this.sqlType = referencedColumn.getSqlType();
		this.length = referencedColumn.getLength();
		this.precision = referencedColumn.getPrecision();
		this.scale = referencedColumn.getScale();
	}

	/**
	 * Sets the column definition.
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param attribute
	 *            the owner attribute
	 * @param referencedAttribute
	 *            the referenced primary key Column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setColumnProperties(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, IdAttributeImpl<?, ?> referencedAttribute) {
		// if attribute present then the join column belongs to an entity table
		if (attribute != null) {
			this.attribute = attribute;
			this.mappingName = attribute.getName() + "_" + referencedAttribute.getColumn().getName();
			this.name = jdbcAdaptor.escape(this.mappingName);
		}
		else {
			this.mappingName = ((EntityTypeImpl<?>) referencedAttribute.getDeclaringType()).getName() + "_"
				+ referencedAttribute.getColumn().getName();
			this.name = jdbcAdaptor.escape(this.mappingName);
		}

		this.setColumnProperties(referencedAttribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTable(AbstractTable table) {
		this.table = table;

		this.table.addColumn(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValue(ManagedInstance managedInstance, Object value) {
		this.attribute.set(managedInstance, value);
	}
}

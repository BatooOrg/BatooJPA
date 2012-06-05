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

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
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

	private final AttributeImpl<?, ?> attribute;
	private final String columnDefinition;
	private final String tableName;
	private final String name;
	private final String referencedColumnName;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean unique;
	private final boolean updatable;

	private AbstractTable table;
	private AttributeImpl<?, ?> referencedAttribute;
	private PkColumn referencedColumn;
	private int length;

	private final String mappingName;
	private int precision;
	private int sqlType;
	private int scale;

	/**
	 * Constructor with metadata
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param attribute
	 *            the attribute
	 * @param metadata
	 *            the referenced column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, JoinColumnMetadata metadata) {
		super();

		this.attribute = attribute;
		this.locator = metadata.getLocator();

		this.columnDefinition = metadata.getColumnDefinition();
		this.mappingName = StringUtils.isNotBlank(metadata.getName()) ? metadata.getName() : attribute.getName();
		this.name = jdbcAdaptor.escape(this.mappingName);
		this.referencedColumnName = metadata.getReferencedColumnName();
		this.tableName = metadata.getTable();
		this.insertable = metadata.isInsertable();
		this.nullable = metadata.isNullable();
		this.unique = metadata.isUnique();
		this.updatable = metadata.isUpdatable();
	}

	/**
	 * Constructor with no metadata
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param attribute
	 *            the attribute
	 * @param referencedColumn
	 *            the referenced primary key column name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(JdbcAdaptor jdbcAdaptor, AttributeImpl<?, ?> attribute, PkColumn referencedColumn) {
		super();

		this.attribute = attribute;
		this.locator = null;

		final EntityTypeImpl<?> referencedEntity = (EntityTypeImpl<?>) referencedColumn.getAttribute().getDeclaringType();
		this.mappingName = attribute.getName() + "_" + referencedEntity.getName();
		this.name = jdbcAdaptor.escape(this.mappingName);

		this.columnDefinition = "";
		this.referencedColumnName = null;
		this.tableName = "";
		this.insertable = true;
		this.nullable = true;
		this.unique = false;;
		this.updatable = true;

		this.setColumnProperties(referencedColumn);
	}

	/**
	 * Returns the attribute of the JoinColumn.
	 * 
	 * @return the attribute of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public AttributeImpl<?, ?> getAttribute() {
		return this.attribute;
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
	 * Returns the referenced column of the join column.
	 * 
	 * @return the referenced column of the join column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PkColumn getReferencedColumn() {
		return this.referencedColumn;
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
		instance = this.attribute.get(instance);

		if (instance instanceof EnhancedInstance) {
			final ManagedInstance<?> managedInstance = ((EnhancedInstance) instance).__enhanced__$$__getManagedInstance();
			if (managedInstance.getStatus() != Status.MANAGED) {
				throw new PersistenceException("Instance " + instance + " is not managed");
			}
		}
		else {
			final ManagedInstance<Object> managedInstance = session.get(instance);
			if ((managedInstance == null) || (managedInstance.getStatus() != Status.MANAGED)) {
				throw new PersistenceException("Instance " + instance + " is not managed");
			}
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

	/**
	 * Sets the column definition.
	 * 
	 * @param referencedColumn
	 *            the referenced primary key Column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setColumnProperties(PkColumn referencedColumn) {
		this.referencedAttribute = referencedColumn.getAttribute();
		this.referencedColumn = referencedColumn;

		this.sqlType = referencedColumn.getSqlType();
		this.length = referencedColumn.getLength();
		this.precision = referencedColumn.getPrecision();
		this.scale = referencedColumn.getScale();
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
	public void setValue(Object instance, Object value) {
		this.attribute.set(instance, value);
	}
}

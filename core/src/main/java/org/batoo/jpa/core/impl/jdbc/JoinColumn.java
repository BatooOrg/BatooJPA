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
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

/**
 * Mapping of two {@link BasicColumn}s.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinColumn extends AbstractColumn {

	private final AbstractLocator locator;
	private AbstractTable table;

	private String mappingName;
	private String name;
	private String referencedColumnName;

	private final String columnDefinition;
	private final String tableName;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean unique;
	private final boolean updatable;

	private int length;
	private int precision;
	private int sqlType;
	private int scale;

	private AssociationMapping<?, ?, ?> mapping;
	private BasicMapping<?, ?> referencedMapping;

	/**
	 * Constructor with no metadata.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param idMapping
	 *            the referenced mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(AssociationMapping<?, ?, ?> mapping, BasicMapping<?, ?> idMapping) {
		super();

		this.mapping = mapping;
		this.locator = null;

		this.columnDefinition = "";
		this.tableName = "";
		this.insertable = true;
		this.nullable = true;
		this.unique = false;
		this.updatable = true;

		this.setColumnProperties(mapping, idMapping);
	}

	/**
	 * Constructor for inheritance and secondary table joins.
	 * 
	 * @param table
	 *            the table
	 * @param idMapping
	 *            the referenced id mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(EntityTable table, BasicMapping<?, ?> idMapping) {
		super();

		this.locator = null;
		this.referencedMapping = idMapping;
		final PkColumn referencedColumn = (PkColumn) idMapping.getColumn();

		this.referencedColumnName = referencedColumn.getName();
		this.columnDefinition = referencedColumn.getColumnDefinition();
		this.tableName = table.getName();
		this.mappingName = referencedColumn.getMappingName();
		this.name = referencedColumn.getName();
		this.insertable = referencedColumn.isInsertable();
		this.nullable = referencedColumn.isNullable();
		this.unique = referencedColumn.isUnique();
		this.updatable = referencedColumn.isUnique();

		this.setColumnProperties(idMapping);
		this.setTable(table);
	}

	/**
	 * Constructor with metadata.
	 * 
	 * @param metadata
	 *            the metadata for the join
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
	 * Constructor for secondary table join column with metadata.
	 * 
	 * @param metadata
	 *            the metadata for the join
	 * @param table
	 *            the secondary table
	 * @param idMapping
	 *            the id mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumn(PrimaryKeyJoinColumnMetadata metadata, SecondaryTable table, BasicMapping<?, ?> idMapping) {
		super();

		this.locator = metadata.getLocator();
		this.tableName = table.getName();

		this.referencedColumnName = metadata.getReferencedColumnName();
		this.columnDefinition = metadata.getColumnDefinition();
		this.mappingName = metadata.getName();

		this.insertable = true;
		this.nullable = false;
		this.unique = false;
		this.updatable = false;

		this.setColumnProperties(idMapping);
		this.setTable(table);
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
	public Mapping<?, ?, ?> getMapping() {
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
	 * Returns the referencedMapping of the JoinColumn.
	 * 
	 * @return the referencedMapping of the JoinColumn
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping<?, ?> getReferencedMapping() {
		return this.referencedMapping;
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
		return this.referencedMapping.getColumn().getTable();
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
	public Object getValue(Object instance) {
		if (this.mapping != null) {
			instance = this.mapping.get(instance);
		}

		return instance != null ? this.referencedMapping.get(instance) : null;
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
	 * @param mapping
	 *            the owner mapping
	 * @param referencedMapping
	 *            the referenced primary key Column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setColumnProperties(AssociationMapping<?, ?, ?> mapping, BasicMapping<?, ?> referencedMapping) {
		final JdbcAdaptor jdbcAdaptor = referencedMapping.getEntity().getMetamodel().getJdbcAdaptor();

		// if attribute present then the join column belongs to an entity table
		if (mapping != null) {
			this.mapping = mapping;
			this.mappingName = mapping.getName() + "_" + referencedMapping.getColumn().getName();
			this.name = jdbcAdaptor.escape(this.mappingName);
		}
		else {
			this.mappingName = ((EntityTypeImpl<?>) referencedMapping.getEntity()).getName() + "_" + referencedMapping.getColumn().getName();
			this.name = jdbcAdaptor.escape(this.mappingName);
		}

		this.setColumnProperties(referencedMapping);
	}

	private void setColumnProperties(BasicMapping<?, ?> referencedMapping) {
		this.referencedMapping = referencedMapping;
		final PkColumn referencedColumn = (PkColumn) referencedMapping.getColumn();

		this.referencedColumnName = referencedColumn.getName();
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
	@SuppressWarnings("rawtypes")
	public void setValue(ManagedInstance managedInstance, Object value) {
		if (this.mapping != null) {
			this.mapping.set(managedInstance, value);
		}
	}
}

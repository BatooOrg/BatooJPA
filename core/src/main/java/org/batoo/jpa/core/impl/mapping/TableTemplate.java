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
package org.batoo.jpa.core.impl.mapping;

import java.util.Arrays;
import java.util.List;

import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.types.IdentifiableTypeImpl;

import com.google.common.collect.Lists;

/**
 * Template of a single table.
 * 
 * @author hceylan
 * @since $version
 */
public final class TableTemplate {

	private final IdentifiableTypeImpl<?> owner;
	private final String schema;
	private final String name;
	private final boolean primary;

	private final UniqueConstraint[] uniqueConstraints;
	private final PrimaryKeyJoinColumn[] primaryKeyJoinColumns;

	private final List<PhysicalColumn> columns = Lists.newArrayList();
	private final List<PhysicalColumn> primaryKeys = Lists.newArrayList();

	/**
	 * @param schemaName
	 *            the unescaped schema name
	 * @param tableName
	 *            The unescaped table name, must be null for primary table
	 * @param uniqueConstraints
	 *            the unique constraints for the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableTemplate(IdentifiableTypeImpl<?> identifiableTypeImpl, String schema, String name, UniqueConstraint[] uniqueConstraints) {
		super();

		this.owner = identifiableTypeImpl;
		this.schema = schema;
		this.name = name;
		this.primary = true;
		this.uniqueConstraints = uniqueConstraints;
		this.primaryKeyJoinColumns = null;
	}

	/**
	 * @param schemaName
	 *            the unescaped schema name
	 * @param tableName
	 *            The unescaped table name, must be null for primary table
	 * @param uniqueConstraints
	 *            the unique constraints for the table
	 * @param primaryKeyJoinColumns
	 *            the primary key join columns, only required for secondary tables.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableTemplate(IdentifiableTypeImpl<?> identifiableTypeImpl, String schema, String name, UniqueConstraint[] uniqueConstraints,
		PrimaryKeyJoinColumn[] primaryKeyJoinColumns) {
		super();

		this.owner = identifiableTypeImpl;
		this.schema = schema;
		this.name = name;
		this.primary = false;
		this.uniqueConstraints = uniqueConstraints;
		this.primaryKeyJoinColumns = primaryKeyJoinColumns;
	}

	/**
	 * Adds a new mapping to the table
	 * 
	 * @param column
	 *            the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addColumn(PhysicalColumn column) {
		this.columns.add(column);

		if (column.isId()) {
			this.primaryKeys.add(column);
		}
	}

	/**
	 * Returns the columns.
	 * 
	 * @return the columns
	 * @since $version
	 */
	public List<PhysicalColumn> getColumns() {
		return this.columns;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the owner.
	 * 
	 * @return the owner
	 * @since $version
	 */
	public IdentifiableTypeImpl<?> getOwner() {
		return this.owner;
	}

	/**
	 * Returns the primaryKeyJoinColumns.
	 * 
	 * @return the primaryKeyJoinColumns
	 * @since $version
	 */
	public PrimaryKeyJoinColumn[] getPrimaryKeyJoinColumns() {
		return this.primaryKeyJoinColumns;
	}

	/**
	 * Returns the primaryKeys.
	 * 
	 * @return the primaryKeys
	 * @since $version
	 */
	public List<PhysicalColumn> getPrimaryKeys() {
		return this.primaryKeys;
	}

	/**
	 * Returns the qualified name of the table.
	 * 
	 * @return the qualified name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQName() {
		if (StringUtils.isNotBlank(this.schema)) {
			return this.schema + "." + this.name;
		}

		return this.name;
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * @since $version
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the uniqueConstraints.
	 * 
	 * @return the uniqueConstraints
	 * @since $version
	 */
	public final UniqueConstraint[] getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * Returns the primary.
	 * 
	 * @return the primary
	 * @since $version
	 */
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "TableTemplate [schema=" + this.schema + ", name=" + this.name + ", primary=" + this.primary + ", owner="
			+ this.owner.getName() + ", primaryKeys=" + this.primaryKeys + ", columns=" + this.columns + ", uniqueConstraints="
			+ Arrays.toString(this.uniqueConstraints) + ", primaryKeyJoinColumns=" + Arrays.toString(this.primaryKeyJoinColumns) + "]";
	}

}

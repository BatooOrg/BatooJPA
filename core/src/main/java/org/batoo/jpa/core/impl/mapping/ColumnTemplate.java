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

import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.metamodel.AttributeImpl;

/**
 * Base class for column templates.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ColumnTemplate<X, T> {

	private final AttributeImpl<X, T> attribute;
	private final String tableName;
	private final String name;
	private final boolean nullable;
	private final boolean unique;
	private final boolean insertable;
	private final boolean updatable;
	private PhysicalColumn physicalColumn;

	/**
	 * Cloning constructor
	 * 
	 * @param attribute
	 *            the new attribute
	 * @param original
	 *            the original column template
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected ColumnTemplate(AttributeImpl<X, T> attribute, ColumnTemplate<?, T> original) {
		super();

		this.attribute = attribute;
		this.insertable = original.insertable;
		this.name = original.name;
		this.nullable = original.nullable;
		this.tableName = original.tableName;
		this.unique = original.unique;
		this.updatable = original.updatable;
	}

	/**
	 * @param attribute
	 *            the attribute that owns that column
	 * @param tableName
	 *            the name of the table
	 * @param name
	 *            the name of the column
	 * @param nullable
	 *            if column is nullable
	 * @param unique
	 *            if column is unique
	 * @param insertable
	 *            if column is insertable
	 * @param updatable
	 *            if column is updatable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnTemplate(AttributeImpl<X, T> attribute, String tableName, String name, boolean nullable, boolean unique,
		boolean insertable, boolean updatable) {
		super();

		this.attribute = attribute;
		this.tableName = tableName;
		this.name = name;
		this.nullable = nullable;
		this.unique = unique;
		this.insertable = insertable;
		this.updatable = updatable;
	}

	/**
	 * Clones this column template so that it is bound to the new attribute
	 * 
	 * @param attribute
	 *            the new attribıte
	 * @return the new column template
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract <Y> ColumnTemplate<Y, T> clone(AttributeImpl<Y, T> attribute);

	/**
	 * Returns the attribute.
	 * 
	 * @return the attribute
	 * @since $version
	 */
	public AttributeImpl<X, T> getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns the physical column.
	 * 
	 * @return the physical column
	 * @since $version
	 */
	public PhysicalColumn getPhysicalColumn() {
		return this.physicalColumn;
	}

	/**
	 * Returns the tableName.
	 * 
	 * @return the tableName
	 * @since $version
	 */
	public final String getTableName() {
		return this.tableName;
	}

	/**
	 * Returns the insertable.
	 * 
	 * @return the insertable
	 * @since $version
	 */
	public final boolean isInsertable() {
		return this.insertable;
	}

	/**
	 * Returns the nullable.
	 * 
	 * @return the nullable
	 * @since $version
	 */
	public final boolean isNullable() {
		return this.nullable;
	}

	/**
	 * Returns the unique.
	 * 
	 * @return the unique
	 * @since $version
	 */
	public final boolean isUnique() {
		return this.unique;
	}

	/**
	 * Returns the updatable.
	 * 
	 * @return the updatable
	 * @since $version
	 */
	public final boolean isUpdatable() {
		return this.updatable;
	}

	/**
	 * Sets the physical column for the column
	 * 
	 * @param physicalColumn
	 *            the physical column to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPhysicalColumn(PhysicalColumn physicalColumn) {
		this.physicalColumn = physicalColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ColumnTemplate [attribute=" + this.attribute + ", tableName=" + this.tableName + ", name=" + this.name + ", nullable="
			+ this.nullable + ", unique=" + this.unique + ", insertable=" + this.insertable + ", updatable=" + this.updatable + "]";
	}
}

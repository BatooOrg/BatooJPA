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

import java.util.Arrays;

import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

/**
 * An item representing a leaf on the query.
 * 
 * @author hceylan
 * @since $version
 */
public class QueryItem {

	/**
	 * Types for the query item
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum QueryItemType {
		EAGER,
		LAZY,
		INVERSE
	}

	private final Association<?, ?> association;
	private final EntityTypeImpl<?> type;
	private final QueryItem[] children;
	private final QueryItemType itemType;
	private final int tableNo;

	/**
	 * Constructor for eager items.
	 * 
	 * @param association
	 *            the association that binds this item to the parent, should be null for the root item
	 * @param tableNo
	 *            the no of the table of the item
	 * @param children
	 *            the children of the item
	 * @since $version
	 * @author hceylan
	 */
	public QueryItem(Association<?, ?> association, int tableNo, QueryItem[] children) {
		super();

		this.association = association;
		this.tableNo = tableNo;
		this.type = association.getType();
		this.children = children;

		this.itemType = QueryItemType.EAGER;
	}

	/**
	 * Constructor for lazy / inverse items.
	 * 
	 * @param association
	 *            the association that binds this item to the parent, should be null for the root item
	 * @param itemType
	 *            the item type of the item
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryItem(Association<?, ?> association, QueryItemType itemType) {
		super();

		this.association = association;
		this.itemType = itemType;

		this.type = association.getType();
		this.children = new QueryItem[0];
		this.tableNo = -1;
	}

	/**
	 * Constructor for the root item
	 * 
	 * @param type
	 * @param children
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryItem(EntityTypeImpl<?> type, QueryItem[] children) {
		super();

		this.type = type;
		this.children = children;

		this.association = null;
		this.itemType = QueryItemType.EAGER;
		this.tableNo = 0;
	}

	/**
	 * Returns the association.
	 * 
	 * @return the association
	 * @since $version
	 */
	public Association<?, ?> getAssociation() {
		return this.association;
	}

	/**
	 * Returns the children.
	 * 
	 * @return the children
	 * @since $version
	 */
	public QueryItem[] getChildren() {
		return this.children;
	}

	/**
	 * Returns the itemType.
	 * 
	 * @return the itemType
	 * @since $version
	 */
	public QueryItemType getItemType() {
		return this.itemType;
	}

	/**
	 * Returns the tableNo.
	 * 
	 * @return the tableNo
	 * @since $version
	 */
	public int getTableNo() {
		return this.tableNo;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 * @since $version
	 */
	public EntityTypeImpl<?> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String pathAsString = this.association != null ? this.association.getPathAsString() : null;

		return "QueryItem [type=" + this.type.getName() + ", tableNo=" + this.tableNo + ", association=" + pathAsString + ", itemType="
			+ this.itemType + ", children=" + Arrays.toString(this.children) + "]";
	}

}

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
package org.batoo.jpa.parser.impl.metadata;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.BaseColumnMetadata;

/**
 * Implementation of {@link BaseColumnMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class BaseColumnMetadataImpl implements BaseColumnMetadata {

	private final JavaLocator locator;
	private final String columnDefinition;

	private final String name;
	private final String table;
	private final boolean insertable;
	private final boolean nullable;
	private final boolean unique;
	private final boolean updatable;

	/**
	 * @param locator
	 *            the java locator
	 * @param table
	 *            the optional name of the table
	 * @param name
	 *            the optional name of the column
	 * @param columnDefinition
	 *            the optional column definition
	 * @param insertable
	 *            if the column is insertable
	 * @param nullable
	 *            if the column is nullable
	 * @param updatable
	 *            if the column is updatable
	 * @param unique
	 *            if the column is unique
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseColumnMetadataImpl(JavaLocator locator, String table, String name, String columnDefinition, boolean insertable,
		boolean nullable, boolean updatable, boolean unique) {
		super();

		this.locator = locator;
		this.table = table;
		this.name = name;
		this.columnDefinition = columnDefinition;
		this.insertable = insertable;
		this.nullable = nullable;
		this.updatable = updatable;
		this.unique = unique;
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
	public String getLocation() {
		return this.locator.getLocation();
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
	public String getTable() {
		return this.table;
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
}

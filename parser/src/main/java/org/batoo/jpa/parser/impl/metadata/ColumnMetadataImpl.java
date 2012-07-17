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

import javax.persistence.Column;
import javax.persistence.OrderColumn;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Implementation of {@link ColumnMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class ColumnMetadataImpl implements ColumnMetadata {

	private final AbstractLocator locator;
	private final String columnDefinition;
	private final boolean insertable;
	private final int length;
	private final String name;
	private final boolean nullable;
	private final int precision;
	private final int scale;
	private final String table;
	private final boolean unique;
	private final boolean updatable;

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadataImpl(AbstractLocator locator, Column annotation) {
		super();

		this.locator = locator;
		this.columnDefinition = annotation.columnDefinition();
		this.insertable = annotation.insertable();
		this.length = annotation.length();
		this.name = annotation.name();
		this.nullable = annotation.nullable();
		this.precision = annotation.precision();
		this.scale = annotation.scale();
		this.table = annotation.table();
		this.unique = annotation.unique();
		this.updatable = annotation.updatable();
	}

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadataImpl(AbstractLocator locator, OrderColumn annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.columnDefinition = annotation.columnDefinition();
		this.insertable = annotation.insertable();
		this.nullable = annotation.nullable();
		this.updatable = annotation.updatable();

		this.length = 0;
		this.precision = 0;
		this.scale = 0;
		this.table = null;
		this.unique = false;
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

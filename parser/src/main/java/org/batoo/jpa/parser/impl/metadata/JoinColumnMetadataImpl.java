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

import javax.persistence.JoinColumn;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

/**
 * Implementation of {@link JoinColumnMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinColumnMetadataImpl implements JoinColumnMetadata {

	private final AbstractLocator locator;
	private final String columnDefinition;
	private final boolean insertable;
	private final String name;
	private final boolean nullable;
	private final String referencedColumnName;
	private final String table;
	private final boolean unique;
	private final boolean updatable;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumnMetadataImpl(AbstractLocator locator, JoinColumn annotation) {
		super();

		this.locator = locator;
		this.columnDefinition = annotation.columnDefinition();
		this.insertable = annotation.insertable();
		this.name = annotation.name();
		this.nullable = annotation.nullable();
		this.referencedColumnName = annotation.referencedColumnName();
		this.table = annotation.table();
		this.unique = annotation.unique();
		this.updatable = annotation.updatable();
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
	public String getReferencedColumnName() {
		return this.referencedColumnName;
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

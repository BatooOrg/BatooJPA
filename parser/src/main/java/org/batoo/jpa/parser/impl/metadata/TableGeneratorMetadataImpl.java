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

import java.util.List;

import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link TableGeneratorMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class TableGeneratorMetadataImpl implements TableGeneratorMetadata {

	private final AbstractLocator locator;
	private final String catalog;
	private final String schema;
	private final String name;
	private final String pkColumnName;
	private final String pkColumnValue;
	private final String valueColumnName;
	private final int initialValue;
	private final int allocationSize;
	private final String table;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableGeneratorMetadataImpl(AbstractLocator locator, TableGenerator annotation) {
		this.locator = locator;
		this.catalog = annotation.catalog();
		this.schema = annotation.schema();
		this.table = annotation.table();
		this.name = annotation.name();
		this.pkColumnName = annotation.pkColumnName();
		this.pkColumnValue = annotation.pkColumnValue();
		this.valueColumnName = annotation.valueColumnName();
		this.initialValue = annotation.initialValue();
		this.allocationSize = annotation.allocationSize();

		for (final UniqueConstraint constraint : annotation.uniqueConstraints()) {
			this.uniqueConstraints.add(new UniqueConstraintMetadataImpl(locator, constraint));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getAllocationSize() {
		return this.allocationSize;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getInitialValue() {
		return this.initialValue;
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
	public String getPkColumnName() {
		return this.pkColumnName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPkColumnValue() {
		return this.pkColumnValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() {
		return this.schema;
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
	public List<UniqueConstraintMetadata> getUniqueConstraints() {
		return this.uniqueConstraints;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getValueColumnName() {
		return this.valueColumnName;
	}
}

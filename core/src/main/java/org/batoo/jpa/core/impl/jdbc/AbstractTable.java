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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Abstract implementation for Entity, Secondary and Join tables.
 * 
 * @author hceylan
 * @since $version
 */
public class AbstractTable {

	private final AbstractLocator locator;

	private final String catalog;
	private final String schema;
	private final String name;
	private final Map<String, BasicColumn> basicColumns = Maps.newHashMap();
	private final Map<String, String[]> uniqueConstraints = Maps.newHashMap();
	private final List<ForeignKey> foreignKeys = Lists.newArrayList();

	/**
	 * @param defaultName
	 *            the default name for the table
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable(String defaultName, TableMetadata metadata) {
		super();

		final String name = (metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName() : defaultName;
		this.locator = metadata != null ? metadata.getLocator() : null;
		this.catalog = (metadata != null) && StringUtils.isNotBlank(metadata.getCatalog()) ? metadata.getCatalog() : null;
		this.schema = (metadata != null) && StringUtils.isNotBlank(metadata.getSchema()) ? metadata.getSchema() : null;
		this.name = name;

		if (metadata != null) {
			for (final UniqueConstraintMetadata constraint : metadata.getUniqueConstraints()) {
				this.uniqueConstraints.put(constraint.getName(), constraint.getColumnNames());
			}
		}
	}

	/**
	 * Adds the column to the table
	 * 
	 * @param basicColumn
	 *            the column to add
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addColumn(BasicColumn basicColumn) {
		final BasicColumn existing = this.basicColumns.put(basicColumn.getName(), basicColumn);

		if (existing != null) {
			throw new MappingException("Duplicate column names " + basicColumn.getName() + " on table " + this.name, basicColumn.getLocator(),
				existing.getLocator());
		}
	}

	/**
	 * Adds a foreign key to the table.
	 * 
	 * @param foreignKey
	 *            the foreign key to add
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addForeignKey(ForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
	}

	/**
	 * Returns the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Returns the collection of basicColumns of the table.
	 * 
	 * @return the collection of basicColumns of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<BasicColumn> getColumns() {
		return this.basicColumns.values();
	}

	/**
	 * Returns the locator.
	 * 
	 * @return the locator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
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
		return Joiner.on(".").skipNulls().join(this.schema, this.name);
	}

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Returns the unique constraints.
	 * 
	 * @return the unique constraints
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Map<String, String[]> getUniqueConstraints() {
		return this.uniqueConstraints;
	}
}

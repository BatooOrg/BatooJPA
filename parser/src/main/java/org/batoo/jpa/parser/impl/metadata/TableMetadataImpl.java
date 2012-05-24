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

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * PersistenceParser for @Table annotation that parses and merges the optional ORM File provided metadata.
 * 
 * @author hceylan
 * @since $version
 */
public class TableMetadataImpl implements TableMetadata {

	private final String name;
	private final JavaLocator locator;
	private final String schema;
	private final String catalog;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param clazz
	 *            the class
	 * @param table
	 *            the table annotation
	 * @param metadata
	 *            the optional metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableMetadataImpl(Class<?> clazz, Table table, TableMetadata metadata) {
		super();

		// if metadata exists use the metadata
		if (metadata != null) {
			this.catalog = metadata.getCatalog();
			this.schema = metadata.getSchema();
			this.name = metadata.getName();
			this.locator = new JavaLocator(clazz, metadata.getLocation());
			this.uniqueConstraints.addAll(metadata.getUniqueConstraints());
		}
		// use the table annotation obtained from the class
		else {
			this.locator = new JavaLocator(clazz, null);

			this.catalog = table.catalog();
			this.schema = table.catalog();
			this.name = table.name();

			for (final UniqueConstraint constraint : table.uniqueConstraints()) {
				this.uniqueConstraints.add(new UniqueConstraintMetadataImpl(this.locator, constraint));
			}
		}
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
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<UniqueConstraintMetadata> getUniqueConstraints() {
		return this.uniqueConstraints;
	}

}

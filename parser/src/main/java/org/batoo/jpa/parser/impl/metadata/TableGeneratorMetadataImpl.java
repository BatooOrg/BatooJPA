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

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Annotated definition of table generators.
 * 
 * @author hceylan
 * @since $version
 */
public class TableGeneratorMetadataImpl implements TableGeneratorMetadata {

	private final JavaLocator locator;
	private final TableGenerator tableGenerator;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param locator
	 *            the java locator
	 * @param tableGenerator
	 *            the obtained {@link TableGenerator} annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableGeneratorMetadataImpl(JavaLocator locator, TableGenerator tableGenerator) {
		super();

		this.locator = locator;
		this.tableGenerator = tableGenerator;

		for (final UniqueConstraint uniqueConstraint : tableGenerator.uniqueConstraints()) {
			this.uniqueConstraints.add(new UniqueConstraintMetadataImpl(locator, uniqueConstraint));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getAllocationSize() {
		return this.tableGenerator.allocationSize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.tableGenerator.catalog();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getInitialValue() {
		return this.tableGenerator.initialValue();
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
		return this.tableGenerator.name();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPkColumnName() {
		return this.tableGenerator.pkColumnName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPkColumnValue() {
		return this.tableGenerator.pkColumnValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() {
		return this.tableGenerator.schema();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTable() {
		return this.tableGenerator.table();
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
		return this.tableGenerator.valueColumnName();
	}

}

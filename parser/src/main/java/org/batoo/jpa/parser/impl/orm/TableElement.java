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
package org.batoo.jpa.parser.impl.orm;

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Base Element for <code>table</code>, <code>join-table</code> and <code>secondary-table</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class TableElement extends ParentElement implements TableMetadata {

	private String catalog;
	private String schema;
	private String name;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param expectedChildElements
	 *            the name of the elements expected
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, ElementConstants.ELEMENT_UNIQUE_CONSTRAINT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.catalog = this.getAttribute(ElementConstants.ATTR_CATALOG, ElementConstants.EMPTY);
		this.schema = this.getAttribute(ElementConstants.ATTR_SCHEMA, ElementConstants.EMPTY);
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof UniqueConstraintsElement) {
			this.uniqueConstraints.add((UniqueConstraintMetadata) child);
		}
	}
}

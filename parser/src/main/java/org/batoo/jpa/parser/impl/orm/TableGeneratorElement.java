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

import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>sequence-generator</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class TableGeneratorElement extends GeneratorElement implements TableGeneratorMetadata {

	private String table;
	private String pkColumnName;
	private String pkColumnValue;
	private String valueColumnName;
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableGeneratorElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ElementConstants.ELEMENT_UNIQUE_CONSTRAINT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.table = this.getAttribute(ElementConstants.ATTR_TABLE, ElementConstants.EMPTY);
		this.pkColumnName = this.getAttribute(ElementConstants.ATTR_PK_COLUMN_NAME, ElementConstants.EMPTY);
		this.pkColumnValue = this.getAttribute(ElementConstants.ATTR_PK_COLUMN_VALUE, ElementConstants.EMPTY);
		this.valueColumnName = this.getAttribute(ElementConstants.ATTR_VALUE_COLUMN_NAME, ElementConstants.EMPTY);
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof UniqueConstraintsElement) {
			this.uniqueConstraints.add((UniqueConstraintsElement) child);
		}
	}
}

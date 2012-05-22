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

import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Element factory for <code>version</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class UniqueConstraintsElementFactory extends ParentElementFactory implements UniqueConstraintMetadata {

	private String name;
	private final List<String> columnNames = Lists.newArrayList();

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public UniqueConstraintsElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes, ElementFactoryConstants.ELEMENT_COLUMN_NAME);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementFactoryConstants.ATTR_NAME, ElementFactoryConstants.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getColumnNames() {
		return this.columnNames.toArray(new String[this.columnNames.size()]);
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
	protected void handleChild(ElementFactory child) {
		if (child instanceof ColumnNameElementFactory) {
			this.columnNames.add(((ColumnNameElementFactory) child).getName());
		}
	}
}

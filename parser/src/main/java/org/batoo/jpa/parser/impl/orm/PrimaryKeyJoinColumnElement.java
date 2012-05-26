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

import java.util.Map;

import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

/**
 * Element for <code>primary-key-join-column</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class PrimaryKeyJoinColumnElement extends ChildElement implements PrimaryKeyJoinColumnMetadata {

	private String name;
	private String columnDefinition;
	private String referencedColumnName;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PrimaryKeyJoinColumnElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
		this.columnDefinition = this.getAttribute(ElementConstants.ATTR_COLUMN_DEFINITION, ElementConstants.EMPTY);
		this.referencedColumnName = this.getAttribute(ElementConstants.ATTR_REFERENCED_COLUMN_NAME, ElementConstants.EMPTY);
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
}

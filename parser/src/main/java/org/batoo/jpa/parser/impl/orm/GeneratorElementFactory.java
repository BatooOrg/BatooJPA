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

import org.batoo.jpa.parser.metadata.GeneratorMetadata;

/**
 * Element factory for <code>sequence-generator</code> and <code>table-generator</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class GeneratorElementFactory extends ParentElementFactory implements GeneratorMetadata {

	private String name;
	private String catalog;
	private String schema;
	private int allocationSize;
	private int initialValue;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public GeneratorElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param childElement
	 *            the name of the element expected
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public GeneratorElementFactory(ParentElementFactory parent, Map<String, String> attributes, String childElement) {
		super(parent, attributes, childElement);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ATTR_GENERATOR, EMPTY);
		this.catalog = this.getAttribute(ATTR_CATALOG, EMPTY);
		this.schema = this.getAttribute(ATTR_SCHEMA, EMPTY);
		this.allocationSize = this.getAttribute(ATTR_ALLOCATION_SIZE, 50);
		this.initialValue = this.getAttribute(ATTR_INITIAL_VALUE, 1);
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
	 * Returns the initialValue of the GeneratorElementFactory.
	 * 
	 * @return the initialValue of the GeneratorElementFactory
	 * 
	 * @since $version
	 * @author hceylan
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
	protected void handleChild(ElementFactory child) {
		// noop
	}

}

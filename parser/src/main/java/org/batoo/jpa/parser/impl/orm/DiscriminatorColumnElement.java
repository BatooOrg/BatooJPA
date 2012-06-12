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

import javax.persistence.DiscriminatorType;

import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;

/**
 * Element for <code>discriminator-column</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class DiscriminatorColumnElement extends ChildElement implements DiscriminatorColumnMetadata {

	private String name;
	private DiscriminatorType discriminatorType;
	private String columnDefinition;
	private int length;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DiscriminatorColumnElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, "DTYPE");
		this.columnDefinition = this.getAttribute(ElementConstants.ATTR_COLUMN_DEFINITION);
		this.discriminatorType = DiscriminatorType.valueOf(this.getAttribute(ElementConstants.ATTR_DISCRIMINATOR_TYPE,
			DiscriminatorType.STRING.name()));
		this.length = this.getAttribute(ElementConstants.ATTR_LENGTH, 31);
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
	public DiscriminatorType getDiscriminatorType() {
		return this.discriminatorType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}
}

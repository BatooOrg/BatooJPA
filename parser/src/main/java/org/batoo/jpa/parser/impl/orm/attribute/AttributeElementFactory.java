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
package org.batoo.jpa.parser.impl.orm.attribute;

import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.orm.ElementFactoryConstants;
import org.batoo.jpa.parser.impl.orm.ParentElementFactory;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Common implementation of attribute element factories.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AttributeElementFactory extends ParentElementFactory implements AttributeMetadata {

	private String name;
	private AccessType access;

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
	public AttributeElementFactory(ParentElementFactory parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, expectedChildElements);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object object) {
		// if the object is a string provide equality based on name and object equality
		if (object instanceof String) {
			return this.name.equals(object);
		}

		// provide equality based on this attribute's name and the object's name
		return this.name.equals(((AttributeMetadata) object).getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementFactoryConstants.ATTR_NAME, ElementFactoryConstants.EMPTY);
		this.access = this.getAttribute(ElementFactoryConstants.ATTR_ACCESS) != null
			? AccessType.valueOf(this.getAttribute(ElementFactoryConstants.ATTR_ACCESS)) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccess() {
		return this.access;
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

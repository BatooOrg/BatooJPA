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

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.impl.orm.AssociationOverrideElement;
import org.batoo.jpa.parser.impl.orm.AttributeOverrideElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>embedded-id</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttributeElement extends AttributeElement implements EmbeddedAttributeMetadata {

	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();

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
	public EmbeddedAttributeElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_ATTRIBUTE_OVERRIDE,//
			ElementConstants.ELEMENT_ASSOCIATION_OVERRIDE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AssociationMetadata> getAssociationOverrides() {
		return this.associationOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return this.attributeOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof AttributeOverrideElement) {
			this.attributeOverrides.add((AttributeOverrideMetadata) child);
		}

		if (child instanceof AssociationOverrideElement) {
			this.associationOverrides.add((AssociationMetadata) child);
		}
	}
}

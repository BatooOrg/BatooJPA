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

import javax.persistence.FetchType;

import org.batoo.jpa.parser.impl.orm.ElementFactoryConstants;
import org.batoo.jpa.parser.impl.orm.ParentElementFactory;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;

/**
 * Element factory for <code>many-to-one</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToOneAttributeElementFactory extends AssociationElementFactory implements ManyToOneAttributeMetadata {

	private boolean optional;

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
	public ManyToOneAttributeElementFactory(ParentElementFactory parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, FetchType.EAGER, //
			ElementFactoryConstants.ELEMENT_CASCADE,//
			ElementFactoryConstants.ELEMENT_JOIN_COLUMN, //
			ElementFactoryConstants.ELEMENT_JOIN_TABLE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.optional = this.getAttribute(ElementFactoryConstants.ATTR_OPTIONAL, Boolean.TRUE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}

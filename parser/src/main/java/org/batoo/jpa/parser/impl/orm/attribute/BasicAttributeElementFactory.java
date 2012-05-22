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

import javax.persistence.EnumType;

import org.batoo.jpa.parser.impl.orm.ElementFactory;
import org.batoo.jpa.parser.impl.orm.ElementFactoryConstants;
import org.batoo.jpa.parser.impl.orm.EnumeratedElementFactory;
import org.batoo.jpa.parser.impl.orm.LobElementFactory;
import org.batoo.jpa.parser.impl.orm.ParentElementFactory;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * Element factory for <code>basic</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttributeElementFactory extends SingularAttributeElementFactory implements BasicAttributeMetadata {

	private EnumType enumType = EnumType.ORDINAL;
	private boolean lob = false;
	private boolean optional = true;

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttributeElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementFactoryConstants.ELEMENT_TEMPORAL, //
			ElementFactoryConstants.ELEMENT_ENUMERATED, //
			ElementFactoryConstants.ELEMENT_COLUMN, //
			ElementFactoryConstants.ELEMENT_LOB);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.optional = this.getAttribute(ATTR_OPTIONAL, true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(ElementFactory child) {
		super.handleChild(child);

		if (child instanceof EnumeratedElementFactory) {
			this.enumType = ((EnumeratedElementFactory) child).getEnumType();
		}

		if (child instanceof LobElementFactory) {
			this.lob = true;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLob() {
		return this.lob;
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

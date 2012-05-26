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
import javax.persistence.FetchType;

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.EnumeratedElement;
import org.batoo.jpa.parser.impl.orm.LobElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * Element for <code>basic</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttributeElement extends PhysicalAttributeElement implements BasicAttributeMetadata {

	private EnumType enumType = EnumType.ORDINAL;
	private boolean lob = false;
	private boolean optional = true;
	private FetchType fetchType;

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttributeElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_TEMPORAL, //
			ElementConstants.ELEMENT_ENUMERATED, //
			ElementConstants.ELEMENT_COLUMN, //
			ElementConstants.ELEMENT_LOB);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.fetchType = FetchType.valueOf(this.getAttribute(ElementConstants.ATTR_FETCH, FetchType.EAGER.name()));
		this.optional = this.getAttribute(ElementConstants.ATTR_OPTIONAL, true);
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
	public FetchType getFetchType() {
		return this.fetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		super.handleChild(child);

		if (child instanceof EnumeratedElement) {
			this.enumType = ((EnumeratedElement) child).getEnumType();
		}

		if (child instanceof LobElement) {
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

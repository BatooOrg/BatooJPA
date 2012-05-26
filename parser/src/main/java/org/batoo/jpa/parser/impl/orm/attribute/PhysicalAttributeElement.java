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

import javax.persistence.TemporalType;

import org.batoo.jpa.parser.impl.orm.ColumnElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.TemporalElement;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.PhysicalAttributeMetadata;

/**
 * Common implementation of singular attribute element factories.
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalAttributeElement extends AttributeElement implements PhysicalAttributeMetadata {

	private TemporalType temporalType;
	private ColumnMetadata column;

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
	public PhysicalAttributeElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, expectedChildElements);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof TemporalElement) {
			this.temporalType = ((TemporalElement) child).getTemporalType();
		}

		if (child instanceof ColumnElement) {
			this.column = (ColumnElement) child;
		}
	}
}

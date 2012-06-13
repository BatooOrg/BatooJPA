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
package org.batoo.jpa.parser.impl.orm.type;

import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.EntityMappings;
import org.batoo.jpa.parser.impl.orm.IdClassElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.attribute.AttributesElement;
import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

/**
 * Element for <code>mapped-superclass</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class MappedSuperclassElementFactory extends ParentElement implements MappedSuperclassMetadata {

	private String className;
	private boolean metadataComplete;
	private AccessType accessType;
	private AttributesElement attrs;
	private String idClass;

	/**
	 * Constructor for ORM File parsing
	 * 
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MappedSuperclassElementFactory(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_ATTRIBUTES, //
			ElementConstants.ELEMENT_ID_CLASS);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.className = this.getAttribute(ElementConstants.ATTR_CLASS, ElementConstants.EMPTY);
		this.metadataComplete = this.getAttribute(ElementConstants.ATTR_METADATA_COMPLETE, false);
		this.accessType = this.getAttribute(ElementConstants.ATTR_ACCESS) != null
			? AccessType.valueOf(this.getAttribute(ElementConstants.ATTR_ACCESS)) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return this.accessType != null ? this.accessType : ((EntityMappings) this.getParent()).getAccessType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesElement getAttributes() {
		return this.attrs;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClassName() {
		return this.className;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getIdClass() {
		return this.idClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof AttributesElement) {
			this.attrs = (AttributesElement) child;
		}

		if (child instanceof IdClassElement) {
			this.idClass = ((IdClassElement) child).getIdClass();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		return this.metadataComplete;
	}
}

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

import org.batoo.jpa.parser.impl.orm.ElementFactory;
import org.batoo.jpa.parser.impl.orm.ElementFactoryConstants;
import org.batoo.jpa.parser.impl.orm.EntityMappingsFactory;
import org.batoo.jpa.parser.impl.orm.ParentElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.AttributesElementFactory;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

/**
 * Element factory for <code>entity-mappings</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityElementFactory extends ParentElementFactory implements EntityMetadata {

	private String name;
	private String className;
	private boolean metadataComplete;
	private Boolean cachable;
	private AccessType accessType;

	private AttributesElementFactory attrs;

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
	public EntityElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementFactoryConstants.ELEMENT_ACCESS, //
			ElementFactoryConstants.ELEMENT_ATTRIBUTES);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementFactoryConstants.ATTR_NAME, ElementFactoryConstants.EMPTY);
		this.className = this.getAttribute(ElementFactoryConstants.ATTR_CLASS, ElementFactoryConstants.EMPTY);
		this.metadataComplete = this.getAttribute(ElementFactoryConstants.ATTR_METADATA_COMPLETE, false);
		this.cachable = this.getAttribute(ElementFactoryConstants.ATTR_CACHABLE) != null
			? Boolean.valueOf(this.getAttribute(ElementFactoryConstants.ATTR_CACHABLE)) : null;
		this.accessType = this.getAttribute(ElementFactoryConstants.ATTR_ACCESS) != null
			? AccessType.valueOf(this.getAttribute(ElementFactoryConstants.ATTR_ACCESS)) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return this.accessType != null ? this.accessType : ((EntityMappingsFactory) this.getParent()).getAccessType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesElementFactory getAttributes() {
		return this.attrs;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean getCachable() {
		return this.cachable;
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
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(ElementFactory child) {
		if (child instanceof AttributesElementFactory) {
			this.attrs = (AttributesElementFactory) child;
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

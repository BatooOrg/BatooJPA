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

import java.util.List;
import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.impl.metadata.Metadata;
import org.batoo.jpa.parser.impl.orm.type.EntityElementFactory;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;

/**
 * Element factory for <code>entity-mappings</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityMappingsFactory extends ParentElementFactory implements Metadata {

	private AccessType accessType;
	private final List<EntityMetadata> entities = Lists.newArrayList();

	/**
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityMappingsFactory(Map<String, String> attributes) {
		super(null, attributes, //
			ElementFactoryConstants.ELEMENT_ACCESS, //
			ElementFactoryConstants.ELEMENT_ENTITY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return this.accessType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityMetadata> getEntities() {
		return this.entities;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(ElementFactory child) {
		if (child instanceof AccessElementFactory) {
			this.accessType = ((AccessElementFactory) child).getAccessType();
		}

		if (child instanceof EntityElementFactory) {
			this.entities.add((EntityMetadata) child);
		}
	}
}

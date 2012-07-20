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
package org.batoo.jpa.core.impl.model.mapping;

import java.util.Iterator;

import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.base.Splitter;

/**
 * Mapping for the entities.
 * 
 * @param <X>
 *            the type of the entity
 * 
 * @author hceylan
 * @since $version
 */
public class EntityMapping<X> extends ParentMapping<X, X> implements RootMapping<X> {

	private final EntityTypeImpl<X> entity;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public EntityMapping(EntityTypeImpl<X> entity) {
		super(null, null, entity.getJavaType(), entity.getName());

		this.entity = entity;

		// inherit the mappings
		if (!entity.isRoot()) {
			this.inherit(((EntityMapping<X>) entity.getParent().getRootMapping()).getChildren());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributeImpl<? super X, X> getAttribute() {
		return null; // N/A
	}

	/**
	 * Returns the entity of the root mapping.
	 * 
	 * @return the entity of the root mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<X> getEntity() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping(String path) {
		final Iterator<String> segments = Splitter.on('.').split(path).iterator();
		Mapping<?, ?, ?> mapping = this;
		while (segments.hasNext()) {
			if (mapping instanceof ParentMapping) {
				mapping = ((ParentMapping<?, ?>) mapping).getChild(segments.next());

				if (mapping == null) {
					return null;
				}
			}
			else {
				return null;
			}
		}

		return mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityMapping<?> getRoot() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getType() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEntity() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return false;
	}
}

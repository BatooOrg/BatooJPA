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
package org.batoo.jpa.parser.impl.annotated;

import java.util.List;
import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.Metadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The implementation of {@link Metadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class MetadataImpl implements Metadata {

	private final Map<String, EntityMetadata> entities = Maps.newHashMap();

	/**
	 * Adds the entity, replacing existing if required
	 * 
	 * @param entity
	 *            the entity to add
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void add(EntityMetadata entity) {
		this.entities.put(entity.getClassName(), entity);
	}

	/**
	 * Returns if the type is already contained in the metamodel.
	 * 
	 * @param className
	 *            the name of the class
	 * @return true if the type is already contained in the metamodel, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean contains(String className) {
		return this.entities.containsKey(className);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return AccessType.FIELD;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityMetadata> getEntities() {
		return Lists.newArrayList(this.entities.values());
	}

	/**
	 * Merges the <code>metadata</code> into global metadata.
	 * 
	 * @param metadata
	 *            the metadata to merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void merge(Metadata metadata) {
		for (final EntityMetadata entity : metadata.getEntities()) {
			final EntityMetadata existing = this.entities.put(entity.getClassName(), entity);

			if (existing != null) {
				throw new MappingException("Duplicate definitions for " + entity.getClassName(), entity.getLocation(),
					existing.getLocation());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this).toString();
	}
}

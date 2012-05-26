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
package org.batoo.jpa.parser.impl.metadata;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.metadata.Metadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of {@link Metadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class MetadataImpl implements Metadata {

	private final Map<String, EntityMetadata> entityMap = Maps.newHashMap();

	/**
	 * @param classes
	 *            the explicit classes obtained from the Persistence XML
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MetadataImpl(List<String> classes) {
		super();

		for (final String clazz : classes) {
			this.entityMap.put(clazz, null);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityMetadata> getEntities() {
		return Lists.newArrayList(this.entityMap.values());
	}

	/**
	 * Merges the ORM XML based metadata.
	 * 
	 * @param metadata
	 *            the metadata to merge
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void merge(Metadata metadata) {
		for (final EntityMetadata entity : metadata.getEntities()) {
			final EntityMetadata existing = this.entityMap.put(entity.getClassName(), entity);

			if (existing != null) {
				if (existing != null) {
					throw new MappingException("Duplicate definitions for " + entity.getClassName(), entity.getLocator(),
						existing.getLocator());
				}
			}
		}
	}

	/**
	 * Parses the types in the metamodel.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void parse() {
		for (final Entry<String, EntityMetadata> entry : this.entityMap.entrySet()) {
			final String className = entry.getKey();
			final EntityMetadata metadata = entry.getValue();

			try {
				final Class<?> clazz = Class.forName(className);
				this.entityMap.put(className, new EntityMetadataImpl(clazz, metadata));
			}
			catch (final ClassNotFoundException e) { // class could not be found
				throw new MappingException("Class " + className + " cound not be found.", metadata.getLocator());
			}

		}
	}

}

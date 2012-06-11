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
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.MappedSuperclassMetadataImpl;
import org.batoo.jpa.parser.metadata.Metadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;
import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of {@link Metadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class MetadataImpl implements Metadata {

	private final Map<String, ManagedTypeMetadata> entityMap = Maps.newHashMap();

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
	public List<ManagedTypeMetadata> getEntityMappings() {
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
		for (final ManagedTypeMetadata managedType : metadata.getEntityMappings()) {
			final ManagedTypeMetadata existing = this.entityMap.put(managedType.getClassName(), managedType);

			if (existing != null) {
				if (existing != null) {
					throw new MappingException("Duplicate definitions for " + managedType.getClassName(), managedType.getLocator(),
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
		for (final Entry<String, ManagedTypeMetadata> entry : this.entityMap.entrySet()) {
			final String className = entry.getKey();
			final ManagedTypeMetadata metadata = entry.getValue();

			try {
				final Class<?> clazz = Class.forName(className);
				if (metadata == null) {
					if (clazz.getAnnotation(Entity.class) != null) {
						this.entityMap.put(className, new EntityMetadataImpl(clazz, (EntityMetadata) metadata));
					}
					else if (clazz.getAnnotation(MappedSuperclass.class) != null) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata));
					}
					else {
						throw new MappingException("Cannot determine type of class " + className);
					}
				}
				else {
					if (metadata instanceof EntityMetadata) {
						this.entityMap.put(className, new EntityMetadataImpl(clazz, (EntityMetadata) metadata));
					}
					else if (metadata instanceof MappedSuperclassMetadata) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata));
					}
				}
			}
			catch (final ClassNotFoundException e) { // class could not be found
				throw new MappingException("Class " + className + " cound not be found.", metadata.getLocator());
			}

		}
	}
}

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
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.type.EmbeddableMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.MappedSuperclassMetadataImpl;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.Metadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.type.EmbeddableMetadata;
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

	private AccessType accessType;
	private boolean xmlMappingMetadataComplete;
	private String schema;
	private String catalog;

	private final List<SequenceGeneratorMetadata> sequenceGenerators = Lists.newArrayList();
	private final List<TableGeneratorMetadata> tableGenerators = Lists.newArrayList();

	private final List<NamedQueryMetadata> namedQueries = Lists.newArrayList();
	private final List<EntityListenerMetadata> entityListeners = Lists.newArrayList();
	private final Map<String, ManagedTypeMetadata> entityMap = Maps.newHashMap();
	private boolean cascadePersist;

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
	public boolean cascadePersists() {
		return this.cascadePersist;
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
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EntityListenerMetadata> getEntityListeners() {
		return this.entityListeners;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<NamedQueryMetadata> getNamedQueries() {
		return this.namedQueries;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<SequenceGeneratorMetadata> getSequenceGenerators() {
		return this.sequenceGenerators;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<TableGeneratorMetadata> getTableGenerators() {
		return this.tableGenerators;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isXmlMappingMetadataComplete() {
		return this.xmlMappingMetadataComplete;
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
		this.accessType = metadata.getAccessType();
		this.catalog = metadata.getCatalog();
		this.schema = metadata.getSchema();

		this.xmlMappingMetadataComplete = metadata.isXmlMappingMetadataComplete();
		this.cascadePersist = metadata.cascadePersists();

		this.sequenceGenerators.addAll(metadata.getSequenceGenerators());
		this.tableGenerators.addAll(metadata.getTableGenerators());

		this.entityListeners.addAll(metadata.getEntityListeners());
		this.namedQueries.addAll(metadata.getNamedQueries());

		for (final ManagedTypeMetadata managedType : metadata.getEntityMappings()) {
			final ManagedTypeMetadata existing = this.entityMap.put(managedType.getClassName(), managedType);

			if (existing != null) {
				if (existing != null) {
					throw new MappingException("Duplicate definitions for " + managedType.getClassName(), managedType.getLocator(), existing.getLocator());
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
		this.parseDefaultListeners();

		for (final Entry<String, ManagedTypeMetadata> entry : this.entityMap.entrySet()) {
			final String className = entry.getKey();
			final ManagedTypeMetadata metadata = entry.getValue();

			try {
				final Class<?> clazz = Class.forName(className);
				if (metadata == null) {
					if (clazz.getAnnotation(Entity.class) != null) {
						final EntityMetadataImpl entityMetadata = new EntityMetadataImpl(clazz, (EntityMetadata) metadata);
						this.entityMap.put(className, entityMetadata);

						this.namedQueries.addAll(entityMetadata.getNamedQueries());
					}
					else if (clazz.getAnnotation(MappedSuperclass.class) != null) {
						this.entityMap.put(className, new MappedSuperclassMetadataImpl(clazz, (MappedSuperclassMetadata) metadata));
					}
					else if (clazz.getAnnotation(Embeddable.class) != null) {
						this.entityMap.put(className, new EmbeddableMetadataImpl(clazz, (EmbeddableMetadata) metadata));
					}
					else {
						throw new MappingException("Cannot determine type of class " + className);
					}
				}
				else {
					if (metadata instanceof EntityMetadata) {
						this.entityMap.put(className, new EntityMetadataImpl(clazz, (EntityMetadata) metadata));

						this.namedQueries.addAll(((EntityMetadata) metadata).getNamedQueries());
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

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void parseDefaultListeners() {
		// TODO Auto-generated method stub

	}
}

/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.parser.impl.orm;

import java.util.List;
import java.util.Map;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.Metadata;
import org.batoo.jpa.parser.metadata.NamedNativeQueryMetadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.type.EmbeddableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;
import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>entity-mappings</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityMappings extends ParentElement implements Metadata {

	private AccessType accessType = AccessType.FIELD;
	private boolean xmlMappingMetadataComplete;
	private String catalog;
	private String schema;

	private final List<SequenceGeneratorMetadata> sequenceGenerators = Lists.newArrayList();
	private final List<TableGeneratorMetadata> tableGenerators = Lists.newArrayList();

	private final List<NamedQueryMetadata> namedQueries = Lists.newArrayList();
	private final List<NamedNativeQueryMetadata> namedNativeQueries = Lists.newArrayList();
	private final List<EntityListenerMetadata> entityListeners = Lists.newArrayList();
	private final List<ManagedTypeMetadata> entities = Lists.newArrayList();
	private boolean cascadePersist;

	/**
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public EntityMappings(Map<String, String> attributes) {
		super(null, attributes, //
			ElementConstants.ELEMENT_ACCESS, //
			ElementConstants.ELEMENT_CATALOG, //
			ElementConstants.ELEMENT_SCHEMA, //
			ElementConstants.ELEMENT_PERSISTENT_UNIT_METADATA, //
			ElementConstants.ELEMENT_SEQUENCE_GENERATOR, //
			ElementConstants.ELEMENT_TABLE_GENERATOR, //
			ElementConstants.ELEMENT_ENTITY, //
			ElementConstants.ELEMENT_MAPPED_SUPERCLASS, //
			ElementConstants.ELEMENT_EMBEDDABLE, //
			ElementConstants.ELEMENT_NAMED_QUERY, //
			ElementConstants.ELEMENT_NAMED_NATIVE_QUERY);
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
		return this.entities;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<NamedNativeQueryMetadata> getNamedNativeQueries() {
		return this.namedNativeQueries;
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
	protected void handleChild(Element child) {
		if (child instanceof AccessElement) {
			this.accessType = ((AccessElement) child).getAccessType();
		}

		if (child instanceof PersistenceUnitMetadataElement) {
			final PersistenceUnitMetadataElement element = (PersistenceUnitMetadataElement) child;
			final PersistenceUnitDefaults persistenceUnitDefaults = element.getPersistenceUnitDefaults();
			if (persistenceUnitDefaults != null) {
				this.accessType = persistenceUnitDefaults.getAccessType();
				this.catalog = persistenceUnitDefaults.getCatalog();
				this.schema = persistenceUnitDefaults.getSchema();
				this.cascadePersist = persistenceUnitDefaults.isCascadePersist();
				this.entityListeners.addAll(persistenceUnitDefaults.getListeners());
			}

			this.xmlMappingMetadataComplete = element.isXmlMappingMetadataComplete();
		}

		if (child instanceof CatalogElement) {
			this.catalog = ((CatalogElement) child).getCatalog();
		}

		if (child instanceof SchemaElement) {
			this.schema = ((SchemaElement) child).getSchema();
		}

		if (child instanceof SequenceGeneratorElement) {
			this.sequenceGenerators.add((SequenceGeneratorElement) child);
		}

		if (child instanceof TableGeneratorElement) {
			this.tableGenerators.add((TableGeneratorElement) child);
		}

		if (child instanceof EmbeddableMetadata) {
			this.entities.add((EmbeddableMetadata) child);
		}

		if (child instanceof MappedSuperclassMetadata) {
			this.entities.add((MappedSuperclassMetadata) child);
		}

		if (child instanceof EntityListenersElement) {
			this.entityListeners.addAll(((EntityListenersElement) child).getListeners());
		}

		if (child instanceof EntityMetadata) {
			this.entities.add((EntityMetadata) child);
		}

		if (child instanceof NamedQueryMetadata) {
			this.namedQueries.add((NamedQueryMetadata) child);
		}

		if (child instanceof NamedNativeQueryMetadata) {
			this.namedNativeQueries.add((NamedNativeQueryMetadata) child);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isXmlMappingMetadataComplete() {
		return this.xmlMappingMetadataComplete;
	}
}

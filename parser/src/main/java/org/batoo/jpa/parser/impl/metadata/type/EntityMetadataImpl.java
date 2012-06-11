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
package org.batoo.jpa.parser.impl.metadata.type;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.impl.metadata.AssociationOverrideMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SecondaryTableMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SequenceGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableMetadataImpl;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation {@link EntityMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityMetadataImpl extends ManagedTypeMetadatImpl implements EntityMetadata {

	private final String name;
	private final Boolean cachable;

	private final TableMetadata table;
	private final SequenceGeneratorMetadata sequenceGenerator;
	private final TableGeneratorMetadata tableGenerator;
	private final List<SecondaryTableMetadata> secondaryTables = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();
	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityMetadataImpl(Class<?> clazz, EntityMetadata metadata) {
		super(clazz, metadata);

		final Set<Class<? extends Annotation>> parsed = Sets.newHashSet();

		// handle name
		this.name = this.handleName(metadata, parsed);

		// handle cacheable
		this.cachable = this.handleCacheable(metadata, parsed);

		// handle tables
		this.table = this.handleTable(metadata, parsed);
		this.handleSecondaryTables(metadata, parsed);

		// handle overrides
		this.handleAttributeOverrides(metadata, parsed);
		this.handleAssociationOverrides(metadata, parsed);

		// handle generators
		this.sequenceGenerator = this.handleSequenceGenerator(metadata, parsed);
		this.tableGenerator = this.handleTableGenerator(metadata, parsed);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AssociationMetadata> getAssociationOverrides() {
		return this.associationOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return this.attributeOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean getCacheable() {
		return this.cachable;
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
	public List<SecondaryTableMetadata> getSecondaryTables() {
		return this.secondaryTables;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SequenceGeneratorMetadata getSequenceGenerator() {
		return this.sequenceGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableMetadata getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableGeneratorMetadata getTableGenerator() {
		return this.tableGenerator;
	}

	/**
	 * Handles the association override definitions of the entity.
	 * <p>
	 * If metadata provides at least one association override definition then the its definitions are added to {@link #attributeOverrides}.
	 * <p>
	 * Else if either {@link AssociationOverrides} or {@link AssociationOverride} annotations present, then definition based on the
	 * annotation is added to {@link #associationOverrides}.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since $version
	 * @author
	 * @param parsed
	 */
	private void handleAssociationOverrides(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getAssociationOverrides().size() > 0)) {
			this.associationOverrides.addAll(metadata.getAssociationOverrides());
		}
		else {
			final AssociationOverrides overrides = this.getClazz().getAnnotation(AssociationOverrides.class);
			if ((overrides != null) && (overrides.value().length > 0)) {
				parsed.add(AttributeOverrides.class);

				for (final AssociationOverride override : overrides.value()) {
					this.associationOverrides.add(new AssociationOverrideMetadataImpl(this.getLocator(), override));
				}
			}
			else {
				final AssociationOverride override = this.getClazz().getAnnotation(AssociationOverride.class);
				parsed.add(AssociationOverride.class);

				if (override != null) {
					this.associationOverrides.add(new AssociationOverrideMetadataImpl(this.getLocator(), override));
				}
			}
		}
	}

	/**
	 * Handles the attribute override definitions of the entity.
	 * <p>
	 * If metadata provides at least one attribute override definition then the its definitions are added to {@link #attributeOverrides}.
	 * <p>
	 * Else if either {@link AttributeOverrides} or {@link AttributeOverride} annotations present, then definition based on the annotation
	 * is added to {@link #attributeOverrides}.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since $version
	 * @author
	 * @param parsed
	 */
	private void handleAttributeOverrides(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getAttributeOverrides().size() > 0)) {
			this.attributeOverrides.addAll(metadata.getAttributeOverrides());
		}
		else {
			final AttributeOverrides overrides = this.getClazz().getAnnotation(AttributeOverrides.class);
			if ((overrides != null) && (overrides.value().length > 0)) {
				parsed.add(AttributeOverrides.class);

				for (final AttributeOverride override : overrides.value()) {
					this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), override));
				}
			}
			else {
				final AttributeOverride override = this.getClazz().getAnnotation(AttributeOverride.class);
				parsed.add(AttributeOverride.class);

				if (override != null) {
					this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), override));
				}
			}
		}
	}

	/**
	 * Handles the cacheability of the entity.
	 * <p>
	 * If metadata specifies the cacheability definition, the definition is returned.
	 * <p>
	 * Then if the {@link Cacheable} annotation present, then returned true.
	 * <p>
	 * Finally entity class's simple name is returned
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotation parsed
	 * @return the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Boolean handleCacheable(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getCacheable() != null)) {
			return metadata.getCacheable();
		}

		final Cacheable cacheable = this.getClazz().getAnnotation(Cacheable.class);
		if (cacheable != null) {
			parsed.add(Cacheable.class);

			return cacheable.value();
		}

		return null;
	}

	/**
	 * Handles the name of the entity.
	 * <p>
	 * If metadata provides a name definition, the definition is returned.
	 * <p>
	 * Then if the {@link Entity} annotation present, then name on the annotation is returned.
	 * <p>
	 * Finally entity class's simple name is returned
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotation parsed
	 * @return the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String handleName(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && StringUtils.isNotBlank(metadata.getName())) {
			return metadata.getName();
		}

		final Entity entity = this.getClazz().getAnnotation(Entity.class);
		if ((entity != null) && StringUtils.isNotBlank(entity.name())) {
			parsed.add(Entity.class);

			return entity.name();
		}

		return this.getClazz().getSimpleName();
	}

	/**
	 * Handles the secondary table definitions of the entity.
	 * <p>
	 * If metadata provides at least one secondary table definition then the its definitions are added to {@link #secondaryTables}.
	 * <p>
	 * Else if either {@link SecondaryTables} or {@link SecondaryTable} annotations present, then definition based on the annotation is
	 * added to {@link #secondaryTables}.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since $version
	 * @author
	 */
	private void handleSecondaryTables(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getSecondaryTables().size() > 0)) {
			this.secondaryTables.addAll(metadata.getSecondaryTables());
		}
		else {
			final SecondaryTables secondaryTables = this.getClazz().getAnnotation(SecondaryTables.class);
			if ((secondaryTables != null) && (secondaryTables.value().length > 0)) {
				parsed.add(SecondaryTables.class);

				for (final SecondaryTable secondaryTable : secondaryTables.value()) {
					this.secondaryTables.add(new SecondaryTableMetadataImpl(this.getLocator(), secondaryTable));
				}
			}
			else {
				final SecondaryTable secondaryTable = this.getClazz().getAnnotation(SecondaryTable.class);
				if (secondaryTable != null) {
					parsed.add(SecondaryTable.class);

					this.secondaryTables.add(new SecondaryTableMetadataImpl(this.getLocator(), secondaryTable));
				}
			}
		}
	}

	/**
	 * Handles the sequence generator definition of the entity.
	 * <p>
	 * If metadata provides a sequence generator then the definition is returned.
	 * <p>
	 * Then if the {@link SequenceGenerator} annotation present, then definition based on the annotation is returned.
	 * <p>
	 * Finally null value is returned
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotation parsed
	 * @return the sequence generator metadata or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private SequenceGeneratorMetadata handleSequenceGenerator(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getSequenceGenerator() != null)) {
			return metadata.getSequenceGenerator();
		}

		final SequenceGenerator annotation = this.getClazz().getAnnotation(SequenceGenerator.class);
		if (annotation != null) {
			parsed.add(SequenceGenerator.class);

			return new SequenceGeneratorMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}

	/**
	 * Handles the table definition of the entity.
	 * <p>
	 * <p>
	 * If metadata provides a table definition then the definition is returned.
	 * <p>
	 * Then if the {@link Table} annotation present, then definition based on the annotation is returned.
	 * <p>
	 * Finally null value is returned
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotation parsed
	 * @return the table metadata or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private TableMetadata handleTable(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getTable() != null)) {
			return metadata.getTable();
		}

		final Table annotation = this.getClazz().getAnnotation(Table.class);
		if (annotation != null) {
			parsed.add(Table.class);

			return new TableMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}

	/**
	 * Handles the table generator definition of the entity.
	 * <p>
	 * If metadata provides a table generator then the definition is returned.
	 * <p>
	 * Then if the {@link TableGenerator} annotation present, then definition based on the annotation is returned.
	 * <p>
	 * Finally null value is returned
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotation parsed
	 * @return the table generator metadata or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private TableGeneratorMetadata handleTableGenerator(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getTableGenerator() != null)) {
			return metadata.getTableGenerator();
		}

		final TableGenerator annotation = this.getClazz().getAnnotation(TableGenerator.class);
		if (annotation != null) {
			parsed.add(TableGenerator.class);

			return new TableGeneratorMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}
}

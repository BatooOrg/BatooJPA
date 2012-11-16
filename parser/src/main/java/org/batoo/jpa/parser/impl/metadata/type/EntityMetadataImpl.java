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
package org.batoo.jpa.parser.impl.metadata.type;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.persistence.AccessType;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.annotations.Index;
import org.batoo.jpa.annotations.Indexes;
import org.batoo.jpa.parser.impl.metadata.AssociationOverrideMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.DiscriminatorColumnMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.IndexMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.NamedNativeQueryMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.NamedQueryMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SecondaryTableMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SequenceGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableMetadataImpl;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;
import org.batoo.jpa.parser.metadata.IndexMetadata;
import org.batoo.jpa.parser.metadata.NamedNativeQueryMetadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation {@link EntityMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityMetadataImpl extends IdentifiableMetadataImpl implements EntityMetadata {

	private final String name;
	private final Boolean cachable;

	private final TableMetadata table;
	private final SequenceGeneratorMetadata sequenceGenerator;
	private final TableGeneratorMetadata tableGenerator;
	private final List<SecondaryTableMetadata> secondaryTables = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();
	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();
	private final List<NamedQueryMetadata> namedQueries = Lists.newArrayList();
	private final List<NamedNativeQueryMetadata> namedNativeQueries = Lists.newArrayList();
	private final List<IndexMetadata> indexes = Lists.newArrayList();
	private InheritanceType inheritanceType;
	private DiscriminatorColumnMetadata discriminatorColumn;
	private String discriminatorValue;

	/**
	 * @param clazz
	 *            the represented class
	 * @param metadata
	 *            the metadata
	 * @param parentAccessType
	 *            the parent access type
	 * 
	 * @since 2.0.0
	 */
	public EntityMetadataImpl(Class<?> clazz, EntityMetadata metadata, AccessType parentAccessType) {
		super(clazz, metadata, parentAccessType);

		final Set<Class<? extends Annotation>> parsed = this.getAnnotationsParsed();

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

		// handle inheritance
		this.handleInheritance(metadata, parsed);

		// handle generators
		this.sequenceGenerator = this.handleSequenceGenerator(metadata, parsed);
		this.tableGenerator = this.handleTableGenerator(metadata, parsed);

		// handle named queries
		this.handleNamedQuery(metadata, parsed);
		this.handleNamedNativeQuery(metadata, parsed);
		this.handleIndexes(metadata, parsed);
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
	public DiscriminatorColumnMetadata getDiscriminatorColumn() {
		return this.discriminatorColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDiscriminatorValue() {
		return this.discriminatorValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<IndexMetadata> getIndexes() {
		return this.indexes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public InheritanceType getInheritanceType() {
		return this.inheritanceType;
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * Handles the index definitions of the entity.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	private void handleIndexes(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		final Indexes indexes = this.getClazz().getAnnotation(Indexes.class);
		if ((indexes != null) && (indexes.value().length > 0)) {
			parsed.add(Indexes.class);

			for (final Index index : indexes.value()) {
				this.indexes.add(new IndexMetadataImpl(this.getLocator(), index));
			}
		}
		else {
			final Index index = this.getClazz().getAnnotation(Index.class);
			parsed.add(Index.class);

			if (index != null) {
				this.indexes.add(new IndexMetadataImpl(this.getLocator(), index));
			}
		}
	}

	/**
	 * Handles the inheritance definition of the entity.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	private void handleInheritance(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if (metadata != null) {
			this.inheritanceType = metadata.getInheritanceType();
			this.discriminatorColumn = metadata.getDiscriminatorColumn();
			this.discriminatorValue = metadata.getDiscriminatorValue();
		}

		if (this.inheritanceType == null) {
			final Inheritance inheritance = this.getClazz().getAnnotation(Inheritance.class);
			if (inheritance != null) {
				this.inheritanceType = inheritance.strategy();

				parsed.add(Inheritance.class);
			}
		}

		if (this.discriminatorColumn == null) {
			final DiscriminatorColumn discriminatorColumn = this.getClazz().getAnnotation(DiscriminatorColumn.class);
			if (discriminatorColumn != null) {
				this.discriminatorColumn = new DiscriminatorColumnMetadataImpl(this.getLocator(), discriminatorColumn);

				parsed.add(DiscriminatorColumn.class);
			}
		}

		if (this.discriminatorValue == null) {
			final DiscriminatorValue discriminatorValue = this.getClazz().getAnnotation(DiscriminatorValue.class);
			if (discriminatorValue != null) {
				this.discriminatorValue = discriminatorValue.value();

				parsed.add(DiscriminatorValue.class);
			}
		}
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
	 * @since 2.0.0
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
	 * Handles the named native query definitions of the entity.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	private void handleNamedNativeQuery(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getNamedNativeQueries().size() > 0)) {
			this.namedNativeQueries.addAll(metadata.getNamedNativeQueries());
		}

		final NamedNativeQueries namedQueries = this.getClazz().getAnnotation(NamedNativeQueries.class);
		if ((namedQueries != null) && (namedQueries.value().length > 0)) {
			parsed.add(NamedNativeQueries.class);

			for (final NamedNativeQuery namedQuery : namedQueries.value()) {
				this.namedNativeQueries.add(new NamedNativeQueryMetadataImpl(this.getLocator(), namedQuery));
			}
		}
		else {
			final NamedNativeQuery namedQuery = this.getClazz().getAnnotation(NamedNativeQuery.class);
			parsed.add(NamedNativeQuery.class);

			if (namedQuery != null) {
				this.namedNativeQueries.add(new NamedNativeQueryMetadataImpl(this.getLocator(), namedQuery));
			}
		}
	}

	/**
	 * Handles the named query definitions of the entity.
	 * 
	 * @param metadata
	 *            the metadata
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	private void handleNamedQuery(EntityMetadata metadata, Set<Class<? extends Annotation>> parsed) {
		if ((metadata != null) && (metadata.getNamedQueries().size() > 0)) {
			this.namedQueries.addAll(metadata.getNamedQueries());
		}

		final NamedQueries namedQueries = this.getClazz().getAnnotation(NamedQueries.class);
		if ((namedQueries != null) && (namedQueries.value().length > 0)) {
			parsed.add(NamedQueries.class);

			for (final NamedQuery namedQuery : namedQueries.value()) {
				this.namedQueries.add(new NamedQueryMetadataImpl(this.getLocator(), namedQuery));
			}
		}
		else {
			final NamedQuery namedQuery = this.getClazz().getAnnotation(NamedQuery.class);
			parsed.add(NamedQuery.class);

			if (namedQuery != null) {
				this.namedQueries.add(new NamedQueryMetadataImpl(this.getLocator(), namedQuery));
			}
		}
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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

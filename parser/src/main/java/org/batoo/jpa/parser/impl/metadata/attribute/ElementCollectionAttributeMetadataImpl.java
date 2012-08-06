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
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.CollectionTableMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.ColumnMetadataImpl;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation element collection attributes.
 * 
 * @author hceylan
 * @since $version
 */
public class ElementCollectionAttributeMetadataImpl extends AttributeMetadataImpl implements ElementCollectionAttributeMetadata {

	private final CollectionTableMetadata collectionTable;
	private final FetchType fetchType;
	private final String targetClass;
	private final ColumnMetadata column;
	private final String mapKey;
	private final String mapKeyClassName;
	private final EnumType enumType;
	private final boolean lob;
	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();
	private final List<AttributeOverrideMetadata> mapKeyAttributeOverrides = Lists.newArrayList();
	private final EnumType mapKeyEnumType;
	private final String orderBy;
	private final TemporalType mapKeyTemporalType;
	private final ColumnMetadata orderColumn;
	private final TemporalType temporalType;
	private final ColumnMetadata mapKeyColumn;

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ElementCollectionAttributeMetadataImpl(Member member, ElementCollectionAttributeMetadata metadata) {
		super(member, metadata);

		this.collectionTable = metadata.getCollectionTable();
		this.targetClass = metadata.getTargetClass();
		this.fetchType = metadata.getFetchType();
		this.column = metadata.getColumn();
		this.lob = metadata.isLob();
		this.attributeOverrides.addAll(metadata.getAttributeOverrides());
		this.associationOverrides.addAll(metadata.getAssociationOverrides());
		this.enumType = metadata.getEnumType();
		this.temporalType = metadata.getTemporalType();
		this.mapKey = metadata.getMapKey();
		this.mapKeyAttributeOverrides.addAll(metadata.getMapKeyAttributeOverrides());
		this.mapKeyClassName = metadata.getMapKeyClassName();
		this.mapKeyColumn = metadata.getMapKeyColumn();
		this.mapKeyEnumType = metadata.getMapKeyEnumType();
		this.mapKeyTemporalType = metadata.getMapKeyTemporalType();
		this.orderBy = metadata.getOrderBy();
		this.orderColumn = metadata.getOrderColumn();
	}

	/**
	 * @param member
	 *            the member
	 * @param name
	 *            the name
	 * @param elementCollection
	 *            the element collection annotation
	 * @param parsed
	 *            the list of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ElementCollectionAttributeMetadataImpl(Member member, String name, ElementCollection elementCollection, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		this.targetClass = "void".equals(elementCollection.targetClass()) ? null : elementCollection.targetClass().getName();
		this.fetchType = elementCollection.fetch();

		this.collectionTable = this.handleCollectionTable(member, parsed);

		this.handleAttributeOverrides(member, parsed);

		this.column = this.handleColumn(member, parsed);
		this.lob = this.handleLob(member, parsed);
		this.enumType = this.handleEnumType(member, parsed);
		this.temporalType = this.handleTemporalType(member, parsed);
		this.mapKey = this.handleMapKey(member, parsed);
		this.mapKeyClassName = this.handleMapKeyClassName(member, parsed);
		this.mapKeyColumn = this.handleMapKeyColumn(member, parsed);
		this.mapKeyEnumType = this.handleMapKeyEnumType(member, parsed);
		this.mapKeyTemporalType = this.handleMapKeyTemporalType(member, parsed);
		this.orderColumn = this.handleOrderColumn(member, parsed);
		this.orderBy = this.handleOrderBy(member, parsed);
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
	public CollectionTableMetadata getCollectionTable() {
		return this.collectionTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchType getFetchType() {
		return this.fetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getMapKeyAttributeOverrides() {
		return this.mapKeyAttributeOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKeyClassName() {
		return this.mapKeyClassName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getMapKeyColumn() {
		return this.mapKeyColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getMapKeyEnumType() {
		return this.mapKeyEnumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TemporalType getMapKeyTemporalType() {
		return this.mapKeyTemporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getOrderBy() {
		return this.orderBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getOrderColumn() {
		return this.orderColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTargetClass() {
		return this.targetClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * Handles the attribute overrides.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void handleAttributeOverrides(Member member, Set<Class<? extends Annotation>> parsed) {
		final AttributeOverrides attributeOverrides = ReflectHelper.getAnnotation(member, AttributeOverrides.class);
		final AttributeOverride attributeOverride = ReflectHelper.getAnnotation(member, AttributeOverride.class);

		if ((attributeOverrides != null) && (attributeOverrides.value() != null) && (attributeOverrides.value().length > 0)) {
			parsed.add(AttributeOverrides.class);

			for (final AttributeOverride a : attributeOverrides.value()) {
				this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), a));
			}
		}
		else if (attributeOverride != null) {
			parsed.add(AttributeOverride.class);

			this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), attributeOverride));
		}
	}

	/**
	 * Handles the {@link CollectionTable} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the collection table metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private CollectionTableMetadata handleCollectionTable(Member member, Set<Class<? extends Annotation>> parsed) {
		final CollectionTable annotation = ReflectHelper.getAnnotation(member, CollectionTable.class);
		if (annotation != null) {
			parsed.add(CollectionTable.class);
			return new CollectionTableMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}

	/**
	 * Handles the {@link Column} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ColumnMetadata handleColumn(final Member member, Set<Class<? extends Annotation>> parsed) {
		final Column annotation = ReflectHelper.getAnnotation(member, Column.class);
		if (annotation != null) {
			parsed.add(Column.class);

			return new ColumnMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}

	/**
	 * Handles the {@link Enumerated} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private EnumType handleEnumType(Member member, Set<Class<? extends Annotation>> parsed) {
		final Enumerated annotation = ReflectHelper.getAnnotation(member, Enumerated.class);
		if (annotation != null) {
			parsed.add(Enumerated.class);

			return annotation.value();
		}

		return null;
	}

	/**
	 * Handles the {@link Lob} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean handleLob(Member member, Set<Class<? extends Annotation>> parsed) {
		final Lob annotation = ReflectHelper.getAnnotation(member, Lob.class);
		if (annotation != null) {
			parsed.add(Lob.class);

			return true;
		}

		return false;
	}

	/**
	 * Handles the {@link MapKey} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String handleMapKey(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKey annotation = ReflectHelper.getAnnotation(member, MapKey.class);
		if (annotation != null) {
			parsed.add(MapKey.class);

			return annotation.name();
		}

		return null;
	}

	/**
	 * Handles the {@link MapKeyClass} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String handleMapKeyClassName(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyClass annotation = ReflectHelper.getAnnotation(member, MapKeyClass.class);
		if (annotation != null) {
			parsed.add(MapKeyClass.class);

			return annotation.value().getName();
		}

		return null;
	}

	/**
	 * Handles the {@link MapKeyColumn} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ColumnMetadata handleMapKeyColumn(final Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyColumn annotation = ReflectHelper.getAnnotation(member, MapKeyColumn.class);
		if (annotation != null) {
			parsed.add(MapKeyColumn.class);

			return new ColumnMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}

	/**
	 * Handles the {@link MapKeyEnumerated} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private EnumType handleMapKeyEnumType(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyEnumerated annotation = ReflectHelper.getAnnotation(member, MapKeyEnumerated.class);
		if (annotation != null) {
			parsed.add(MapKeyEnumerated.class);

			return annotation.value();
		}

		return null;
	}

	/**
	 * Handles the {@link MapKeyTemporal} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private TemporalType handleMapKeyTemporalType(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyTemporal annotation = ReflectHelper.getAnnotation(member, MapKeyTemporal.class);
		if (annotation != null) {
			parsed.add(MapKeyTemporal.class);

			return annotation.value();
		}

		return null;
	}

	/**
	 * Handles the {@link OrderBy} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String handleOrderBy(Member member, Set<Class<? extends Annotation>> parsed) {
		final OrderBy annotation = ReflectHelper.getAnnotation(member, OrderBy.class);
		if (annotation != null) {
			parsed.add(OrderBy.class);

			return annotation.value();
		}

		return null;
	}

	/**
	 * Handles the {@link OrderColumn} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ColumnMetadata handleOrderColumn(final Member member, Set<Class<? extends Annotation>> parsed) {
		final OrderColumn annotation = ReflectHelper.getAnnotation(member, OrderColumn.class);
		if (annotation != null) {
			parsed.add(OrderColumn.class);

			return new ColumnMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}

	/**
	 * Handles the {@link Temporal} annotation.
	 * 
	 * @param member
	 *            the member
	 * @param parsed
	 *            the list of annotations parsed
	 * @return the map key value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private TemporalType handleTemporalType(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyTemporal annotation = ReflectHelper.getAnnotation(member, MapKeyTemporal.class);
		if (annotation != null) {
			parsed.add(MapKeyTemporal.class);

			return annotation.value();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLob() {
		return this.lob;
	}
}

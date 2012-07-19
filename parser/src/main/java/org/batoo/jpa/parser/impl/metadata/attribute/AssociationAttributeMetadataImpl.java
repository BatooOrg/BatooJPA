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
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.MapKeyTemporal;
import javax.persistence.MapsId;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.TemporalType;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.ColumnMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.JoinColumnMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.JoinTableMetadaImpl;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * The implementation for {@link AssociationAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AssociationAttributeMetadataImpl extends AttributeMetadataImpl implements AssociationAttributeMetadata {

	private String targetEntity;
	private final Set<CascadeType> cascades;
	private final FetchType fetchType;
	private final JoinTableMetadata joinTable;
	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member of association attribute
	 * @param metadata
	 *            the metadata definition of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationAttributeMetadataImpl(Member member, AssociationAttributeMetadata metadata) {
		super(member, metadata);

		this.cascades = metadata.getCascades();
		this.fetchType = metadata.getFetchType();
		this.joinTable = metadata.getJoinTable();
		this.joinColumns.addAll(Lists.newArrayList(metadata.getJoinColumns()));
	}

	/**
	 * @param member
	 *            the java member of association attribute
	 * @param name
	 *            the name of the association attribute
	 * @param parsed
	 *            the set of annotations parsed
	 * @param targetEntity
	 *            the class of the target entity of the association attribute
	 * @param fetchType
	 *            the fetch type of the of the association attribute
	 * @param cascades
	 *            the cascades of the of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed, String targetEntity, FetchType fetchType,
		CascadeType[] cascades) {
		super(member, name);

		this.targetEntity = "void".equals(targetEntity) ? null : targetEntity;
		this.fetchType = fetchType;
		this.cascades = cascades != null ? Sets.newHashSet(cascades) : Sets.<CascadeType> newHashSet();

		final JoinColumns joinColumns = ReflectHelper.getAnnotation(member, JoinColumns.class);
		final JoinColumn joinColumn = ReflectHelper.getAnnotation(member, JoinColumn.class);
		final JoinTable joinTable = ReflectHelper.getAnnotation(member, JoinTable.class);

		if ((joinColumns != null) && (joinColumns.value().length > 0)) {
			parsed.add(JoinColumns.class);

			for (final JoinColumn column : joinColumns.value()) {
				this.joinColumns.add(new JoinColumnMetadataImpl(this.getLocator(), column));
			}

			this.joinTable = null;
		}
		else if (joinColumn != null) {
			parsed.add(JoinColumn.class);

			this.joinColumns.add(new JoinColumnMetadataImpl(this.getLocator(), joinColumn));

			this.joinTable = null;
		}
		else if (joinTable != null) {
			parsed.add(JoinTable.class);

			this.joinTable = new JoinTableMetadaImpl(this.getLocator(), joinTable);
		}
		else {
			this.joinTable = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<CascadeType> getCascades() {
		return this.cascades;
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
	public List<JoinColumnMetadata> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTableMetadata getJoinTable() {
		return this.joinTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTargetEntity() {
		return this.targetEntity;
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
	protected String handleMapKey(Member member, Set<Class<? extends Annotation>> parsed) {
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
	protected String handleMapKeyClassName(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyClass annotation = ReflectHelper.getAnnotation(member, MapKeyClass.class);
		if (annotation != null) {
			parsed.add(MapKeyClass.class);

			return annotation.value().getName();
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
	protected ColumnMetadata handleMapKeyColumn(Member member, Set<Class<? extends Annotation>> parsed) {
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
	protected EnumType handleMapKeyEnumType(Member member, Set<Class<? extends Annotation>> parsed) {
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
	protected TemporalType handleMapKeyTemporalType(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapKeyTemporal annotation = ReflectHelper.getAnnotation(member, MapKeyTemporal.class);
		if (annotation != null) {
			parsed.add(MapKeyTemporal.class);

			return annotation.value();
		}

		return null;
	}

	/**
	 * Handles the {@link MapsId} annotation.
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
	protected String handleMapsId(Member member, Set<Class<? extends Annotation>> parsed) {
		final MapsId annotation = ReflectHelper.getAnnotation(member, MapsId.class);
		if (annotation != null) {
			parsed.add(MapsId.class);

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
	protected String handleOrderBy(Member member, Set<Class<? extends Annotation>> parsed) {
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
	protected ColumnMetadata handleOrderColumn(final Member member, Set<Class<? extends Annotation>> parsed) {
		final OrderColumn annotation = ReflectHelper.getAnnotation(member, OrderColumn.class);
		if (annotation != null) {
			parsed.add(OrderColumn.class);

			return new ColumnMetadataImpl(this.getLocator(), annotation);
		}

		return null;
	}
}

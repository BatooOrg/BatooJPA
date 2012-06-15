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
package org.batoo.jpa.core.impl.model.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributeMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link PluralAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <C>
 *            The type of the represented collection
 * @param <E>
 *            The element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public abstract class PluralAttributeImpl<X, C, E> extends AttributeImpl<X, C> implements PluralAttribute<X, C, E> {

	/**
	 * Creates an associated plural attribute corresponding to member type
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            the attribute type
	 * @param <X>
	 *            the type of the managed type
	 * @return the attribute created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <X> PluralAttributeImpl<X, ?, ?> create(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		final Member member = ((AttributeMetadataImpl) metadata).getMember();

		Class<?> type;
		if (member instanceof Field) {
			type = ((Field) member).getType();
		}
		else {
			type = ((Method) member).getReturnType();
		}

		if (List.class == type) {
			return new ListAttributeImpl(declaringType, metadata, attributeType);
		}
		else if (Set.class == type) {
			return new SetAttributeImpl(declaringType, metadata, attributeType);
		}
		else if (Collection.class == type) {
			return new CollectionAttributeImpl(declaringType, metadata, attributeType);
		}
		else if (Map.class == type) {
			return null;
		}

		throw new MappingException("Cannot determine collection type for " + type, metadata.getLocator());
	}

	private final Class<E> bindableJavaType;
	private final javax.persistence.metamodel.Attribute.PersistentAttributeType attributeType;
	private final boolean association;
	private Type<E> type;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            the attribute type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public PluralAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		super(declaringType, metadata);

		this.attributeType = attributeType;

		if ((metadata instanceof AssociationAttributeMetadata) && StringUtils.isNotBlank(((AssociationAttributeMetadata) metadata).getTargetEntity())) {
			try {
				this.bindableJavaType = (Class<E>) Class.forName(((AssociationAttributeMetadata) metadata).getTargetEntity());
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Target enttity class not found", metadata.getLocator());
			}
		}
		else {
			this.bindableJavaType = ReflectHelper.getGenericType(this.getJavaMember(), 0);
		}

		this.association = attributeType != PersistentAttributeType.ELEMENT_COLLECTION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<E> getBindableJavaType() {
		return this.bindableJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final BindableType getBindableType() {
		return BindableType.PLURAL_ATTRIBUTE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Type<E> getElementType() {
		if (this.type != null) {
			return this.type;
		}

		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();

		switch (this.attributeType) {
			case ONE_TO_MANY:
			case MANY_TO_MANY:
				this.type = metamodel.entity(this.bindableJavaType);
				break;
			case ELEMENT_COLLECTION:
				this.type = metamodel.embeddable(this.bindableJavaType) != null ? metamodel.embeddable(this.bindableJavaType)
					: metamodel.createBasicType(this.bindableJavaType);
		}

		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final PersistentAttributeType getPersistentAttributeType() {
		return this.attributeType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isAssociation() {
		return this.association;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isCollection() {
		return true;
	}

	/**
	 * Creates a new lazy initialized managed collection to track changes.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param session
	 *            the session
	 * @param id
	 *            the id of the root entity
	 * @return the lazy initialized managed collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract C newCollection(PluralAssociationMapping<?, E, C> mapping, SessionImpl session, Object id);

	/**
	 * Creates a new managed collection to track changes.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param session
	 *            the session
	 * @param id
	 *            the id of the root entity
	 * @param values
	 *            the values
	 * @return the managed collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract C newCollection(PluralAssociationMapping<?, E, C> mapping, SessionImpl session, Object id, Collection<? extends E> values);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (this.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION) {
			builder.append("element-collection");
		}
		else {
			builder.append("association ");

			switch (this.getCollectionType()) {
				case COLLECTION:
					builder.append("collection");
					break;
				case LIST:
					builder.append("list");
					break;
				case MAP:
					builder.append("map");
					break;
				case SET:
					builder.append("set");
					break;
			}
		}

		final String declaringType = this.getDeclaringType().getJavaType().getSimpleName();

		final String type = this.getBindableJavaType().getSimpleName();
		builder.append(" ").append(declaringType).append(".").append(this.getName()).append("(").append(type).append(")");

		return builder.toString();
	}
}

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
package org.batoo.jpa.core.impl.model.attribute;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.PluralAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
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
 * @since 2.0.0
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
	 * @since 2.0.0
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
			return new MapAttributeImpl(declaringType, metadata, attributeType);
		}

		throw new MappingException("Cannot determine collection type for " + type, metadata.getLocator());
	}

	private final Class<E> bindableJavaType;
	private final javax.persistence.metamodel.Attribute.PersistentAttributeType attributeType;
	private final boolean association;
	private TypeImpl<E> type;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            the attribute type
	 * @param valueIndexNo
	 *            the index of the generic value parameter, typically 0 for {@link Collection}, {@link List} and {@link Set} attributes and
	 *            1 for {@link Map} attributes
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public PluralAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType, int valueIndexNo) {
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
			this.bindableJavaType = ReflectHelper.getGenericType(this.getJavaMember(), valueIndexNo);
		}

		this.association = attributeType != PersistentAttributeType.ELEMENT_COLLECTION;
	}

	/**
	 * Returns the attributeType of the PluralAttributeImpl.
	 * 
	 * @return the attributeType of the PluralAttributeImpl
	 * 
	 * @since 2.0.0
	 */
	public javax.persistence.metamodel.Attribute.PersistentAttributeType getAttributeType() {
		return this.attributeType;
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
	public TypeImpl<E> getElementType() {
		if (this.type != null) {
			return this.type;
		}

		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();

		switch (this.attributeType) {
			case ONE_TO_MANY:
			case MANY_TO_MANY:
				return this.type = metamodel.entity(this.bindableJavaType);
			default: // ELEMENT_COLLECTION:
				return this.type = metamodel.embeddable(this.bindableJavaType) != null ? metamodel.embeddable(this.bindableJavaType)
					: metamodel.createBasicType(this.bindableJavaType);
		}
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
	 * @param managedInstance
	 *            the managed instance
	 * @param lazy
	 *            if the collection is lazy
	 * @return the lazy initialized managed collection
	 * 
	 * @since 2.0.0
	 */
	public abstract C newCollection(PluralMapping<?, C, E> mapping, ManagedInstance<?> managedInstance, boolean lazy);

	/**
	 * Creates a new managed collection to track changes.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * @param values
	 *            the values
	 * @return the managed collection
	 * 
	 * @since 2.0.0
	 */
	public abstract C newCollection(PluralMapping<?, C, E> mapping, ManagedInstance<?> managedInstance, Object values);

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

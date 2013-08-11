/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

import java.lang.reflect.Member;

import javax.persistence.metamodel.Attribute;

import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributeMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link Attribute}.
 * 
 * @param <X>
 *            The represented type that contains the attribute
 * @param <Y>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AttributeImpl<X, Y> implements Attribute<X, Y> {

	private static int nextAttributeId;

	private static int nextAttributeId() {
		return AttributeImpl.nextAttributeId++;
	}

	private final int attributeId;
	private final AttributeMetadata metadata;
	private final ManagedTypeImpl<X> declaringType;
	private final String name;
	private final Member javaMember;
	private final Class<Y> javaType;
	private final MetamodelImpl metamodel;

	private final AbstractAccessor accessor;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public AttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata) {
		super();

		this.attributeId = AttributeImpl.nextAttributeId();
		this.metadata = metadata;

		this.declaringType = declaringType;
		this.name = metadata.getName();
		this.javaMember = ((AttributeMetadataImpl) metadata).getMember();
		this.javaType = ReflectHelper.getActualType(declaringType.getJavaType(), this.name, ReflectHelper.getMemberType(this.javaMember));
		this.metamodel = declaringType.getMetamodel();
		this.accessor = ReflectHelper.getAccessor(this.javaMember);
	}

	/**
	 * Clones the attribute.
	 * <p>
	 * This is used for cloning th attributes from Mapped Super Classes onto child types.
	 * 
	 * @param type
	 *            the owner type
	 * @return the new attribute
	 * @param <Z>
	 *            the new type
	 * 
	 * @since 2.0.1
	 */
	public abstract <Z extends X> AttributeImpl<Z, Y> clone(EntityTypeImpl<Z> type);

	/**
	 * Returns the attribute value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the attribute value of instance
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public Y get(Object instance) {
		if (instance == null) {
			return null;
		}

		return (Y) this.accessor.get(instance);
	}

	/**
	 * Returns the ordinal id of the attribute.
	 * 
	 * @return the ordinal id of the attribute
	 * 
	 * @since 2.0.0
	 */
	public Integer getAttributeId() {
		return this.attributeId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ManagedTypeImpl<X> getDeclaringType() {
		return this.declaringType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Member getJavaMember() {
		return this.javaMember;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<Y> getJavaType() {
		return this.javaType;
	}

	/**
	 * Returns the locator of the AttributeImpl.
	 * 
	 * @return the locator of the AttributeImpl
	 * 
	 * @since 2.0.0
	 */
	public AbstractLocator getLocator() {
		return this.metadata.getLocator();
	}

	/**
	 * Returns the metadata of the attribute.
	 * 
	 * @return the metadata of the attribute
	 * 
	 * @since 2.0.0
	 */
	public AttributeMetadata getMetadata() {
		return this.metadata;
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * 
	 * @since 2.0.0
	 */
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
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
	 * Sets the attribute value of instance.
	 * 
	 * @param instance
	 *            the instance
	 * @param value
	 *            the value to set
	 * 
	 * @since 2.0.0
	 */
	public void set(Object instance, Object value) {
		this.accessor.set(instance, value);
	}
}

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

import java.util.List;

import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;

import sun.reflect.ConstructorAccessor;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link SingularAttribute} for embeddable attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("restriction")
public class EmbeddedAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T> {
	private static final Object[] EMPTY_PARAMS = new Object[] {};

	private final boolean id;
	private final List<AssociationMetadata> associationOverrides;
	private final List<AttributeOverrideMetadata> attributeOverrides;
	private EmbeddableTypeImpl<T> type;
	private final ConstructorAccessor constructor;

	/**
	 * Constructor for id type embedded attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttribute(IdentifiableTypeImpl<X> declaringType, EmbeddedIdAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.id = true;

		this.associationOverrides = Lists.newArrayList();
		this.attributeOverrides = metadata.getAttributeOverrides();

		try {
			this.constructor = ReflectHelper.createConstructor(this.getBindableJavaType().getConstructor());
		}
		catch (final Exception e) {
			throw new MappingException("Embeddable type does not have a default constructor", this.getLocator());
		}
	}

	/**
	 * Constructor for regular embedded attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttribute(ManagedTypeImpl<X> declaringType, EmbeddedAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.id = false;

		this.associationOverrides = metadata.getAssociationOverrides();
		this.attributeOverrides = metadata.getAttributeOverrides();

		try {
			this.constructor = ReflectHelper.createConstructor(this.getBindableJavaType().getConstructor());
		}
		catch (final Exception e) {
			throw new MappingException("Embeddable type does not have a default constructor", this.getLocator());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return PersistentAttributeType.EMBEDDED;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddableTypeImpl<T> getType() {
		if (this.type != null) {
			return this.type;
		}

		return this.type = this.getMetamodel().embeddable(this.getBindableJavaType());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}

	/**
	 * Returns a new embeddable instance
	 * 
	 * @return the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked" })
	public T newInstance() {
		try {
			return (T) this.constructor.newInstance(EmbeddedAttribute.EMPTY_PARAMS);
		}
		catch (final Exception e) {}

		return null;
	}
}

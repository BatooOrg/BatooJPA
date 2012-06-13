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

import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link SingularMapping} for embeddable attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T> {

	private final boolean id;
	private final List<AssociationMetadata> associationOverrides;
	private final List<AttributeOverrideMetadata> attributeOverrides;
	private EmbeddableTypeImpl<T> type;

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
	public T newInstance() {
		return this.getType().newInstance();
	}
}

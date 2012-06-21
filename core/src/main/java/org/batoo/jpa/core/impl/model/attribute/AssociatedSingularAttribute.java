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

import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OptionalAssociationAttributeMetadata;

/**
 * Implementation of {@link SingularMapping} representing types of ManyToOne and OneToOne
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * @author hceylan
 * @since $version
 */
public class AssociatedSingularAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T> {

	private final PersistentAttributeType attributeType;
	private final boolean optional;

	private EntityTypeImpl<T> type;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            the type of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociatedSingularAttribute(ManagedTypeImpl<X> declaringType, PersistentAttributeType attributeType, AssociationAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.attributeType = attributeType;
		this.optional = ((OptionalAssociationAttributeMetadata) metadata).isOptional();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociationAttributeMetadata getMetadata() {
		return (AssociationAttributeMetadata) super.getMetadata();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return this.attributeType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("association").append(super.toString());

		if (this.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE) {
			if (this.isOptional()) {
				builder.append(" <0..*>");
			}
			else {
				builder.append(" <1..*>");
			}
		}
		else {
			if (this.isOptional()) {
				builder.append(" <0..1>");
			}
			else {
				builder.append(" <1..1>");
			}
		}

		return builder.toString();
	}
}

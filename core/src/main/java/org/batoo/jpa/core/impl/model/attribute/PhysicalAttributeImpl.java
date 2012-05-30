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

import javax.persistence.TemporalType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.model.BasicTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.PhysicalAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for basic, version and id attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public abstract class PhysicalAttributeImpl<X, T> extends SingularAttributeImpl<X, T> {

	private final TemporalType temporalType;
	private final BasicTypeImpl<T> type;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalAttributeImpl(ManagedTypeImpl<X> declaringType, PhysicalAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.type = this.getDeclaringType().getMetamodel().createBasicType(this.getJavaType());
		this.temporalType = metadata.getTemporalType();
	}

	/**
	 * Returns the physical column of the attribute.
	 * 
	 * @return the physical column of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract PhysicalColumn getColumn();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return PersistentAttributeType.BASIC;
	}

	/**
	 * Returns the temporalType.
	 * 
	 * @return the temporalType
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicTypeImpl<T> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return false;
	}
}

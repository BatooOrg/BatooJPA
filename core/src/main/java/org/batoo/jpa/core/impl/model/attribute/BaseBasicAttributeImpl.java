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

import javax.persistence.FetchType;
import javax.persistence.TemporalType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.PhysicalAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for basic and id attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public abstract class BaseBasicAttributeImpl<X, T> extends SingularAttributeImpl<X, T> {

	private final FetchType fetchType;
	private final TemporalType temporalType;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseBasicAttributeImpl(ManagedTypeImpl<X> declaringType, PhysicalAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.fetchType = metadata.getFetchType();
		this.temporalType = metadata.getTemporalType();
	}

	/**
	 * Returns the fetchType.
	 * 
	 * @return the fetchType
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FetchType getFetchType() {
		return this.fetchType;
	}

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
	public boolean isAssociation() {
		return false;
	}
}

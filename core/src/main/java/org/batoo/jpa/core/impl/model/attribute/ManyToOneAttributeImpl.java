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

import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for many-to-one attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToOneAttributeImpl<X, T> extends AssociatedSingularAttribute<X, T> {

	private final boolean optional;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToOneAttributeImpl(ManagedTypeImpl<X> declaringType, ManyToOneAttributeMetadata metadata) {
		super(declaringType, metadata, null);

		this.optional = metadata.isOptional();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return PersistentAttributeType.MANY_TO_ONE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}

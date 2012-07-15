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

import java.util.Collection;
import java.util.Set;

import javax.persistence.metamodel.SetAttribute;

import org.batoo.jpa.core.impl.collections.ManagedSet;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link SetAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public class SetAttributeImpl<X, E> extends PluralAttributeImpl<X, Set<E>, E> implements SetAttribute<X, E> {

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            attribute type
	 * @since $version
	 * @author hceylan
	 */
	public SetAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		super(declaringType, metadata, attributeType, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.SET;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<E> newCollection(PluralAssociationMapping<?, Set<E>, E> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		return new ManagedSet<X, E>(mapping, managedInstance, lazy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<E> newCollection(PluralAssociationMapping<?, Set<E>, E> mapping, ManagedInstance<?> managedInstance, Object values) {
		return new ManagedSet<X, E>(mapping, managedInstance, (Collection<? extends E>) values);
	}
}

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

import javax.persistence.metamodel.CollectionAttribute;

import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link CollectionAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public class CollectionAttributeImpl<X, E> extends PluralAttributeImpl<X, Collection<E>, E> implements CollectionAttribute<X, E> {

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
	public CollectionAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		super(declaringType, metadata, attributeType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.COLLECTION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<E> newCollection(PluralAssociationMapping<?, E, Collection<E>> mapping, ManagedInstance<?> managedInstance) {
		return new ManagedList<X, E>(mapping, managedInstance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<E>
		newCollection(PluralAssociationMapping<?, E, Collection<E>> mapping, ManagedInstance<?> managedInstance, Collection<? extends E> values) {
		return new ManagedList<X, E>(mapping, managedInstance, values);
	}
}

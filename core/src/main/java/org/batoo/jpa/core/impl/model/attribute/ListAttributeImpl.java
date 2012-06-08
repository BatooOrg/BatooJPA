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

import java.sql.SQLException;
import java.util.List;

import javax.persistence.metamodel.ListAttribute;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

/**
 * Implementation of {@link ListAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <C>
 *            The type of the represented collection
 * @param <E>
 *            The element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public class ListAttributeImpl<X, C, E> extends AssociatedPluralAttribute<X, List<E>, E> implements ListAttribute<X, E> {

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            attribute type
	 * @param mappedBy
	 *            the mapped by attribute
	 * @param removesOrphans
	 *            if attribute removes orphans
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ListAttributeImpl(ManagedTypeImpl<X> declaringType, AssociationAttributeMetadata metadata,
		PersistentAttributeType attributeType, String mappedBy, boolean removesOrphans) {
		super(declaringType, attributeType, metadata, mappedBy, removesOrphans);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void checkTransient(ManagedInstance<? extends X> managedInstance) {
		final List<E> entities = this.get(managedInstance.getInstance());

		final SessionImpl session = managedInstance.getSession();

		if (entities != null) {
			for (final E entity : entities) {
				session.checkTransient(entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(SessionImpl session, ConnectionImpl connection, ManagedInstance<? extends X> managedInstance) throws SQLException {
		final List<E> entities = this.get(managedInstance.getInstance());

		if (entities != null) {
			for (final E entity : entities) {
				this.getJoinTable().performInsert(session, connection, managedInstance.getInstance(), entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.LIST;
	}
}

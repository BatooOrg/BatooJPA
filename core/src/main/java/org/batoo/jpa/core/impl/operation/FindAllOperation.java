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
package org.batoo.jpa.core.impl.operation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

/**
 * Operation to find an entity.
 * 
 * @author hceylan
 * @since $version
 */
public class FindAllOperation<X, C, E> extends AbstractOperation<X> {

	private final ManagedId<X> managedId;
	private final CollectionMapping<X, C, E> association;
	private Collection<E> collection;

	/**
	 * @param entityManagerImpl
	 * @param managedId
	 * @param association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FindAllOperation(EntityManagerImpl entityManager, ManagedId<X> managedId, CollectionMapping<X, C, E> association) {
		super(entityManager, managedId.getInstance());

		this.managedId = managedId;
		this.association = association;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected List<AbstractOperation<?>> cascade(Association<?, ?> association) {
		return null; // NOOP
	}

	/**
	 * Returns the collection.
	 * 
	 * @return the collection
	 * @since $version
	 */
	public Collection<E> getCollection() {
		return this.collection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void perform(Connection connection) throws SQLException {
		// NOOP
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean prepare(SessionImpl session) {
		try {
			this.collection = this.association.performSelect(session, this.managedId);
		}
		catch (final SQLException e) {
			throw new PersistenceException("Collection cannot be loaded", e);
		}

		return false; // NOOP
	}

}

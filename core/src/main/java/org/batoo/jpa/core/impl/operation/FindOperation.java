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
import java.util.List;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;

/**
 * Operation to find an entity.
 * 
 * @author hceylan
 * @since $version
 */
public class FindOperation<X> extends AbstractOperation<X> {

	private final ManagedId<X> managedId;

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param managedId
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 * @param initialize
	 */
	public FindOperation(EntityManagerImpl entityManager, ManagedId<X> managedId) {
		super(entityManager, managedId.getInstance());

		this.managedId = managedId;
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
	 * Returns the instance.
	 * 
	 * @return the instance
	 * @since $version
	 */
	public X getInstance() {
		return this.instance;
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
	@SuppressWarnings("unchecked")
	protected boolean prepare(SessionImpl session) {
		this.managedInstance = this.em.getSession().get(this.instance);

		if ((this.managedInstance == null) || (!this.managedInstance.isLoaded())) {
			try {
				final ManagedInstance<? super X> oldInstance = this.managedInstance;
				this.instance = this.type.performSelect(session, this.managedId);
				if (oldInstance != null) {
					oldInstance.clearReferences(this.instance);
				}
			}
			catch (final SQLException e) {
				throw new PersistenceException("Entity cannot be loaded", e);
			}
		}
		else {
			this.instance = (X) this.managedInstance.getInstance();
		}

		return false; // NOOP
	}
}

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
package org.batoo.jpa.core.impl;

import java.sql.Connection;

import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class SessionImpl {

	private Repository repository;
	private final EntityManagerImpl em;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public SessionImpl(EntityManagerImpl entityManager) {
		super();

		this.em = entityManager;
		this.clear();
	}

	/**
	 * Clears the session.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void clear() {
		this.repository = new Repository();
	}

	/**
	 * Returns the managed instance in the session.
	 * 
	 * @param instance
	 *            the instance.
	 * @return managed instance or a new unmanaged instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> ManagedInstance<X> get(X instance) {
		final ManagedInstance<X> newManagedInstance = ManagedInstance.create(this, instance);

		final ManagedId<X> managedId = newManagedInstance.getId();
		final EntityTypeImpl<? super X> entityType = newManagedInstance.getType();
		final ManagedInstance<X> oldManagedInstance = this.repository.get(entityType).get(managedId);

		return oldManagedInstance != null ? oldManagedInstance : newManagedInstance;
	}

	/**
	 * Returns the active connection.
	 * 
	 * @return the connection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Connection getConnection() {
		return this.em.getConnection();
	}

	/**
	 * Returns the entity manager this session belongs to.
	 * 
	 * @return the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerImpl getEntityManager() {
		return this.em;
	}

	/**
	 * Puts the instance into session, marking the instance as managed.
	 * 
	 * @param type
	 *            the type of the entity
	 * @param managedInstance
	 *            the managed instance
	 * @return the original managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> ManagedInstance<X> put(ManagedInstance<X> managedInstance) {
		return this.repository.get(managedInstance.getType()).put(managedInstance);
	}
}

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

	private static volatile int nextSessionId = 0;

	private Repository repository;
	private final EntityManagerImpl em;

	private final int sessionId;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public SessionImpl(EntityManagerImpl entityManager) {
		super();

		this.em = entityManager;
		this.clear();
		this.sessionId = ++nextSessionId;
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

	public <X> ManagedInstance<X> get(final EntityTypeImpl<X> type, X entity) {
		final ManagedId<X> id = type.getManagedIdForInstance(this, entity);
		return this.get(id);
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
	@SuppressWarnings("unchecked")
	public <X> ManagedInstance<X> get(ManagedId<X> id) {
		final EntityTypeImpl<X> type = id.getType();
		final SubRepository<X> subRepository = this.repository.get(type);
		return (ManagedInstance<X>) subRepository.get(id);
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
	@SuppressWarnings("unchecked")
	public <X> ManagedInstance<X> get(X entity) {
		final EntityTypeImpl<X> type = this.em.getMetamodel().entity((Class<X>) entity.getClass());
		return this.get(type, entity);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		return this.sessionId;
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
	 * @return
	 */
	public <X> void put(ManagedInstance<X> managedInstance) {
		this.repository.get(managedInstance.getType().getTopType()).put(managedInstance);
	}
}

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
package org.batoo.jpa.core.impl.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.instance.Prioritizer;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class SessionImpl {

	private static volatile int nextSessionId = 0;

	private final EntityManagerImpl em;
	private final MetamodelImpl metamodel;
	private final int sessionId;
	private Repository repository;

	private final HashSet<ManagedInstance<?>> externalEntities = Sets.newHashSet();
	private final LinkedList<ManagedInstance<?>> identifiableEntities = Lists.newLinkedList();
	private final LinkedList<ManagedInstance<?>> changedEntities = Lists.newLinkedList();

	/**
	 * @param entityManager
	 *            the owner entity manager
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SessionImpl(EntityManagerImpl entityManager, MetamodelImpl metamodel) {
		super();

		this.em = entityManager;
		this.metamodel = metamodel;
		this.sessionId = ++SessionImpl.nextSessionId;

		this.clear();
	}

	/**
	 * Checks if the instance is managed in this session
	 * 
	 * @param instance
	 *            the instance to check
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void checkTransient(Object instance) {
		if (instance instanceof EnhancedInstance) {
			final ManagedInstance<?> associate = ((EnhancedInstance) instance).__enhanced__$$__getManagedInstance();
			if ((associate.getStatus() != Status.MANAGED) && (associate.getSession() == this)) {
				throw new PersistenceException("Instance " + instance + " is not managed");
			}
		}
		else {
			final ManagedInstance<?> associate = this.get(instance);
			if ((associate == null) || (associate.getStatus() != Status.MANAGED)) {
				throw new PersistenceException("Instance " + instance + " is not managed");
			}
		}
	}

	/**
	 * Clears the session.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void clear() {
		// XXX detach all the entities if there is an existing repository
		this.repository = new Repository();
		this.externalEntities.clear();
	}

	/**
	 * Flushes the session persisting changes to the database.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the flush will take place
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flush(ConnectionImpl connection, EntityTransactionImpl transaction) throws SQLException {
		final int totalSize = this.externalEntities.size() + this.externalEntities.size() + this.changedEntities.size();
		final ArrayList<ManagedInstance<?>> instances = Lists.newArrayListWithCapacity(totalSize);

		instances.addAll(this.externalEntities);
		instances.addAll(this.identifiableEntities);
		instances.addAll(this.changedEntities);

		final ManagedInstance<?>[] sortedInstances = Prioritizer.sort(instances);

		for (final ManagedInstance<?> instance : sortedInstances) {
			instance.flush(connection, transaction);
		}

		for (final ManagedInstance<?> instance : sortedInstances) {
			instance.checkTransients();
			instance.flushAssociations(connection);
		}

		this.externalEntities.addAll(this.identifiableEntities);
		this.identifiableEntities.clear();
	}

	/**
	 * Returns the managed instance in the session.
	 * 
	 * @param instance
	 *            the instance.
	 * @param <X>
	 *            the type of the instance
	 * @return managed instance or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> ManagedInstance<X> get(ManagedInstance<X> instance) {
		final EntityTypeImpl<? super X> type = instance.getType();
		final SubRepository<? super X> subRepository = this.repository.get(type);

		return (ManagedInstance<X>) subRepository.get(instance);
	}

	/**
	 * Returns the managed instance instance in the session
	 * 
	 * @param entity
	 *            the entity
	 * @param <X>
	 *            the type of the instance
	 * @return the managed instance or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> ManagedInstance<X> get(X entity) {
		final EntityTypeImpl<?> type = this.metamodel.entity(entity.getClass());
		final ManagedInstance<X> managedInstance = (ManagedInstance<X>) type.getManagedInstance(this, entity);

		return this.get(managedInstance);
	}

	/**
	 * Returns the entity manager.
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
	 * Returns the sessionId.
	 * 
	 * @return the sessionId
	 * @since $version
	 */
	public int getSessionId() {
		return this.sessionId;
	}

	/**
	 * Puts the instance into the session.
	 * 
	 * @param <X>
	 *            the type of the instance
	 * @param instance
	 *            the instance to put into the session
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void put(ManagedInstance<X> instance) {
		final EntityTypeImpl<? super X> type = instance.getType();
		final SubRepository<? super X> subRepository = this.repository.get(type);

		subRepository.put(instance);
	}

	/**
	 * Puts the new instance into the session.
	 * <p>
	 * Additionally stores object in a safe repository that it knows the changes are not traced by enhancement.
	 * 
	 * @param <X>
	 *            the type of the instance
	 * @param instance
	 *            the instance to put into the session
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void putNew(ManagedInstance<X> instance) {
		this.externalEntities.add(instance);

		this.put(instance);
	}

	/**
	 * Puts the new instance into the session. The difference from {@link #putNew(ManagedInstance)} is that the instance needs an insert
	 * execution to obtain its id.
	 * <p>
	 * Under normal circumstances, an implicit flush should be followed by this call.
	 * <p>
	 * Post to the flush operation, the instance will be moved into a safe repository that it knows the changes are not traced by
	 * enhancement.
	 * 
	 * @param instance
	 *            the instance to put
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void putNewIdentifiable(ManagedInstance<?> instance) {
		this.identifiableEntities.add(instance);
	}

	/**
	 * Removes the instance from the session.
	 * 
	 * @param <X>
	 *            the type of the instance
	 * @param instance
	 *            the instance to remove
	 * @param transaction
	 *            the transaction to check against the validity of the transaction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void remove(EntityTransactionImpl transaction, ManagedInstance<X> instance) {
		final EntityTypeImpl<? super X> type = instance.getType();
		final SubRepository<? super X> subRepository = this.repository.get(type);

		final ManagedInstance<?> removed = subRepository.remove(instance);

		if (removed.isExternal()) {
			this.externalEntities.remove(instance);
		}

		removed.cascadeDetach(transaction);

		removed.setTransaction(transaction);
	}
}

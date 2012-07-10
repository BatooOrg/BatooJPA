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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Prioritizer;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

	private final HashMap<ManagedId<?>, ManagedInstance<?>> repository = Maps.newHashMap();

	private final HashSet<ManagedInstance<?>> externalEntities = Sets.newHashSet();
	private final HashSet<ManagedInstance<?>> identifiableEntities = Sets.newHashSet();
	private final HashSet<ManagedInstance<?>> changedEntities = Sets.newHashSet();

	private List<ManagedInstance<?>> entitiesLoading = Lists.newArrayList();

	private int loadTracker = 0;

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
		this.repository.clear();
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
		final int totalSize = this.externalEntities.size() + this.identifiableEntities.size() + this.changedEntities.size();
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
	 * Returns the managed instance instance in the session
	 * 
	 * @param id
	 *            the managed id
	 * @param <X>
	 *            the type of the instance
	 * @return the managed instance or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <Y, X> ManagedInstance<Y> get(ManagedId<X> id) {
		return (ManagedInstance<Y>) this.repository.get(id);
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
		final EntityTypeImpl<X> type = (EntityTypeImpl<X>) this.metamodel.entity(entity.getClass());

		final ManagedId<X> id = new ManagedId<X>(type, entity);

		return (ManagedInstance<X>) this.repository.get(id);
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
	 * Notifies the session that the lazy instance is loading
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void lazyInstanceLoading(ManagedInstance<?> instance) {
		this.entitiesLoading.add(instance);
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
		this.repository.put(instance.getId(), instance);

		if (this.loadTracker > 0) {
			this.entitiesLoading.add(instance);
		}
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
	 * Releases the load tracker, so that the entities loaded are processed for associations and <code>PostLoad</code> listeners are
	 * invoked.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void releaseLoadTracker() {
		this.loadTracker--;

		if (this.loadTracker == 0) {
			// swap the set
			final List<ManagedInstance<?>> entitiesLoaded = this.entitiesLoading;
			this.entitiesLoading = Lists.newArrayList();

			for (final ManagedInstance<?> instance : entitiesLoaded) {
				// check if the transaction is marked as rollback
				final EntityTransactionImpl transaction = this.em.getTransaction();
				if (transaction.getRollbackOnly()) {
					return;
				}

				// mark as loaded
				instance.setLoading(false);

				// process the associations
				instance.processAssociations();
			}
		}
	}

	/**
	 * Removes the instance from the session.
	 * 
	 * @param entity
	 *            the entity to remove
	 * @param <X>
	 *            the type of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> void remove(X entity) {
		final EntityTypeImpl<X> type = (EntityTypeImpl<X>) this.metamodel.entity(entity.getClass());
		final ManagedId<X> instanceId = new ManagedId<X>(type, entity);

		final ManagedInstance<?> instance = this.repository.get(instanceId);
		if (instance != null) {
			this.repository.remove(instanceId);
			this.externalEntities.remove(instance);
			this.identifiableEntities.remove(instance);

			instance.cascadeDetach(this.em);
		}
	}

	/**
	 * Sets the load tracker so that the insertions into session is tracked.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setLoadTracker() {
		this.loadTracker++;
	}
}

/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.PersistenceException;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Prioritizer;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class SessionImpl {

	private static final BLogger LOG = BLoggerFactory.getLogger(SessionImpl.class);

	private static volatile int nextSessionId = 0;

	private final EntityManagerImpl em;
	private final MetamodelImpl metamodel;
	private final int sessionId;

	private final HashMap<ManagedId<?>, ManagedInstance<?>> repository = Maps.newHashMap();

	private final ArrayList<ManagedInstance<?>> newEntities = Lists.newArrayList();
	private final ArrayList<ManagedInstance<?>> externalEntities = Lists.newArrayList();
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
	 * Cascades the removals.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void cascadeRemovals() {
		SessionImpl.LOG.debug("Cascading removals on session {0}", this.sessionId);

		final ArrayList<ManagedInstance<?>> instances = Lists.newArrayList(this.changedEntities);
		for (final ManagedInstance<?> instance : instances) {
			if (instance.getStatus() == Status.REMOVED) {
				instance.cascadeRemove(this.em);
			}
		}
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
		SessionImpl.LOG.debug("Session clearing {0}", this.sessionId);

		// TODO detach all the entities if there is in existing repository
		this.repository.clear();
		this.externalEntities.clear();
		this.changedEntities.clear();
	}

	/**
	 * Checks the versions for Optimistic locking on instances.
	 * 
	 * @param connection
	 *            the connection
	 * @param removals
	 *            the removals
	 * @param updates
	 *            the updates
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void doVersionChecks(ConnectionImpl connection, ManagedInstance<?>[] removals, ManagedInstance<?>[] updates) throws SQLException {
		SessionImpl.LOG.debug("Performing version checks on session {0}", this.sessionId);

		for (final ManagedInstance<?> instance : removals) {
			instance.checkVersion(connection);
		}

		for (final ManagedInstance<?> instance : updates) {
			instance.checkVersion(connection);
		}
	}

	/**
	 * Increments the versions.
	 * 
	 * @param connection
	 *            the connection
	 * @param updates
	 *            the updates
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void doVersionUpgrades(ConnectionImpl connection, ManagedInstance<?>[] updates) throws SQLException {
		SessionImpl.LOG.debug("Performing version upgrades on session {0}", this.sessionId);

		for (final ManagedInstance<?> instance : updates) {
			instance.incrementVersion(connection, false);
		}
	}

	/**
	 * Fires the post callbacks.
	 * 
	 * @param updates
	 *            the list of updates
	 * @param removals
	 *            the list of removals
	 * @param callbackAvailability
	 *            the callback availability
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void firePostCallbacks(final ManagedInstance<?>[] updates, final ManagedInstance<?>[] removals, final CallbackAvailability callbackAvailability) {
		if (callbackAvailability.postRemove()) {
			for (final ManagedInstance<?> instance : removals) {
				instance.fireCallbacks(EntityListenerType.POST_REMOVE);
			}
		}

		if (callbackAvailability.postWrite()) {
			for (final ManagedInstance<?> instance : updates) {
				instance.fireCallbacks(EntityListenerType.POST_UPDATE);
			}
		}
	}

	/**
	 * Fires the pre callbacks.
	 * 
	 * @param updates
	 *            the list of updates
	 * @param removals
	 *            the list of removals
	 * @param callbackAvailability
	 *            the callback availability
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void firePreCallbacks(final ManagedInstance<?>[] sortedUpdates, final ManagedInstance<?>[] sortedRemovals,
		final CallbackAvailability callbackAvailability) {
		if (callbackAvailability.preRemove()) {
			for (final ManagedInstance<?> instance : sortedRemovals) {
				instance.fireCallbacks(EntityListenerType.PRE_REMOVE);
			}
		}

		if (callbackAvailability.preWrite()) {
			for (final ManagedInstance<?> instance : sortedUpdates) {
				instance.fireCallbacks(EntityListenerType.PRE_UPDATE);
			}
		}
	}

	/**
	 * Flushes the session persisting changes to the database.
	 * 
	 * @param connection
	 *            the connection to use
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flush(ConnectionImpl connection) throws SQLException {
		SessionImpl.LOG.debug("Flushing session {0}", this.sessionId);

		final ArrayList<ManagedInstance<?>> updates = Lists.newArrayList(this.newEntities);
		final ArrayList<ManagedInstance<?>> removals = Lists.newArrayListWithCapacity(this.changedEntities.size());

		for (final ManagedInstance<?> instance : this.changedEntities) {
			if (instance.getStatus() == Status.NEW) {
				//should be already in the list the instance is a new instance
				continue;
			}
			else if (instance.getStatus() == Status.REMOVED) {
				removals.add(instance);
			}
			else if (instance.hasSelfUpdate()) {
				updates.add(instance);
			}
		}

		final ManagedInstance<?>[] sortedUpdates = new ManagedInstance[updates.size()];
		final ManagedInstance<?>[] sortedRemovals = new ManagedInstance[removals.size()];

		final CallbackAvailability callbackAvailability = new CallbackAvailability();

		Prioritizer.sort(updates, removals, sortedUpdates, sortedRemovals, callbackAvailability);

		SessionImpl.LOG.debug("Flushing session {0}: updates {1}, removals {2}", this.sessionId, sortedUpdates.length, sortedRemovals.length);

		// fire callbacks
		this.firePreCallbacks(sortedUpdates, sortedRemovals, callbackAvailability);

		this.doVersionChecks(connection, sortedRemovals, sortedUpdates);

		this.doVersionUpgrades(connection, sortedUpdates);

		for (final ManagedInstance<?> instance : sortedRemovals) {
			instance.flushAssociations(connection, true, false);
		}

		for (final ManagedInstance<?> instance : sortedUpdates) {
			instance.flushAssociations(connection, true, false);
		}

		for (final ManagedInstance<?> instance : sortedRemovals) {
			instance.flush(connection);
		}

		for (final ManagedInstance<?> instance : sortedUpdates) {
			instance.flush(connection);
		}

		for (final ManagedInstance<?> instance : sortedUpdates) {
			instance.checkTransients();
		}

		for (final ManagedInstance<?> instance : sortedUpdates) {
			instance.flushAssociations(connection, false, this.newEntities.contains(instance));
			instance.sortLists();
			instance.reset();
		}

		// fire callbacks
		this.firePostCallbacks(sortedUpdates, sortedRemovals, callbackAvailability);

		SessionImpl.LOG.debug("Flush successful for session {0}", this.sessionId);

		// move new entities to external entities
		this.externalEntities.addAll(this.newEntities);

		for (final ManagedInstance<?> instance : this.newEntities) {
			if (!instance.hasInitialId()) {
				this.repository.put(instance.getId(), instance);
			}
		}

		this.changedEntities.clear();
		this.newEntities.clear();
	}

	/**
	 * Returns the managed instance instance in the session
	 * 
	 * @param id
	 *            the managed id
	 * @param <X>
	 *            the type of the instance
	 * @param <Y>
	 *            the actual type of the instance
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
		Class<? extends Object> clazz = null;

		if (entity instanceof EnhancedInstance) {
			final ManagedInstance<?> instance = ((EnhancedInstance) entity).__enhanced__$$__getManagedInstance();
			if (instance.getSession() == this) {
				return (ManagedInstance<X>) instance;
			}

			clazz = entity.getClass().getSuperclass();
		}

		if (clazz == null) {
			clazz = entity.getClass();
		}

		final EntityTypeImpl<X> type = (EntityTypeImpl<X>) this.metamodel.entity(clazz);
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
	 * Handles the additions to the collections.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleAdditions() {
		SessionImpl.LOG.debug("Processing additions to the session {0}", this.sessionId);

		for (final ManagedInstance<?> instance : this.changedEntities) {
			instance.handleAdditions(this.em);
		}
	}

	/**
	 * Handles the external entities that are updated
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleExternals() {
		SessionImpl.LOG.debug("Inspecting updated external entities on session {0}", this.sessionId);

		for (final ManagedInstance<?> instance : this.externalEntities) {
			instance.checkUpdated();
		}
	}

	/**
	 * Removes entities that have been orphaned
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleOrphans() {
		SessionImpl.LOG.debug("Inspecting orphan on session {0}", this.sessionId);

		for (final ManagedInstance<?> instance : this.changedEntities) {
			if (instance.getStatus() != Status.REMOVED) {
				instance.handleOrphans(this.em);
			}
		}
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
		SessionImpl.LOG.debug("Lazy instance is being loaded {0}", instance);

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
	public <X> void putExternal(ManagedInstance<X> instance) {
		if (instance.hasInitialId()) {
			this.repository.put(instance.getId(), instance);
		}

		this.newEntities.add(instance);
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
			SessionImpl.LOG.debug("Load tracker is released on session {0}", this.sessionId);

			// swap the set
			final List<ManagedInstance<?>> entitiesLoaded = this.entitiesLoading;
			this.entitiesLoading = Lists.newArrayList();

			for (final ManagedInstance<?> instance : entitiesLoaded) {
				// check if the transaction is marked as rollback
				if (this.em.hasTransactionMarkedForRollback()) {
					return;
				}

				// mark as loaded
				instance.setLoading(false);

				// process the associations
				instance.processJoinedMappings();
				instance.sortLists();
			}

			for (final ManagedInstance<?> instance : entitiesLoaded) {
				instance.fireCallbacks(EntityListenerType.POST_LOAD);
			}
		}
	}

	/**
	 * Removes the instance from the session.
	 * 
	 * @param entity
	 *            the entity to remove
	 * @return returns the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ManagedInstance<?> remove(Object entity) {
		final EntityTypeImpl<?> type = this.metamodel.entity(entity.getClass());
		final ManagedId<?> instanceId = new ManagedId(type, entity);

		final ManagedInstance<?> instance = this.repository.get(instanceId);
		if (instance != null) {
			this.repository.remove(instanceId);
			this.changedEntities.remove(instance);
			this.externalEntities.remove(instance);
			this.newEntities.remove(instance);
		}

		return instance;
	}

	/**
	 * Marks the instance as changed.
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setChanged(ManagedInstance<?> instance) {
		this.changedEntities.add(instance);

		if (instance.getStatus() == Status.REMOVED) {
			this.externalEntities.remove(instance);
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

		if (this.loadTracker == 1) {
			SessionImpl.LOG.debug("Load tracker is triggered on session {0}", this.sessionId);
		}
	}
}

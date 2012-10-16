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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.PersistenceException;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.cache.CacheImpl;
import org.batoo.jpa.core.impl.cache.CacheInstance;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Prioritizer;
import org.batoo.jpa.core.impl.instance.Status;
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

	/**
	 * Max size of the batch. TODO: Consider making this parametric
	 */
	public static final int BATCH_SIZE = 50;

	private static volatile int nextSessionId = 0;

	private final EntityManagerImpl em;
	private final MetamodelImpl metamodel;
	private final int sessionId;

	private final HashMap<ManagedId<?>, ManagedInstance<?>> repository = Maps.newHashMap();

	private final ArrayList<ManagedInstance<?>> newEntities = Lists.newArrayList();
	private final ArrayList<ManagedInstance<?>> externalEntities = Lists.newArrayList();
	private final HashSet<ManagedInstance<?>> changedEntities = Sets.newHashSet();

	private List<ManagedInstance<?>> entitiesLoading = Lists.newArrayList();
	private Set<ManagedId<?>> idsNotCached = Sets.newHashSet();

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
	 * @param instances
	 *            array of changed instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void cascadeRemovals(ManagedInstance<?>[] instances) {
		SessionImpl.LOG.debug("Cascading removals on session {0}", this.sessionId);

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
	 * Performs the remove operations. Batches together the removes on the same tables.
	 * 
	 * @param connection
	 *            the connection
	 * @param removes
	 *            the array of removes
	 * @throws SQLException
	 *             thrown in case of an underlying SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void doRemoves(Connection connection, final ManagedInstance<?>[] removes) throws SQLException {
		final ManagedInstance<?>[] batch = new ManagedInstance[SessionImpl.BATCH_SIZE];

		int i = 0;

		while (i < removes.length) {
			int batchSize = 0;
			EntityTypeImpl<?> lastEntity = null;

			// group upto BATCH_SIZE and same type entities into a single batch
			while ((i < removes.length) && //
				(batchSize < SessionImpl.BATCH_SIZE) && //
				((lastEntity == null) || (lastEntity == removes[i].getType()))) {

				lastEntity = removes[i].getType();

				batch[batchSize] = removes[i];
				batchSize++;
				i++;
				continue;
			}

			SessionImpl.LOG.debug("Batch remove is being performed for {0} with the size {1}", lastEntity.getName(), batchSize);

			lastEntity.performRemove(connection, batch, batchSize);

			batchSize = 0;
			lastEntity = null;
		}
	}

	/**
	 * Performs the insert / update operations. Batches together the inserts on the same tables.
	 * 
	 * @param connection
	 *            the connection
	 * @param updates
	 *            the array of updates
	 * @throws SQLException
	 *             thrown in case of an underlying SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void doUpdates(Connection connection, final ManagedInstance<?>[] updates) throws SQLException {
		final ManagedInstance<?>[] inserts = new ManagedInstance[SessionImpl.BATCH_SIZE];

		int i = 0;

		while (i < updates.length) {
			EntityTypeImpl<?> lastEntity = null;
			int batchSize = 0;

			// group upto MAX_INSERTS and same type entities that are new into a single batch
			while ((i < updates.length) && //
				(batchSize < SessionImpl.BATCH_SIZE) && //
				(updates[i].getStatus() == Status.NEW) && //
				((lastEntity == null) || (lastEntity == updates[i].getType()))) {

				// batch inserts only possible for non identity-type entities.
				if (lastEntity == null) {
					if (!updates[i].getType().isSuitableForBatchInsert()) {
						break;
					}

					lastEntity = updates[i].getType();
				}

				inserts[batchSize] = updates[i];
				batchSize++;
				i++;
				continue;
			}

			if (batchSize > 0) {
				SessionImpl.LOG.debug("Batch insert is being performed for {0} with the size {1}", lastEntity.getName(), batchSize);

				lastEntity.performInsert(connection, inserts, batchSize);
			}
			else {
				final ManagedInstance<?> instance = updates[i];
				if (instance.getStatus() == Status.NEW) {
					inserts[0] = instance;
					instance.getType().performInsert(connection, inserts, 1);
				}
				else {
					instance.getType().performUpdate(connection, instance);
				}

				i++;
			}
		}
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
	private void doVersionChecks(Connection connection, ManagedInstance<?>[] removals, ManagedInstance<?>[] updates) throws SQLException {
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
	private void doVersionUpgrades(Connection connection, ManagedInstance<?>[] updates) throws SQLException {
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
	public void flush(Connection connection) throws SQLException {
		SessionImpl.LOG.debug("Flushing session {0}", this.sessionId);

		final ArrayList<ManagedInstance<?>> updates = Lists.newArrayList(this.newEntities);
		final ArrayList<ManagedInstance<?>> removals = Lists.newArrayListWithCapacity(this.changedEntities.size());

		for (final ManagedInstance<?> instance : this.changedEntities) {
			if (instance.getStatus() == Status.NEW) {
				// should be already in the list the instance is a new instance
				continue;
			}
			else if (instance.getStatus() == Status.REMOVED) {
				removals.add(instance);
			}
			else if (instance.hasSelfUpdate()) {
				updates.add(instance);
			}
		}

		if ((updates.size() == 0) && (removals.size() == 0)) {
			return;
		}

		final ManagedInstance<?>[] sortedUpdates = new ManagedInstance[updates.size()];
		final ManagedInstance<?>[] sortedRemovals = new ManagedInstance[removals.size()];

		final CallbackAvailability callbackAvailability = new CallbackAvailability();

		Prioritizer.sort(updates, removals, sortedUpdates, sortedRemovals, callbackAvailability);

		SessionImpl.LOG.debug("Flushing session {0}: updates {1}, removals {2}", this.sessionId, sortedUpdates.length, sortedRemovals.length);

		// validations
		final EntityManagerFactoryImpl entityManagerFactory = this.em.getEntityManagerFactory();
		if (entityManagerFactory.hasValidators()) {
			for (final ManagedInstance<?> instance : sortedUpdates) {
				instance.getType().runValidators(entityManagerFactory, instance);
			}

			for (final ManagedInstance<?> instance : sortedRemovals) {
				instance.getType().runValidators(entityManagerFactory, instance);
			}
		}

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

		this.doRemoves(connection, sortedRemovals);
		this.doUpdates(connection, sortedUpdates);

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

		this.putInstancesToCache(this.em.getEntityManagerFactory().getCache(), sortedUpdates, sortedRemovals);

		// move new entities to external entities
		this.externalEntities.addAll(this.newEntities);

		for (int i = 0; i < this.newEntities.size(); i++) {
			final ManagedInstance<?> instance = this.newEntities.get(i);

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
		ManagedInstance<Y> instance = (ManagedInstance<Y>) this.repository.get(id);

		if (instance != null) {
			return instance;
		}

		final CacheImpl cache = this.em.getEntityManagerFactory().getCache();
		if (cache.getCacheRetrieveMode(id.getType()) == CacheRetrieveMode.USE) {
			if (this.idsNotCached.contains(id)) {
				return null;
			}

			final CacheInstance cacheInstance = cache.get(id);
			if (cacheInstance != null) {
				final EntityTypeImpl<X> type = this.metamodel.entity((cacheInstance.getEntityName()));
				instance = (ManagedInstance<Y>) type.getManagedInstanceById(this, id, false);

				cacheInstance.copyTo(cache, instance);
				instance.enhanceCollections();

				this.setLoadTracker();
				try {
					this.put(instance);
				}
				finally {
					this.releaseLoadTracker();
				}

				return instance;
			}

			if (instance == null) {
				this.idsNotCached.add(id);
			}
		}

		return instance;
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
			if ((instance != null) && (instance.getSession() == this)) {
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
	 * @return the array of changed instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<?>[] handleAdditions() {
		SessionImpl.LOG.debug("Processing additions to the session {0}", this.sessionId);

		final ManagedInstance<?>[] instances = this.changedEntities.toArray(new ManagedInstance[this.changedEntities.size()]);
		for (final ManagedInstance<?> instance : instances) {
			instance.handleAdditions(this.em);
		}

		return instances;
	}

	/**
	 * Handles the external entities that are updated
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleExternals() {
		SessionImpl.LOG.debug("Inspecting updated external entities on session {0}", this.sessionId);

		for (int i = 0; i < this.externalEntities.size(); i++) {
			this.externalEntities.get(i).checkUpdated();
		}
	}

	/**
	 * Removes entities that have been orphaned
	 * 
	 * @param instances
	 *            the array of changed instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleOrphans(ManagedInstance<?>[] instances) {
		SessionImpl.LOG.debug("Inspecting orphan on session {0}", this.sessionId);

		for (final ManagedInstance<?> instance : instances) {
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

		if ((this.loadTracker > 0) && instance.isLoading()) {
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

	private void putInstancesToCache(CacheImpl cache, ManagedInstance<?>[] sortedUpdates, ManagedInstance<?>[] sortedRemovals) {
		if (!cache.isOn()) {
			return;
		}

		for (final ManagedInstance<?> instance : sortedRemovals) {
			cache.put(instance);
		}

		for (final ManagedInstance<?> instance : sortedUpdates) {
			if (cache.getCacheStoreMode(instance.getType()) == CacheStoreMode.USE) {
				cache.put(instance);
			}
		}
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
			final CacheImpl cache = this.getEntityManager().getEntityManagerFactory().getCache();

			SessionImpl.LOG.debug("Load tracker is released on session {0}", this.sessionId);

			// swap the set
			final ManagedInstance<?>[] entitiesLoaded = this.entitiesLoading.toArray(new ManagedInstance[this.entitiesLoading.size()]);
			this.entitiesLoading = Lists.newArrayList();
			this.idsNotCached = Sets.newHashSet();

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

				if (!instance.isLoadingFromCache() && (cache.getCacheStoreMode(instance.getType()) != CacheStoreMode.BYPASS)) {
					cache.put(instance);
				}

				// mark as loaded
				instance.setLoadingFromCache(false);
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

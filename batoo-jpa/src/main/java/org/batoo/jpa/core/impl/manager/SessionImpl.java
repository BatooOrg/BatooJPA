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

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Prioritizer;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SessionImpl {

	private static final BLogger LOG = BLoggerFactory.getLogger(SessionImpl.class);

	private static volatile long nextSessionId = 1;

	private final EntityManagerImpl em;
	private final MetamodelImpl metamodel;
	private Object sessionId;

	private final HashMap<ManagedId<?>, ManagedInstance<?>> repository = Maps.newHashMap();

	private final ArrayList<ManagedInstance<?>> newEntities = Lists.newArrayList();
	private final ArrayList<ManagedInstance<?>> externalEntities = Lists.newArrayList();
	private final HashSet<ManagedInstance<?>> changedEntities = Sets.newHashSet();

	private List<ManagedInstance<?>> entitiesLoading = Lists.newArrayList();

	private int loadTracker = 0;

	private final int insertBatchSize;
	private final int removeBatchSize;

	private int sessionTracker;

	/**
	 * @param entityManager
	 *            the owner entity manager
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since 2.0.0
	 */
	public SessionImpl(EntityManagerImpl entityManager, MetamodelImpl metamodel) {
		super();

		this.em = entityManager;
		this.metamodel = metamodel;
		this.insertBatchSize = this.em.getJdbcAdaptor().getInsertBatchSize();
		this.removeBatchSize = this.em.getJdbcAdaptor().getRemoveBatchSize();

		if (SessionImpl.LOG.isDebugEnabled()) {
			this.sessionId = "Session" + SessionImpl.nextSessionId++;
		}
		else {
			this.sessionId = this;
		}
	}

	/**
	 * Cascades the removals.
	 * 
	 * @param instances
	 *            array of changed instances
	 * 
	 * @since 2.0.0
	 */
	public void cascadeRemovals(ManagedInstance<?>[] instances) {
		SessionImpl.LOG.debug("Cascading removals on session {0}", this);

		for (final ManagedInstance<?> instance : instances) {
			if (instance.getStatus() == Status.REMOVED) {
				instance.cascadeRemove(this.em, null, null);
			}
		}
	}

	/**
	 * Checks if the instance is managed in this session
	 * 
	 * @param instance
	 *            the instance to check
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void clear() {
		SessionImpl.LOG.debug("Session clearing {0}", this);

		for (final ManagedInstance<?> instance : this.repository.values()) {
			instance.setStatus(Status.DETACHED);
		}

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
	 * @since 2.0.0
	 */
	private void doRemoves(Connection connection, final ManagedInstance<?>[] removes) throws SQLException {
		final ManagedInstance<?>[] batch = new ManagedInstance[this.removeBatchSize];

		int i = 0;

		while (i < removes.length) {
			int batchSize = 0;
			EntityTypeImpl<?> lastEntity = null;

			int removeBatchSize = this.removeBatchSize;

			// group upto INSERT_BATCH_SIZE and same type entities into a single batch
			while ((i < removes.length) && //
				(batchSize < removeBatchSize) && //
				((lastEntity == null) || (lastEntity == removes[i].getType()))) {

				lastEntity = removes[i].getType();
				if (!lastEntity.canBatchRemoves()) {
					removeBatchSize = 1;
				}

				batch[batchSize] = removes[i];
				batchSize++;
				i++;
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
	 * @since 2.0.0
	 */
	private void doUpdates(Connection connection, final ManagedInstance<?>[] updates) throws SQLException {
		final ManagedInstance<?>[] managedInstances = new ManagedInstance[this.insertBatchSize];

		int i = 0;

		while (i < updates.length) {
			EntityTypeImpl<?> lastEntity = null;
			int batchSize = 0;

			// group upto MAX_INSERTS and same type entities that are new into a single batch
			while ((i < updates.length) && //
				(batchSize < this.insertBatchSize) && //
				(updates[i].getStatus() == Status.NEW) && //
				((lastEntity == null) || (lastEntity == updates[i].getType()))) {

				// batch inserts only possible for non identity-type entities.
				if (lastEntity == null) {
					if (!updates[i].getType().isSuitableForBatchInsert()) {
						break;
					}

					lastEntity = updates[i].getType();
				}

				managedInstances[batchSize] = updates[i];
				batchSize++;
				i++;
				continue;
			}

			if (batchSize > 0) {
				SessionImpl.LOG.debug("Batch insert is being performed for {0} with the size {1}", lastEntity.getName(), batchSize);

				lastEntity.performInsert(connection, managedInstances, batchSize);
			}
			else {
				final ManagedInstance<?> instance = updates[i];
				if (instance.getStatus() == Status.NEW) {
					managedInstances[0] = instance;
					instance.getType().performInsert(connection, managedInstances, 1);
				}
				else {
					instance.getType().performUpdate(connection, instance);
				}

				i++;
			}
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
	 * @since 2.0.0
	 */
	private void doVersionUpgrades(Connection connection, ManagedInstance<?>[] updates) throws SQLException {
		SessionImpl.LOG.debug("Performing version upgrades on session {0}", this);

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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void flush(Connection connection) throws SQLException {
		SessionImpl.LOG.debug("Flushing session {0}", this);

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

		SessionImpl.LOG.debug("Flushing session {0}: updates {1}, removals {2}", this, sortedUpdates.length, sortedRemovals.length);

		// validations
		final EntityManagerFactoryImpl entityManagerFactory = this.em.getEntityManagerFactory();
		if (entityManagerFactory.hasValidators()) {
			final Set<ConstraintViolation<?>> violations = Sets.newHashSet();

			for (final ManagedInstance<?> instance : sortedUpdates) {
				violations.addAll(instance.getType().runValidators(entityManagerFactory, instance));
			}

			for (final ManagedInstance<?> instance : sortedRemovals) {
				violations.addAll(instance.getType().runValidators(entityManagerFactory, instance));
			}

			if (violations.size() > 0) {
				throw new ConstraintViolationException("Cannot flush due to validation errors.", violations);
			}
		}

		// fire callbacks
		this.firePreCallbacks(sortedUpdates, sortedRemovals, callbackAvailability);

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

		SessionImpl.LOG.debug("Flush successful for session {0}", this);

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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
		if (type == null) {
			throw new PersistenceException(entity.getClass().getName() + " is not a persistence class");
		}

		final ManagedId<X> id = type.getId(entity);

		return (ManagedInstance<X>) this.repository.get(id);
	}

	/**
	 * Returns the entity manager.
	 * 
	 * @return the entity manager
	 * 
	 * @since 2.0.0
	 */
	public EntityManagerImpl getEntityManager() {
		return this.em;
	}

	/**
	 * Handles the additions to the collections.
	 * 
	 * @return the array of changed instances
	 * 
	 * @since 2.0.0
	 */
	public ManagedInstance<?>[] handleAdditions() {
		SessionImpl.LOG.debug("Processing additions to the session {0}", this);

		final ManagedInstance<?>[] instances = this.changedEntities.toArray(new ManagedInstance[this.changedEntities.size()]);
		for (final ManagedInstance<?> instance : instances) {
			instance.handleAdditions(this.em);
		}

		return instances;
	}

	/**
	 * Handles the external entities that are updated
	 * 
	 * @since 2.0.0
	 */
	public void handleExternals() {
		SessionImpl.LOG.debug("Inspecting updated external entities on session {0}", this);

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
	 * @since 2.0.0
	 */
	public void handleOrphans(ManagedInstance<?>[] instances) {
		SessionImpl.LOG.debug("Inspecting orphan on session {0}", this);

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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void releaseLoadTracker() {
		this.loadTracker--;

		if (this.loadTracker == 0) {
			SessionImpl.LOG.debug("Load tracker is released on session {0}", this);

			// swap the set
			final ManagedInstance<?>[] entitiesLoaded = this.entitiesLoading.toArray(new ManagedInstance[this.entitiesLoading.size()]);
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

				// mark as loaded
				instance.setLoadingFromCache(false);
			}

			for (final ManagedInstance<?> instance : entitiesLoaded) {
				instance.fireCallbacks(EntityListenerType.POST_LOAD);
			}

			this.sessionTracker--;
			if (this.sessionTracker == 0) {
				SessionImpl.LOG.debug("Session tracker released on session {0}", this);

				this.em.detachAllIfNotTransactionScoped();
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
	 * @since 2.0.0
	 */
	public ManagedInstance<?> remove(Object entity) {
		final EntityTypeImpl<?> type = this.metamodel.entity(entity.getClass());
		final ManagedId<?> instanceId = type.getId(entity);

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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void setLoadTracker() {
		this.loadTracker++;
		this.sessionTracker++;

		if (this.loadTracker == 1) {
			SessionImpl.LOG.debug("Load tracker is triggered on session {0}", this);
		}

		if (this.sessionTracker == 1) {
			SessionImpl.LOG.debug("Session tracker is triggered on session {0}", this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.sessionId.toString();
	}
}

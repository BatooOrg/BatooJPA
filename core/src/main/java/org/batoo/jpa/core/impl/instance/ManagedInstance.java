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
package org.batoo.jpa.core.impl.instance;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The managed instance to track entity instances.
 * 
 * @param <X>
 *            the type of the managed instance
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedInstance<X> {

	private static final BLogger LOG = BLoggerFactory.getLogger(ManagedInstance.class);

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private final X instance;
	private Status status;
	private boolean optimisticLock;
	private LockModeType lockMode;

	private final SingularMapping<? super X, ?> idMapping;
	private final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings;

	private final HashMap<Mapping<?, ?, ?>, Object> snapshot = Maps.newHashMap();
	private final HashSet<AssociationMapping<?, ?, ?>> associationsLoaded;
	private final Set<PluralAssociationMapping<?, ?, ?>> associationsChanged;

	private boolean loading;
	private boolean refreshing;
	private boolean changed;

	private boolean hasInitialId;
	private ManagedId<? super X> id;
	private int h;

	/**
	 * The current lock mode context.
	 */
	public static ThreadLocal<LockModeType> LOCK_CONTEXT = new ThreadLocal<LockModeType>();

	/**
	 * @param type
	 *            the entity type of the instance
	 * @param session
	 *            the session
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance(EntityTypeImpl<X> type, SessionImpl session, X instance) {
		super();

		this.type = type;
		this.session = session;
		this.instance = instance;
		this.lockMode = ManagedInstance.LOCK_CONTEXT.get();

		this.associationsChanged = Sets.newHashSet();
		this.associationsLoaded = Sets.newHashSet();

		if (type.getRootType().hasSingleIdAttribute()) {
			this.idMapping = type.getRootType().getIdMapping();
			this.idMappings = null;
		}
		else {
			this.idMappings = this.type.getIdMappings();
			this.idMapping = null;
		}

		this.status = Status.MANAGED;
	}

	/**
	 * @param type
	 *            the entity type of the instance
	 * @param session
	 *            the session
	 * @param instance
	 *            the instance
	 * @param id
	 *            the id of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance(EntityTypeImpl<X> type, SessionImpl session, X instance, ManagedId<? super X> id) {
		this(type, session, instance);

		if (this.idMapping != null) {
			this.idMapping.set(this, id.getId());
		}
		else {
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.idMappings) {
				final Object value = pair.getSecond().get(id.getId());
				pair.getFirst().set(this, value);
			}
		}

		this.id = id;
	}

	/**
	 * Cascades the detach operation.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void cascadeDetach(EntityManagerImpl entityManager) {
		this.status = Status.DETACHED;

		ManagedInstance.LOG.debug("Cascading detach on {0}", this);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsDetachable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				if (mapping.getAttribute().getCollectionType() == CollectionType.MAP) {
					// TODO handle map
				}
				else {
					// extract the collection
					final Collection<?> collection = (Collection<?>) mapping.get(this.instance);

					// cascade to each element in the collection
					for (final Object element : collection) {
						entityManager.detach(element);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);

				entityManager.detach(associate);
			}
		}
	}

	/**
	 * Cascades the persist operation.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param processed
	 *            registry of processed entities
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean cascadePersist(EntityManagerImpl entityManager, ArrayList<Object> processed) {
		ManagedInstance.LOG.debug("Cascading persist on {0}", this);

		boolean requiresFlush = false;

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsPersistable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				if (mapping.getAttribute().getCollectionType() == CollectionType.MAP) {
					// TODO handle map
				}
				else {
					// extract the collection
					final Collection<?> collection = (Collection<?>) mapping.get(this.instance);

					// cascade to each element in the collection
					for (final Object element : collection) {
						requiresFlush |= entityManager.persistImpl(element, processed);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);
				if (associate != null) {
					requiresFlush |= entityManager.persistImpl(associate, processed);
				}
			}
		}

		return requiresFlush;
	}

	/**
	 * Cascades the remove operation
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void cascadeRemove(EntityManagerImpl entityManager) {
		ManagedInstance.LOG.debug("Cascading remove on {0}", this);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsRemovable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				if (mapping.getAttribute().getCollectionType() == CollectionType.MAP) {
					// TODO handle map
				}
				else {
					// extract the collection
					final Collection<?> collection = (Collection<?>) mapping.get(this.instance);

					// cascade to each element in the collection
					for (final Object element : collection) {
						entityManager.remove(element);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);

				if (associate != null) {
					entityManager.remove(associate);
				}
			}
		}
	}

	/**
	 * Marks the instance as may have changed.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void changed() {
		if (!this.changed && (this.associationsChanged.size() == 0)) {
			this.session.setChanged(this);

		}

		this.snapshot();
		this.changed = true;
	}

	/**
	 * Checks that no association of the instance is transient
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void checkTransients() {
		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsNotPersistable()) {
			association.checkTransient(this);
		}
	}

	/**
	 * Checks if the instance updated.
	 * <p>
	 * Only meaningful for external entities as their instances' are not enhanced.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void checkUpdated() {
		// no snapshot, nothing to check
		if ((this.snapshot.size() == 0) || this.changed) {
			return;
		}

		// iterate over old values
		for (final Mapping<?, ?, ?> mapping : this.type.getMappingsSingular()) {
			final Object newValue = mapping.get(this.instance);
			final Object oldValue = this.snapshot.get(mapping);

			// if it is changed then mark as changed and bail out
			if (oldValue != newValue) {
				this.changed();

				return;
			}
		}
	}

	/**
	 * Checks the optimistic lock for the instance.
	 * 
	 * @param connection
	 *            the connection
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void checkVersion(ConnectionImpl connection) throws SQLException {
		// no optimistic lock, nothing to check
		if (!this.optimisticLock) {
			ManagedInstance.LOG.debug("No optimistic lock support on {0}", this);
			return;
		}

		final EntityTypeImpl<? super X> rootType = this.type.getRootType();
		final Object currentVersion = rootType.getVersionAttribute().get(this.instance);
		final Object expectedVersion = rootType.performSelectVersion(connection, this);

		if (ObjectUtils.notEqual(currentVersion, expectedVersion)) {
			ManagedInstance.LOG.debug("Optimistic lock matches on {0}: {1}", this, expectedVersion);

			throw new PersistenceException("Entity was updated by a different transaction " + this.instance);
		}
		else {
			ManagedInstance.LOG.debug("Optimistic lock mismatches on {0}, found {1}, expected {2}", this, expectedVersion);
		}
	}

	/**
	 * Enhances the collections of the managed instance.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void enhanceCollections() {
		for (final PluralAssociationMapping<?, ?, ?> association : this.type.getAssociationsPlural()) {
			association.enhance(this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		@SuppressWarnings("unchecked")
		final ManagedInstance<? super X> other = (ManagedInstance<? super X>) obj;

		if (this.getId() == null) {
			return false;
		}

		return this.getId().equals(other.getId());
	}

	/**
	 * Fills the sequence / table generated values. The operation returns false if at least one entity needs to obtain identity from the
	 * database.
	 * 
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean fillIdValues() {
		ManagedInstance.LOG.debug("Auto generating id values for {0}", this);

		return this.hasInitialId = this.fillValuesImpl();
	}

	private boolean fillValuesImpl() {
		if (this.idMapping != null) {
			return this.idMapping.fillValue(this.instance);
		}
		else {
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> mapping : this.idMappings) {
				if (!mapping.getSecond().fillValue(this.instance)) {
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * 
	 * Flushes the state of the instance to the database.
	 * 
	 * @param connection
	 *            the connection to use to flush
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flush(ConnectionImpl connection) throws SQLException {
		ManagedInstance.LOG.debug("Flushing instance {0}", this);

		switch (this.status) {
			case NEW:
				this.type.performInsert(connection, this);
				this.status = Status.MANAGED;
				break;
			case MANAGED:
				if (this.changed) {
					this.type.performUpdate(connection, this);
				}
				break;
			case REMOVED:
				this.type.performRemove(connection, this);
				break;
		}
	}

	/**
	 * Flushes the associations.
	 * 
	 * @param connection
	 *            the connection
	 * @param removals
	 *            true if the removals should be flushed and false for the additions
	 * @param force
	 *            true to force, effective only for insertions and for new entities.
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flushAssociations(ConnectionImpl connection, boolean removals, boolean force) throws SQLException {
		ManagedInstance.LOG.debug("Flushing associations for instance {0}", this);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsJoined()) {
			association.flush(connection, this, removals, force);
		}
	}

	/**
	 * Returns the associations that are loaded.
	 * 
	 * @return the associations that are loaded.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public HashSet<AssociationMapping<?, ?, ?>> getAssociationsLoaded() {
		return this.associationsLoaded;
	}

	/**
	 * Returns the id of the instance.
	 * 
	 * @return the id of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId<? super X> getId() {
		if (this.id != null) {
			return this.id;
		}

		return this.id = new ManagedId<X>(this.type, this.instance);
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
	 * Returns the lock mode of the instance.
	 * 
	 * @return the lock mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public LockModeType getLockMode() {
		return this.lockMode;
	}

	/**
	 * Returns the session.
	 * 
	 * @return the session
	 * @since $version
	 */
	public SessionImpl getSession() {
		return this.session;
	}

	/**
	 * Returns the status.
	 * 
	 * @return the status
	 * @since $version
	 */
	public Status getStatus() {
		return this.status;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 * @since $version
	 */
	public EntityTypeImpl<X> getType() {
		return this.type;
	}

	/**
	 * Handles the entities that have been added.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleAdditions(EntityManagerImpl entityManager) {
		ManagedInstance.LOG.debug("Inspecting additions for instance {0}", this);

		for (final PluralAssociationMapping<?, ?, ?> association : this.associationsChanged) {
			association.persistAdditions(entityManager, this);
		}
	}

	/**
	 * Handles the entities that have been orphaned.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleOrphans(EntityManagerImpl entityManager) {
		ManagedInstance.LOG.debug("Inspecting orphans for instance {0}", this);

		for (final PluralAssociationMapping<?, ?, ?> association : this.associationsChanged) {
			association.removeOrphans(entityManager, this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final Object id = this.getId();
		if (id == null) {
			return 1;
		}

		final int prime = 31;
		final int result = 1;

		this.h = (prime * result) + this.type.getRootType().getName().hashCode();

		return this.h = (prime * result) + id.hashCode();
	}

	/**
	 * Increments the version of the instance.
	 * 
	 * @param connection
	 *            the connection
	 * @param commit
	 *            true if version update should be committed immediately
	 * @throws SQLException
	 *             thrown in case of an underlying SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void incrementVersion(ConnectionImpl connection, boolean commit) throws SQLException {
		if (!this.type.getRootType().hasVersionAttribute()) {
			return;
		}

		final EntityTypeImpl<? super X> rootType = this.type.getRootType();

		final BasicAttribute<? super X, ?> version = rootType.getVersionAttribute();

		switch (this.type.getVersionType()) {
			case SHORT:
				final short shortValue = (short) (((Number) version.get(this.instance)).shortValue() + 1);
				version.set(this.instance, shortValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, shortValue);

				break;
			case SHORT_OBJECT:
				final Short shortObjValue = version.get(this.instance) == null ? 0 //
					: new Short((short) (((Number) version.get(this.instance)).shortValue() + 1));
				version.set(this.instance, shortObjValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, shortObjValue);

				break;

			case INT:
				final int intValue = (((Number) version.get(this.instance)).intValue() + 1);
				version.set(this.instance, intValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, intValue);

				break;
			case INT_OBJECT:
				final Integer intObjValue = version.get(this.instance) == null ? 0 : //
					new Integer((((Number) version.get(this.instance)).intValue() + 1));
				version.set(this.instance, intObjValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, intObjValue);

				break;

			case LONG:
				final long longValue = (((Number) version.get(this.instance)).longValue() + 1);
				version.set(this.instance, longValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, longValue);

				break;
			case LONG_OBJECT:
				final Long longObjValue = version.get(this.instance) == null ? 0 : //
					new Long((((Number) version.get(this.instance)).longValue() + 1));
				version.set(this.instance, longObjValue);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, longObjValue);

				break;

			case TIMESTAMP:
				final Timestamp value = new Timestamp(System.currentTimeMillis());
				version.set(this.instance, value);

				ManagedInstance.LOG.debug("Version upgraded instance: {0} - {1}", this, value);
		}

		if (commit) {
			rootType.performVersionUpdate(connection, this);

			ManagedInstance.LOG.debug("Version committed instance: {0} - {1}", this);
		}
		else {
			this.changed = true;
		}
	}

	/**
	 * Returns if the instance is loading.
	 * 
	 * @return true if the instance is loading, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isLoading() {
		return this.loading;
	}

	/**
	 * Returns if the instance is refreshing.
	 * 
	 * @return true if the instance is refreshing, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isRefreshing() {
		return this.refreshing;
	}

	/**
	 * Merges the instance state with the <code>entity</code>.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param entity
	 *            the entity to merge
	 * @param requiresFlush
	 *            if an implicit flush is required
	 * @param processed
	 *            registry of processed entities
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void mergeWith(EntityManagerImpl entityManager, X entity, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed) {
		ManagedInstance.LOG.debug("Merging instance  {0} with {1}", this, entity);

		this.snapshot();

		for (final BasicMapping<?, ?> mapping : this.type.getBasicMappings()) {
			mapping.set(this, mapping.get(entity));
		}

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociations()) {
			association.mergeWith(entityManager, this, entity, requiresFlush, processed);
		}

		this.checkUpdated();
	}

	/**
	 * Processes the associations.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void processAssociations() {
		ManagedInstance.LOG.debug("Post processing associations for instance {0}", this);

		for (final PluralAssociationMapping<?, ?, ?> association : this.type.getAssociationsPlural()) {
			if (!this.associationsLoaded.contains(association)) {
				if (association.isEager()) {
					association.load(this);
				}
				else {
					association.setLazy(this);
				}
			}
		}
	}

	/**
	 * Refreshes the instance from the database.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param connection
	 *            the connection
	 * @param lockMode
	 *            the lock mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void refresh(EntityManagerImpl entityManager, ConnectionImpl connection, LockModeType lockMode) {
		ManagedInstance.LOG.debug("Refeshing instance {0}", this);

		this.type.performRefresh(connection, this, lockMode);

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociations()) {
			if (association instanceof PluralAssociationMapping) {
				((PluralAssociationMapping<?, ?, ?>) association).refreshCollection(entityManager, this);
			}
			else if (association.cascadesRefresh()) {
				final Object associate = association.get(this.instance);

				entityManager.refresh(associate);
			}
		}
	}

	/**
	 * Returns if the instance has initial id.
	 * 
	 * @return true if the instance has initial id, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasInitialId() {
		return this.hasInitialId;
	}

	/**
	 * Resets the change status of the instance.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void reset() {
		ManagedInstance.LOG.debug("Reset instance {0}", this);

		this.associationsChanged.clear();

		this.changed = false;

		this.snapshot();
	}

	/**
	 * Marks the plural association as changed.
	 * 
	 * @param association
	 *            the association that has changed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setAssociationChanged(PluralAssociationMapping<?, ?, ?> association) {
		ManagedInstance.LOG.debug("Association changed for instance: {0}", this, association);

		if ((this.associationsChanged.size() == 0) && !this.changed) {
			this.session.setChanged(this);
		}

		this.associationsChanged.add(association);
	}

	/**
	 * Sets the association as loaded.
	 * 
	 * @param association
	 *            the association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setAssociationLoaded(AssociationMapping<?, ?, ?> association) {
		this.associationsLoaded.add(association);
	}

	/**
	 * Marks the instance as loading.
	 * 
	 * @param loading
	 *            loading to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	/**
	 * Sets the optimistic lock on
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setOptimisticLock() {
		ManagedInstance.LOG.debug("Optimistic lock enabled for instance {0}", this);

		this.optimisticLock = true;
	}

	/**
	 * Marks the instance as refreshing.
	 * 
	 * @param refreshing
	 *            refreshing to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setRefreshing(boolean refreshing) {
		this.refreshing = refreshing;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status to set
	 * @since $version
	 */
	public void setStatus(Status status) {
		if (status != this.status) {
			ManagedInstance.LOG.debug("Instance status changing for {0}: {1} -> {2}", this, this.status, status);

			this.status = status;
		}
	}

	/**
	 * Creates a snapshot of the entity.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void snapshot() {
		ManagedInstance.LOG.debug("Snapshot generated for instance {0}", this);

		for (final Mapping<?, ?, ?> mapping : this.type.getMappingsSingular()) {
			this.snapshot.put(mapping, mapping.get(this.instance));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedInstance [session=" + this.session.getSessionId() //
			+ ", type=" + this.type.getName() //
			+ ", status=" + this.status //
			+ ", id=" + (this.id != null ? this.id.getId() : null) //
			+ ", instance=" //
			+ this.instance + "]";
	}
}

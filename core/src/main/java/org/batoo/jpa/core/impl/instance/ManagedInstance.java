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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;

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

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private final X instance;
	private Status status;

	private final SingularMapping<? super X, ?> idMapping;
	private final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings;

	private final HashSet<AssociationMapping<?, ?, ?>> associationsLoaded;
	private final Set<PluralAssociationMapping<?, ?, ?>> associationsChanged;

	private boolean loading;
	private boolean refreshing;
	private boolean changed;

	private ManagedId<? super X> id;
	private int h;

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
	 * Cascades the merge operation
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean cascadeMerge(EntityManagerImpl entityManager) {
		boolean requiresFlush = false;

		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsMergable()) {

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
						requiresFlush |= entityManager.mergeImpl(element);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);
				requiresFlush |= entityManager.mergeImpl(associate);
			}
		}

		return requiresFlush;

	}

	/**
	 * Cascades the persist operation.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean cascadePersist(EntityManagerImpl entityManager) {
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
						requiresFlush |= entityManager.persistImpl(element);
					}
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);
				if (associate != null) {
					requiresFlush |= entityManager.persistImpl(associate);
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
				entityManager.remove(associate);
			}
		}
	}

	public void changed() {
		if (!this.changed && (this.associationsChanged.size() == 0)) {
			this.session.setChanged(this);
		}

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
		if (this.status == null) {
			this.type.performInsert(connection, this);

			this.status = Status.MANAGED;
			this.session.putExternal(this);
		}
		else {
			switch (this.status) {
				case NEW:
					this.type.performInsert(connection, this);
					this.status = Status.MANAGED;
					break;
				case MANAGED:
					this.type.performUpdate(connection, this);
					break;
				case REMOVED:
					this.type.performRemove(connection, this);
					break;
			}
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
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void handleOrphans(EntityManagerImpl entityManager) {
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
	 * Merges the entity state with the <code>entity</code>. Also handles cascades.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param entity
	 *            the entity to merge
	 * @return if flush is required
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean mergeWith(EntityManagerImpl entityManager, X entity) {
		if (this.instance != entity) {
			for (final BasicMapping<?, ?> mapping : this.type.getBasicMappings()) {
				mapping.set(this, mapping.get(entity));
			}

			final boolean requiresFlush = this.cascadeMerge(entityManager);

			for (final AssociationMapping<?, ?, ?> association : this.type.getAssociationsMergable()) {
				association.mergeWith(entityManager, this, entity);
			}

			this.session.setChanged(this);

			return requiresFlush;
		}

		return false;
	}

	/**
	 * Processes the associations.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void processAssociations() {
		for (final AssociationMapping<?, ?, ?> association : this.type.getAssociations()) {
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void refresh(EntityManagerImpl entityManager, ConnectionImpl connection) {
		this.type.performRefresh(connection, this);

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
	 * Resets the change status of the instance.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void reset() {
		this.associationsChanged.clear();
		this.changed = false;
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
		this.status = status;
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
			+ ", id=" + this.id //
			+ ", instance=" //
			+ this.instance + "]";
	}
}

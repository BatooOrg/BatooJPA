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
import java.util.Set;

import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.EntityTransactionImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
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

	/**
	 * The states for a managed instance
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum Status {
		/**
		 * Instance is new
		 */
		NEW,

		/**
		 * Instance is managed
		 */
		MANAGED,

		/**
		 * Instance is removed
		 */
		REMOVED,

		/**
		 * Instance is detached
		 */
		DETACHED,
	}

	private final EntityTypeImpl<X> type;
	private final SessionImpl session;
	private final X instance;
	private Status status;

	private final Set<PluralAssociationMapping<?, ?, ?>> changedCollections = Sets.newHashSet();
	private final SingularMapping<? super X, ?> idMapping;
	private final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings;
	private final EmbeddableTypeImpl<?> idType;

	private final Set<AssociationMapping<?, ?>> associationsLoaded = Sets.newHashSet();
	private EntityTransactionImpl transaction;

	private final boolean external = true;
	private Object id;

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

		if (type.getRootType().hasSingleIdAttribute()) {
			this.idMapping = type.getRootType().getIdMapping();
			this.idMappings = null;
			this.idType = null;
		}
		else {
			this.idType = (EmbeddableTypeImpl<?>) this.type.getRootType().getIdType();
			this.idMappings = this.type.getIdMappings();
			this.idMapping = null;
		}
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
	public ManagedInstance(EntityTypeImpl<X> type, SessionImpl session, X instance, Object id) {
		this(type, session, instance);

		this.id = id;
		if (this.idMapping != null) {
			this.idMapping.set(this, id);
		}
		else {
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.idMappings) {
				final Object value = pair.getSecond().get(id);
				pair.getFirst().set(this, value);
			}
		}
	}

	/**
	 * 
	 * @param transaction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void cascadeDetach(EntityTransactionImpl transaction) {
		// TODO Auto-generated method stub

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

		for (final AssociationMapping<?, ?> association : this.type.getAssociationsPersistable()) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof PluralAssociationMapping) {
				final PluralAssociationMapping<?, ?, ?> mapping = (PluralAssociationMapping<?, ?, ?>) association;

				// extract the collection
				final Collection<?> collection = (Collection<?>) mapping.get(this.instance);

				// cascade to each element in the collection
				for (final Object element : collection) {
					requiresFlush |= entityManager.persistImpl(element);
				}
			}
			else {
				final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) association;
				final Object associate = mapping.get(this.instance);
				requiresFlush |= entityManager.persistImpl(associate);
			}
		}

		return requiresFlush;
	}

	/**
	 * Checks that no association of the instance is transient
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void checkTransients() {
		for (final AssociationMapping<?, ?> association : this.type.getAssociations()) {
			association.checkTransient(this);
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

		if ((this.instance != null) && (this.instance == other.instance)) {
			return true;
		}

		if (this.getType() != other.getType()) {
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
	 * @param transaction
	 *            the transaction to perform flush against
	 * @param connection
	 *            the connection to use to flush
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flush(ConnectionImpl connection, EntityTransactionImpl transaction) throws SQLException {
		if (this.status == null) {
			this.type.performInsert(connection, transaction, this);

			this.status = Status.MANAGED;
			this.session.putNew(this);
		}
		else {
			switch (this.status) {
				case NEW:
					this.type.performInsert(connection, transaction, this);
					this.status = Status.MANAGED;
					break;
				case MANAGED:
					this.type.performUpdate(connection, transaction, this);
					break;
				case REMOVED:
					this.type.performRemove(connection, transaction, this);
					break;
			}
		}

		this.transaction = transaction;
	}

	/**
	 * Flushes the associations.
	 * 
	 * @param connection
	 *            the connection
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void flushAssociations(ConnectionImpl connection) throws SQLException {
		for (final AssociationMapping<?, ?> association : this.type.getAssociationsJoined()) {
			association.flush(connection, this);
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
	public Set<AssociationMapping<?, ?>> getAssociationsLoaded() {
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
	public Object getId() {
		if (this.id != null) {
			return this.id;
		}

		if (this.idMapping != null) {
			return this.id = this.idMapping.get(this.instance);
		}

		this.id = this.idType.newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.idMappings) {
			final Object value = pair.getSecond().get(this.instance);
			pair.getFirst().getAttribute().set(this.id, value);
		}

		return this.id;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final int prime = 31;
		final int result = 1;

		final Object id = this.getId();
		if (id == null) {
			return result;
		}

		return this.h = (prime * result) + id.hashCode();
	}

	/**
	 * Returns the external.
	 * 
	 * @return the external
	 * @since $version
	 */
	public boolean isExternal() {
		return this.external;
	}

	/**
	 * Marks a collection as changed.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void markCollectionChanged(PluralAssociationMapping<?, ?, ?> mapping) {
		this.changedCollections.add(mapping);
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
	public void setAssociationLoaded(AssociationMapping<?, ?> association) {
		this.associationsLoaded.add(association);
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
	 * Sets the transaction for the instance.
	 * <p>
	 * Having a transaction as a mark on an instance is useful in case of detaching, if the transaction is still active the transaction will
	 * be set to roll back only.
	 * 
	 * @param transaction
	 *            the transaction to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setTransaction(EntityTransactionImpl transaction) {
		if ((this.transaction != null) && (this.transaction.isActive())) {
			this.transaction.setRollbackOnly();
		}

		this.transaction = transaction;
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
			+ ", external=" + this.external //
			+ ", instance=" //
			+ this.instance + "]";
	}
}

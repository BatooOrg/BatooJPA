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
import java.util.IdentityHashMap;
import java.util.List;

import org.batoo.jpa.core.impl.EntityTransactionImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.SimpleEntityManager;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.batoo.jpa.core.impl.metamodel.SingularAttributeImpl;
import org.batoo.jpa.core.impl.metamodel.TypeImpl;

import com.google.common.collect.Maps;

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
	private final List<BasicMapping<?, ?>> idMappings;
	private BasicMapping<?, ?> singleId;
	private EntityTransactionImpl transaction;
	private boolean initialized;

	private Object id;
	private boolean loaded;
	private final IdentityHashMap<CollectionMapping<?, ?, ?>, Object> enhancedCollections = Maps.newIdentityHashMap();

	private int h;
	private SingularAttributeImpl<?, ?> embeddedAttribute;
	private Class<?> idJavaType;

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

		this.idMappings = type.getRoot().getIdMappings();
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

		this.populateId();
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
	public void cascadeDetach(SimpleEntityManager entityManager) {
		final Association<?, ?>[] associations = this.type.getAssociationsDetachable();

		for (final Association<?, ?> association : associations) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof CollectionMapping) {
				final CollectionMapping<?, ?, ?> collectionMapping = (CollectionMapping<?, ?, ?>) association;
				final Collection<?> collection = (Collection<?>) collectionMapping.getValue(this.instance);

				// cascade to each element in the collection
				for (final Object element : collection) {
					entityManager.detach(element);
				}
			}
			else {
				final Object associate = association.getValue(this.instance);
				entityManager.detach(associate);
			}
		}
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
	public boolean cascadePersist(SimpleEntityManager entityManager) {
		boolean requiresFlush = false;

		final Association<?, ?>[] associations = this.type.getAssociationsPersistable();

		for (final Association<?, ?> association : associations) {

			// if the association a collection attribute then we will cascade to each element
			if (association instanceof CollectionMapping) {
				final CollectionMapping<?, ?, ?> collectionMapping = (CollectionMapping<?, ?, ?>) association;
				final Collection<?> collection = (Collection<?>) collectionMapping.getValue(this.instance);

				// cascade to each element in the collection
				for (final Object element : collection) {
					requiresFlush |= entityManager.persistImpl(element);
				}
			}
			else {
				final Object associate = association.getValue(this.instance);
				requiresFlush |= entityManager.persistImpl(associate);
			}
		}

		return requiresFlush;
	}

	/**
	 * Enhances the collection.
	 * 
	 * @param association
	 *            the association to enhance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void enhanceCollection(CollectionMapping<?, ?, ?> association) {
		if (!this.enhancedCollections.containsKey(association)) {
			this.enhancedCollections.put(association, null);

			association.getDeclaringAttribute().newInstance(this, false);
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

		if (this.instance == other.instance) {
			return true;
		}

		if (!this.type.getIdentityRoot().equals(other.type.getIdentityRoot())) {
			return false;
		}

		for (final BasicMapping<?, ?> mapping : this.idMappings) {
			final Object id1 = mapping.getValue(this.instance);
			final Object id2 = mapping.getValue(other.instance);

			if (!id1.equals(id2)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Fills the sequence / table generated values.
	 * The operation returns false if at least one entity needs to obtain identity from the database.
	 * 
	 * @return false if all ok, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean fillIdValues() {
		boolean allFilled = true;
		for (final BasicMapping<?, ?> mapping : this.idMappings) {
			allFilled &= mapping.getDeclaringAttribute().fillValue(this.instance);
		}

		return allFilled;
	}

	/**
	 * 
	 * Flushes the state of the instance to the database.
	 * 
	 * @param connection
	 *            the connection to use to flush
	 * @param transaction
	 *            the transaction to perform flush against
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
	 * 
	 * Flushes the associations of the instance to the database.
	 * 
	 * @param connection
	 *            the connection to use to flush
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void flushAssociations(ConnectionImpl connection) throws SQLException {
		for (final PersistableAssociation association : this.type.getAssociationsOwnedPersistable()) {
			association.performInsert(connection, this);
		}
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

		this.initialize();

		if (this.singleId != null) {
			return this.singleId.getValue(this.instance);
		}

		if (this.embeddedAttribute != null) {
			return this.embeddedAttribute.get(this.instance);
		}

		Object id = null;
		try {
			id = this.idJavaType.newInstance();
		}
		catch (final Exception e) {}

		for (final SingularAttributeImpl<?, ?> attribute : this.type.getIdAttributes()) {
			final SingularAttributeImpl<?, ?> idAttribute = attribute;
			idAttribute.set(id, idAttribute.get(this.instance));
		}

		if (id != null) {
			this.id = id;
		}

		return id;
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
		int result = 1;
		for (final BasicMapping<?, ?> mapping : this.idMappings) {
			final Object idValue = mapping.getValue(this.instance);
			result = (prime * result) + idValue.hashCode();
		}

		return this.h = result;
	}

	/**
	 * Returns if the instance has an id.
	 * 
	 * @return true if the instance has an id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasId() {
		for (final BasicMapping<?, ?> mapping : this.idMappings) {
			if (mapping.getValue(this.instance) == null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	private void initialize() {
		if (!this.initialized) {

			if (this.idMappings.size() == 1) {
				this.singleId = this.idMappings.get(0);
			}
			else {
				final TypeImpl<?> idType = this.type.getIdType();
				if ((idType != null) && idType.isEmbeddable()) {
					this.embeddedAttribute = this.type.getIdAttributes()[0];
				}
				else if (this.type.getIdJavaType() != null) {
					this.idJavaType = this.type.getIdJavaType();
				}
			}

			this.initialized = true;
		}
	}

	/**
	 * Returns the loaded.
	 * 
	 * @return the loaded
	 * @since $version
	 */
	public boolean isLoaded() {
		return this.loaded;
	}

	/**
	 * Populates the instance's id with the id.
	 * 
	 * @param id
	 *            the id for the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void populateId() {
		this.initialize();

		if (this.embeddedAttribute != null) {
			this.embeddedAttribute.set(this.instance, this.id);
		}
		else if (this.type.getIdJavaType() != null) {
			for (final SingularAttributeImpl<?, ?> attribute : this.type.getIdAttributes()) {
				final SingularAttributeImpl<? super X, ?> idAttribute = (SingularAttributeImpl<? super X, ?>) attribute;
				idAttribute.set(this.instance, idAttribute.get(this.id));
			}
		}
		else {
			for (final BasicMapping<?, ?> mapping : this.idMappings) {
				mapping.setValue(this.instance, this.id);
			}
		}
	}

	/**
	 * Sets the loaded.
	 * 
	 * @param loaded
	 *            the loaded to set
	 * @since $version
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
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
			+ ", id=" + this.getId() //
			+ ", instance=" //
			+ this.instance + "]";
	}
}

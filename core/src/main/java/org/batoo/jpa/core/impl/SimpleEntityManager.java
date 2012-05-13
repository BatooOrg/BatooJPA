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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

/**
 * The base class for {@link EntityManager}.
 * <p>
 * Partitions the base persistence operations down to keep clean.
 * <ul>
 * List of operations implemented at this level is
 * <li>Transaction management
 * <li>Entity lifecycle
 * <li>Session management
 * <li>Task queue management
 * </ul>
 * 
 * @author hceylan
 * @since $version
 */
public abstract class SimpleEntityManager implements EntityManager {

	private static final BLogger LOG = BLogger.getLogger(EntityManagerImpl.class);

	// Reference properties
	protected final EntityManagerFactoryImpl emf;
	private final MetamodelImpl metamodel;
	@SuppressWarnings("rawtypes")
	protected final Map properties;
	protected final DataSourceImpl datasource;

	// Lifecycle properties
	protected boolean open;
	private ConnectionImpl connection;
	private EntityTransactionImpl transaction;
	private final SessionImpl session;

	/**
	 * @param emf
	 * @param properties
	 * @param datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SimpleEntityManager(EntityManagerFactoryImpl emf, @SuppressWarnings("rawtypes") Map properties, DataSourceImpl datasource) {
		super();

		this.emf = emf;
		this.metamodel = emf.getMetamodel();
		this.properties = properties;
		this.datasource = datasource;

		this.open = true;
		this.session = new SessionImpl((EntityManagerImpl) this);
	}

	private void assertOpen() {
		if (!this.open) {
			throw new IllegalStateException("EntityManager has been previously closed");
		}
	}

	private void assertTransaction() {
		this.assertOpen();

		if ((this.transaction == null) || !this.transaction.isActive()) {
			throw new TransactionRequiredException("No active transaction");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized void clear() {
		this.assertOpen();

		if ((this.transaction != null) && this.transaction.isActive()) {
			this.transaction.rollback();
			LOG.warn("Session cleared with active and transaction. Updated persistent types will become stale...");
		}

		this.session.clear();
	}

	/**
	 * Clears the transaction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void clearTransaction() {
		this.assertOpen();

		this.transaction = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized void close() {
		this.assertOpen();

		if ((this.transaction != null) && this.transaction.isActive()) {
			this.transaction.rollback();

			LOG.warn("Entity manager closed with an active transaction. Updated persistent types will become stale...");
		}

		this.session.clear();
		try {
			if (this.connection != null) {
				this.connection.close();
			}
			this.connection = null;
		}
		catch (final SQLException e) {}

		this.open = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object entity) {
		this.assertOpen();

		final EntityTypeImpl<?> type = this.metamodel.entity(entity.getClass());
		final ManagedInstance<?> instance = type.getManagedInstance(this.getSession(), entity);

		return this.session.get(instance) != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void detach(Object entity) {
		this.assertOpen();

		final EntityTypeImpl<?> type = this.metamodel.entity(entity.getClass());
		final ManagedInstance<?> instance = type.getManagedInstance(this.getSession(), entity);

		this.getSession().remove(this.transaction, instance);

		instance.cascadeDetach(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		if (primaryKey == null) {
			throw new NullPointerException();
		}

		// try to locate in the session
		final EntityTypeImpl<T> type = this.metamodel.entity(entityClass);
		final ManagedInstance<T> instance = type.getManagedInstanceById(this.getSession(), primaryKey);
		final ManagedInstance<T> existing = this.getSession().get(instance);
		if (existing != null) {
			return existing.getInstance();
		}

		try {
			return type.performSelect(this.session, instance);
		}
		catch (final SQLException e) {
			throw new PersistenceException("Entity cannot be loaded");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * Returns the items for the lazy association.
	 * 
	 * @param managedInstance
	 *            the managed instance of which the association to be reolved
	 * @param association
	 *            the association to be resolved
	 * @return the collection of items
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X, C, E> Collection<E> findAll(ManagedInstance<?> managedInstance, CollectionMapping<X, C, E> association) {
		this.assertOpen();

		try {
			return association.performSelect(this.session, (ManagedInstance<X>) managedInstance);
		}
		catch (final SQLException e) {
			throw new PersistenceException("Unable to initialize the collection");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush() {
		this.assertOpen();

		try {
			this.session.flush(this.connection, this.transaction);
		}
		catch (final SQLException e) {
			throw new PersistenceException("Flush failed", e);
		}
	}

	/**
	 * Returns the active connection.
	 * 
	 * @return the connection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized ConnectionImpl getConnection() {
		if (this.connection == null) {
			try {
				this.connection = this.datasource.getConnection();
			}
			catch (final SQLException e) {
				throw new PersistenceException("Unable to obtain connection from the datasource", e);
			}
		}

		return this.connection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getDelegate() {
		this.assertOpen();

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactoryImpl getEntityManagerFactory() {
		return this.emf;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FlushModeType getFlushMode() {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode(Object entity) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetamodelImpl getMetamodel() {
		return this.emf.getMetamodel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * Returns the session
	 * 
	 * @return the session
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SessionImpl getSession() {
		return this.session;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized EntityTransaction getTransaction() {
		if (this.transaction != null) {
			return this.transaction;
		}

		this.transaction = new EntityTransactionImpl((EntityManagerImpl) this, this.getConnection());

		return this.transaction;
	}

	private void handleBatooException(BatooException e) {
		// if the cause is already PersistenceException then throw it
		if (e.getCause() instanceof PersistenceException) {
			throw (PersistenceException) e.getCause();
		}

		// if the cause is null or not a RuntimeException then throw
		if (e.getCause() instanceof RuntimeException) {
			throw (RuntimeException) e.getCause();
		}

		// wrap it up into a PersistenceException and throw
		throw new PersistenceException(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return this.open;
	}

	/**
	 * @param entityTransactionImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void isValid(EntityTransactionImpl transaction) {
		if (this.transaction != transaction) {
			throw new PersistenceException("Transaction is stale");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void joinTransaction() {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void lock(Object entity, LockModeType lockMode) {
		if (lockMode == LockModeType.NONE) {
			throw new IllegalArgumentException("LokcMode cannot be NONE");
		}

		this.assertTransaction();

		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		this.lock(entity, lockMode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T merge(T entity) {
		this.assertTransaction();

		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void persist(Object entity) {
		this.assertTransaction();

		final boolean flush = this.persistImpl(entity);
		if (flush) {
			this.flush();
		}
	}

	/**
	 * Cascaded implementation of {@link #persist(Object)}.
	 * <p>
	 * Also manages a direct or indirect requirement to an implicit flush.
	 * 
	 * @param entity
	 *            the entity to cascade
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean persistImpl(Object entity) {
		boolean requiresFlush = false;

		final EntityTypeImpl<?> type = this.metamodel.entity(entity.getClass());
		final ManagedInstance<?> instance = type.getManagedInstance(this.getSession(), entity);

		ManagedInstance<?> existing = null;

		if (instance.hasId()) {
			existing = this.getSession().get(instance);
		}

		if (existing != null) {
			switch (existing.getStatus()) {
				case DETACHED:
					throw new EntityExistsException("Entity has been previously detached");
				case MANAGED:
					requiresFlush |= existing.cascadePersist(this);
					break;
				case REMOVED:
					existing.setStatus(Status.MANAGED);
					break;
				case NEW:
					break;
			}
		}
		else {
			requiresFlush = !instance.fillIdValues();
			requiresFlush |= instance.cascadePersist(this);

			if (!requiresFlush) {
				instance.setStatus(Status.NEW);

				this.session.putNew(instance);
			}
			else {
				this.session.putNewIdentifiable(instance);
			}
		}

		return requiresFlush;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity) {
		this.refresh(entity, LockModeType.NONE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, LockModeType lockMode) {
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		this.refresh(entity, lockMode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		this.refresh(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void remove(Object entity) {
		this.assertTransaction();

		this.throwNotImplemented();
	}

	protected void throwNotImplemented() {
		throw new NotImplementedException();
	}

}

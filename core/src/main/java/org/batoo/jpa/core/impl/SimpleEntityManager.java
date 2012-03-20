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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.sql.DataSource;

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.operation.DetachOperation;
import org.batoo.jpa.core.impl.operation.FindOperation;
import org.batoo.jpa.core.impl.operation.OperationManager;
import org.batoo.jpa.core.impl.operation.PersistOperation;
import org.batoo.jpa.core.impl.operation.TaskQueue;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

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
	@SuppressWarnings("rawtypes")
	protected final Map properties;
	protected final DataSource datasource;

	// Lifecycle properties
	protected boolean open;
	private Connection connection;
	private EntityTransactionImpl transaction;
	private final SessionImpl session;

	private final TaskQueue taskQueue;

	/**
	 * @param emf
	 * @param properties
	 * @param datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SimpleEntityManager(EntityManagerFactoryImpl emf, @SuppressWarnings("rawtypes") Map properties, DataSource datasource) {
		super();

		this.emf = emf;
		this.properties = properties;
		this.datasource = datasource;

		this.open = true;
		this.session = new SessionImpl((EntityManagerImpl) this);
		this.taskQueue = new TaskQueue((EntityManagerImpl) this);
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

		return this.getSession().get(entity) != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void detach(Object entity) {
		this.assertOpen();

		OperationManager.prepare(this.getSession(), new DetachOperation<Object>((EntityManagerImpl) this, entity));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		this.assertOpen();

		final EntityTypeImpl<T> type = this.getMetamodel().entity(entityClass);
		final ManagedId<? super T> managedId = type.getManagedId(this.session, primaryKey);

		@SuppressWarnings("rawtypes")
		final FindOperation<T> operation = new FindOperation((EntityManagerImpl) this, managedId);

		try {
			OperationManager.prepare(this.getSession(), operation);
		}
		catch (final RuntimeException e) {
			LOG.error(e, "Find operation encountered an error");

			if ((this.transaction != null) && this.transaction.isActive()) {
				this.transaction.setRollbackOnly();
			}

			throw e;
		}

		return (T) operation.getManagedInstance().getInstance();
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized void flush() {
		this.assertOpen();

		try {
			this.taskQueue.flush(this.connection);
		}
		catch (final BatooException e) {
			this.getTransaction().setRollbackOnly();

			this.handleBatooException(e);
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
	public synchronized Connection getConnection() {
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
	 * Returns the taskQueue.
	 * 
	 * @return the taskQueue
	 * @since $version
	 */
	public TaskQueue getTaskQueue() {
		return this.taskQueue;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void persist(Object entity) {
		this.assertTransaction();

		OperationManager.prepare(this.getSession(), new PersistOperation<Object>((EntityManagerImpl) this, entity));
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
		if (lockMode != LockModeType.NONE) {
			this.assertTransaction();
		}
		else {
			this.assertOpen();
		}

		this.throwNotImplemented();
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

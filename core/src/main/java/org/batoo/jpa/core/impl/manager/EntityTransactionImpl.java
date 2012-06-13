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

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;

import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;

/**
 * Implementation of {@link EntityTransaction}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTransactionImpl implements EntityTransaction {

	private final EntityManagerImpl em;
	private final ConnectionImpl connection;
	private boolean active;
	private boolean rollbackOnly;

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param connection
	 *            the active connection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTransactionImpl(EntityManagerImpl entityManager, ConnectionImpl connection) {
		super();

		this.em = entityManager;
		this.connection = connection;
	}

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void assertValid() {
		this.em.isValid(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void begin() {
		try {
			if (!this.active) {
				this.connection.setAutoCommit(false);
			}
		}
		catch (final SQLException e) {
			throw new PersistenceException("Unable to begin transaction", e);
		}

		this.active = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void commit() {
		if (!this.active) {
			throw new TransactionRequiredException("Transaction has not been started");
		}

		this.assertValid();

		if (this.rollbackOnly) {
			throw new TransactionRequiredException("Transaction is marked as rollback only");
		}

		try {
			this.em.flush();

			this.connection.setAutoCommit(true);

			this.em.clearTransaction();

			this.active = false;
		}
		catch (final SQLException e) {
			throw new PersistenceException("Unable to commit transaction", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean getRollbackOnly() {
		return this.rollbackOnly;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isActive() {
		return this.active;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void rollback() {
		this.assertValid();

		try {
			this.connection.rollback();

			this.em.clearTransaction();
		}
		catch (final SQLException e) {
			throw new PersistenceException("Unable to rollback transaction", e);
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setRollbackOnly() {
		this.rollbackOnly = true;
	}

}

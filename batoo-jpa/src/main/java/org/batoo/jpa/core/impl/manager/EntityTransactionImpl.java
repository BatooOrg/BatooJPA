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
		if (this.rollbackOnly) {
			throw new PersistenceException("Transaction is set to rollback only");
		}

		this.em.isValid(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void begin() {
		this.assertValid();

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

			this.connection.commit();
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
		return this.active && !this.rollbackOnly;
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

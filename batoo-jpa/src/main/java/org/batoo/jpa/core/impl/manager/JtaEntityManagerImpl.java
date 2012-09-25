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

import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;

/**
 * Entity Manager for JTA Environments.
 * 
 * @author hceylan
 * @since $version
 */
public class JtaEntityManagerImpl extends EntityManagerImpl {

	private Transaction jtaTransaction;
	private final JtaEntityManagerFactoryImpl emf;

	/**
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param metamodel
	 *            the metamodel
	 * @param datasource
	 *            the datasource
	 * @param properties
	 *            properties for the entity manager
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @since $version
	 * @author hceylan
	 */
	public JtaEntityManagerImpl(JtaEntityManagerFactoryImpl entityManagerFactory, MetamodelImpl metamodel, DataSourceImpl datasource,
		Map<String, Object> properties, JdbcAdaptor jdbcAdaptor) {
		super(entityManagerFactory, metamodel, datasource, properties, jdbcAdaptor);

		this.emf = entityManagerFactory;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void assertTransaction() {
		this.assertOpen();

		this.joinTransaction();

		if (this.jtaTransaction == null) {
			throw new TransactionRequiredException("No active transaction");
		}

		return;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void closeConnection() {
		super.closeConnection();

		this.jtaTransaction = null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void closeConnectionIfNecessary() {
		if (this.jtaTransaction == null) {
			this.closeConnection();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTransactionImpl getTransaction() {
		throw new PersistenceException("Transactions are configured as container managed");

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasTransactionMarkedForRollback() {
		if (this.jtaTransaction != null) {
			try {
				return this.jtaTransaction.getStatus() == javax.transaction.Status.STATUS_MARKED_ROLLBACK;
			}
			catch (final SystemException e) {
				throw new PersistenceException("Cannot check transaction status", e);
			}
		}

		return super.hasTransactionMarkedForRollback();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isJoinedToTransaction() {
		return this.jtaTransaction != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void joinTransaction() {
		if (this.jtaTransaction != null) {
			return;
		}

		this.assertOpen();

		try {
			this.jtaTransaction = this.emf.getTransactionManager().getTransaction();

			if (this.jtaTransaction != null) {
				this.jtaTransaction.registerSynchronization(new Synchronization() {

					@Override
					public void afterCompletion(int status) {
						JtaEntityManagerImpl.this.closeConnection();
					}

					@Override
					public void beforeCompletion() {
						JtaEntityManagerImpl.this.flush();
					}
				});
			}
		}
		catch (final Exception e) {
			throw new PersistenceException("Unable to join JTA");
		}
	}

}

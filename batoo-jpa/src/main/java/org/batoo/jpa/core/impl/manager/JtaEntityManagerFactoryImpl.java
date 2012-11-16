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

import java.util.Collections;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceException;
import javax.transaction.TransactionManager;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.parser.PersistenceParser;

/**
 * Entity Manager factory for JTA Environments.
 * 
 * @author hceylan
 * @since $version
 */
public class JtaEntityManagerFactoryImpl extends EntityManagerFactoryImpl {

	private static final BLogger LOG = BLoggerFactory.getLogger(JtaEntityManagerFactoryImpl.class);

	private static final String[] TRANSACTION_MANAGERS = new String[] { //
	"java:appserver/TransactionManager", //
		"java:/TransactionManager",//
		"java:jboss/TransactionManager", //
		"java:comp/pm/TransactionManager", //
		"TransactionManager", //
		"java:appserver/TransactionManager" };

	private final TransactionManager transactionManager;

	/**
	 * @param name
	 *            the name of the entity manager factory
	 * @param parser
	 *            the persistence parser
	 * 
	 * @since $version
	 */
	public JtaEntityManagerFactoryImpl(String name, PersistenceParser parser) {
		super(name, parser);

		this.transactionManager = this.lookupTransactionManager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerImpl createEntityManager() {
		this.assertOpen();

		return new JtaEntityManagerImpl(this, this.getMetamodel(), this.getDatasource(), Collections.<String, Object> emptyMap(), this.getJdbcAdaptor());
	}

	/**
	 * Returns the transaction manager.
	 * 
	 * @return the transaction manager
	 * 
	 * @since $version
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	private TransactionManager lookupTransactionManager() {
		for (final String jndiName : JtaEntityManagerFactoryImpl.TRANSACTION_MANAGERS) {
			final TransactionManager manager = this.lookupTransactionManager(jndiName);
			if (manager != null) {
				JtaEntityManagerFactoryImpl.LOG.info("Using JTA Transaction manager: {0}", jndiName);
				return manager;
			}
		}

		throw new PersistenceException("Unable to locate the transa ction manager");
	}

	private TransactionManager lookupTransactionManager(String jndiName) {
		try {
			JtaEntityManagerFactoryImpl.LOG.debug("Trying JTA Transaction Manager: {0}", jndiName);

			return (TransactionManager) new InitialContext().lookup(jndiName);
		}
		catch (final NamingException e) {}

		return null;
	}
}

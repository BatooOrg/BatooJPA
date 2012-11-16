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
package org.batoo.jpa.community.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.jdbc.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author hceylan
 * @since 2.0.0
 */
public abstract class BaseCoreTest { // extends BaseTest {

	private static final BLogger LOG = BLoggerFactory.getLogger(BaseCoreTest.class);

	private static final String DEFAULT = "default";

	/**
	 * Rule to get Persistence XML File name.
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	@Rule
	public final TestWatcher watchman = new TestWatcher() {

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void starting(Description description) {
			BaseCoreTest.this.persistenceUnitName = BaseCoreTest.DEFAULT;

			final PersistenceContext persistenceContext = description.getAnnotation(PersistenceContext.class);
			if (persistenceContext != null) {
				// if unit name is not the default set the unit name
				if (StringUtils.isNotBlank(persistenceContext.unitName())) {
					BaseCoreTest.this.persistenceUnitName = persistenceContext.unitName();
				}
			}
		}
	};

	private String persistenceUnitName;
	private EntityManagerFactoryImpl emf;
	private ClassLoader oldContextClassLoader;
	private EntityManagerImpl em;
	private EntityTransaction tx;

	/**
	 * Begins transaction.
	 * 
	 * @since 2.0.0
	 */
	protected void begin() {
		this.tx().begin();
	}

	private void cleanUpEm() {
		if ((this.em != null) && this.em.isOpen()) {
			try {
				this.em.close();
			}
			catch (final Exception e) {}
			this.em = null;
		}
	}

	private void cleanupTx() {
		if ((this.tx != null) && this.tx.isActive()) {
			try {
				this.tx.rollback();
			}
			catch (final Exception e) {}

			this.tx = null;
		}
	}

	/**
	 * Closes the Entity Manager.
	 * 
	 * @since 2.0.0
	 */
	protected void close() {
		this.cleanupTx();

		this.cleanUpEm();
	}

	/**
	 * 
	 * Commits the transaction.
	 * 
	 * @since 2.0.0
	 */
	protected void commit() {
		this.tx().commit();

		this.tx = null;
	}

	/**
	 * Returns if entity is contained in the session.
	 * 
	 * @param entity
	 * @return true if entity is contained in the session
	 * 
	 * @since 2.0.0
	 */
	protected boolean contains(Object entity) {
		return this.em().contains(entity);
	}

	/**
	 * Creates and returns a JPQL query.
	 * 
	 * @param qlString
	 *            the query string
	 * @param resultClass
	 *            the result class
	 * @return the query
	 * 
	 * @since 2.0.0
	 */
	protected Query cq(String qlString) {
		return this.em().createQuery(qlString);
	}

	/**
	 * Creates and returns a JPQL query.
	 * 
	 * @param qlString
	 *            the query string
	 * @param resultClass
	 *            the result class
	 * @param <T>
	 *            the result ype
	 * @return the query
	 * 
	 * @since 2.0.0
	 */
	protected <T> TypedQuery<T> cq(String qlString, Class<T> resultClass) {
		return this.em().createQuery(qlString, resultClass);
	}

	/**
	 * Creates and returns a JPQL update query.
	 * 
	 * @param qlString
	 *            the query string
	 * @return the query
	 * 
	 * @since 2.0.0
	 */
	protected Query cu(String qlString) {
		return this.em().createQuery(qlString);
	}

	/**
	 * Detaches the entity.
	 * 
	 * @param entity
	 * 
	 * @since 2.0.0
	 */
	protected void detach(Object entity) {
		this.em().detach(entity);
	}

	/**
	 * Returns the Entity Manager
	 * 
	 * @return the Entity Manager
	 * 
	 * @since 2.0.0
	 */
	protected EntityManagerImpl em() {
		if (this.em != null) {
			return this.em;
		}

		return this.em = this.emf().createEntityManager();
	}

	/**
	 * Returns the Entity Manager Factory.
	 * 
	 * @return the Entity Manager Factory
	 * @since 2.0.0
	 */
	protected EntityManagerFactoryImpl emf() {
		if (this.emf == null) {
			this.emf = this.setupEmf();
		}

		return this.emf;
	}

	/**
	 * @since 2.0.0
	 */
	private void ensureTx() {
		if ((this.tx == null) || !this.tx.isActive()) {
			this.begin();
		}
	}

	private void exec(String cmd) {
		try {
			final Process process = Runtime.getRuntime().exec(cmd);
			if (process.waitFor() != 0) {
				BaseCoreTest.LOG.error("Command failed: " + process.exitValue());
				IOUtils.copy(process.getErrorStream(), System.err);
			}

			IOUtils.copy(process.getErrorStream(), System.out);
		}
		catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds and returns the entity.
	 * 
	 * @param cls
	 *            the class of the entity
	 * @param pk
	 *            the primary key of the entity
	 * @param <T>
	 *            the type of entity
	 * @return entity or null
	 * 
	 * @since 2.0.0
	 */
	protected <T> T find(Class<T> cls, Object pk) {
		return this.find(cls, pk, null);
	}

	/**
	 * Finds and returns the entity.
	 * 
	 * @param cls
	 *            the class of the entity
	 * @param pk
	 *            the primary key of the entity
	 * @param <T>
	 *            the type of entity
	 * @param lockMode
	 * @return entity or null
	 * 
	 * @since 2.0.0
	 */
	protected <T> T find(Class<T> cls, Object pk, LockModeType lockMode) {
		return this.em().find(cls, pk, lockMode);
	}

	/**
	 * Flushes the Entity Manager
	 * 
	 * @since 2.0.0
	 */
	protected void flush() {
		this.em().flush();
	}

	/**
	 * @return the name of the persistence unit name
	 * 
	 * @since 2.0.0
	 */
	protected String getPersistenceUnitName() {
		return this.getRootPackage();
	}

	private String getRootPackage() {
		return this.getClass().getPackage().getName();
	}

	/**
	 * If the entity manager factory will be created by the test itself.
	 * <p>
	 * Useful for mapping tests.
	 * 
	 * @return false to lazy setup the entity manager factory
	 * 
	 * @since 2.0.0
	 */
	protected boolean lazySetup() {
		return false;
	}

	/**
	 * Merges the entity.
	 * 
	 * @param entity
	 * @param <T>
	 *            the type of the entity
	 * 
	 * @since 2.0.0
	 * @return
	 */
	protected <T> T merge(T entity) {
		this.ensureTx();

		return this.em().merge(entity);
	}

	/**
	 * Persists the entity.
	 * 
	 * @param entity
	 * 
	 * @since 2.0.0
	 */
	protected void persist(Object entity) {
		this.ensureTx();

		this.em().persist(entity);
	}

	/**
	 * Returns a reference to the entity.
	 * 
	 * @param cls
	 *            the class of the entity
	 * @param pk
	 *            the primary key of the entity
	 * @param <T>
	 *            the type of entity
	 * @return entity or null
	 * 
	 * @since 2.0.0
	 */
	protected <T> T reference(Class<T> cls, Object pk) {
		return this.em().getReference(cls, pk);
	}

	/**
	 * Persists the entity.
	 * 
	 * @param entity
	 * 
	 * @since 2.0.0
	 */
	protected void refresh(Object entity) {
		this.em().refresh(entity);
	}

	/**
	 * Persists the entity.
	 * 
	 * @param entity
	 * 
	 * @since 2.0.0
	 */
	protected void remove(Object entity) {
		this.ensureTx();

		this.em().remove(entity);
	}

	/**
	 * Rolls the transaction back.
	 * 
	 * @since 2.0.0
	 */
	protected void rollback() {
		this.tx().rollback();

		this.tx = null;
	}

	/**
	 * Builds the session factory.
	 * 
	 * @throws SQLException
	 *             th
	 * 
	 * @since 2.0.0
	 */
	@Before
	public void setup() throws SQLException {
		if (System.getProperty("testMode") == null) {
			System.setProperty("testMode", "hsql");
			System.setProperty("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
			System.setProperty("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:test");
			System.setProperty("javax.persistence.jdbc.user", "sa");
			System.setProperty("javax.persistence.jdbc.password", "");
		}

		final String testMode = System.getProperty("testMode");

		if ("mssql".equals(testMode)) {
			final String username = System.getProperty("javax.persistence.jdbc.user");
			final String password = System.getProperty("javax.persistence.jdbc.password");
			final Connection connection = DriverManager.getConnection(System.getProperty("javax.persistence.jdbc.url"), username, password);

			try {
				final QueryRunner qr = new QueryRunner(true, false);
				qr.update(connection, "use master");
				qr.update(connection, "drop database test");
				qr.update(connection, "create database test");
			}
			finally {
				connection.close();
			}
		}

		if ("oracle".equals(testMode)) {
			final String username = System.getProperty("javax.persistence.jdbc.user");
			final String password = System.getProperty("javax.persistence.jdbc.password");
			final Connection connection = DriverManager.getConnection(System.getProperty("javax.persistence.jdbc.url"), username, password);

			try {
				final QueryRunner qr = new QueryRunner(true, false);
				final List<Object[]> tables = qr.query(connection, "select TABLE_NAME from user_tables", new ArrayListHandler());
				for (final Object[] table : tables) {
					try {
						qr.update(connection, "DROP TABLE " + table[0] + " CASCADE CONSTRAINTS");
					}
					catch (final Exception e) {}
				}
			}
			finally {
				connection.close();
			}

		}

		if (!this.lazySetup()) {
			this.emf = this.setupEmf();
		}
	}

	/**
	 * Sets up the entity manager factory.
	 * 
	 * @return the entity manager factory
	 * 
	 * @since 2.0.0
	 */
	protected EntityManagerFactoryImpl setupEmf() {
		return this.setupEmf(this.persistenceUnitName);
	}

	/**
	 * Sets up the entity manager factory.
	 * 
	 * @param puName
	 *            the persistence unit name
	 * @return the entity manager factory
	 * 
	 * @since 2.0.0
	 */
	protected EntityManagerFactoryImpl setupEmf(String puName) {
		final Thread currentThread = Thread.currentThread();

		if (this.oldContextClassLoader != null) {
			currentThread.setContextClassLoader(this.oldContextClassLoader);
		}

		this.oldContextClassLoader = currentThread.getContextClassLoader();

		final TestClassLoader cl = new TestClassLoader(this.oldContextClassLoader);
		currentThread.setContextClassLoader(cl);
		cl.setRoot(this.getRootPackage());

		return (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory(puName);
	}

	/**
	 * Cleans up the test
	 * 
	 * @throws SQLException
	 *             thrown if the tear down fails
	 * 
	 * @since 2.0.0
	 */
	@After
	public void teardown() throws SQLException {
		final String testMode = System.getProperty("testMode");

		this.cleanupTx();

		final QueryRunner qr = "mssql".equals(testMode) ? new QueryRunner(true, false) : new QueryRunner();

		if (this.emf != null) {
			if ("mysql".equals(testMode)) {
				final EntityManagerImpl em = this.em();

				qr.update(em.getConnection(), "drop database test");
				qr.update(em.getConnection(), "create database test");
			}
			else if ("hsql".equals(testMode)) {
				final EntityManagerImpl em = this.em();

				qr.update(em.getConnection(), "shutdown");
			}
		}

		this.cleanUpEm();

		if ((this.emf != null) && this.emf.isOpen()) {
			try {
				this.emf.close();
			}
			catch (final Exception e) {}
			this.emf = null;
		}

		if ("mssql".equals(testMode)) {
			final String username = System.getProperty("javax.persistence.jdbc.user");
			final String password = System.getProperty("javax.persistence.jdbc.password");
			final Connection connection = DriverManager.getConnection(System.getProperty("javax.persistence.jdbc.url"), username, password);
			try {
				qr.update(connection, "use master");
				qr.update(connection, "drop database test");
				qr.update(connection, "create database test");
			}
			finally {
				connection.close();
			}
		}

		if (StringUtils.isBlank(testMode) || "derby".equals(testMode)) {
			try {
				DriverManager.getConnection("jdbc:derby:memory:test;drop=true");
			}
			catch (final Exception e) {}
		}
		else if ("pgsql".equals(testMode)) {
			this.exec("/usr/bin/dropdb test -U postgres -h localhost");
			this.exec("/usr/bin/createdb test -U postgres -h localhost");
		}

		Thread.currentThread().setContextClassLoader(this.oldContextClassLoader);
		this.oldContextClassLoader = null;
	}

	/**
	 * Returns the active transaction from the entity manager.
	 * 
	 * @return the active transaction
	 * 
	 * @since 2.0.0
	 */
	protected EntityTransaction tx() {
		if (this.tx != null) {
			return this.tx;
		}

		return this.tx = this.em().getTransaction();
	}
}

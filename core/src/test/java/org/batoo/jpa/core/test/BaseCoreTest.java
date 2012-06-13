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
package org.batoo.jpa.core.test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.test.BaseTest;
import org.batoo.jpa.core.BJPASettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.google.common.collect.Maps;

/**
 * @author hceylan
 * 
 * @since $version
 */
public abstract class BaseCoreTest extends BaseTest {

	private static final String DEFAULT = "default";

	/**
	 * Rule to get Persistence XML File name.
	 * 
	 * @since $version
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
	private EntityManagerFactory emf;
	private ClassLoader oldContextClassLoader;
	private EntityManager em;
	private EntityTransaction tx;

	/**
	 * Begins transaction.
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	protected void close() {
		this.cleanupTx();

		this.cleanUpEm();
	}

	/**
	 * 
	 * Commits the transaction.
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	protected boolean contains(Object entity) {
		return this.em().contains(entity);
	}

	/**
	 * Detaches the entity.
	 * 
	 * @param entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void detach(Object entity) {
		this.em().detach(entity);
	}

	/**
	 * Returns the Entity Manager
	 * 
	 * @return the Entity Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected EntityManager em() {
		if (this.em != null) {
			return this.em;
		}

		return this.em = this.emf().createEntityManager();
	}

	/**
	 * Returns the Entity Manager Factory.
	 * 
	 * @return the Entity Manager Factory
	 * @since $version
	 */
	public EntityManagerFactory emf() {
		if (this.emf == null) {
			this.setupEmf();
		}

		return this.emf;
	}

	/**
	 * @since $version
	 * @author hceylan
	 */
	private void ensureTx() {
		if ((this.tx == null) || !this.tx.isActive()) {
			this.begin();
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
	 * @since $version
	 * @author hceylan
	 */
	protected <T> T find(Class<T> cls, Object pk) {
		return this.em().find(cls, pk);
	}

	/**
	 * Flushes the Entity Manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void flush() {
		this.em().flush();
	}

	/**
	 * @return the name of the persistence unit name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getPersistenceUnitName() {
		return this.getRootPackage();
	}

	/**
	 * @return the persistence unit properties override
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected HashMap<Object, Object> getProperties() {
		final HashMap<Object, Object> properties = Maps.newHashMap();

		properties.put(BJPASettings.ROOT_PACKAGE, this.getRootPackage());
		properties.put(BJPASettings.CLASS_LOADER_CLASS, TestClassLoader.class.getCanonicalName());

		return properties;
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
	 * @since $version
	 * @author hceylan
	 */
	protected boolean lazySetup() {
		return false;
	}

	/**
	 * Persists the entity.
	 * 
	 * @param entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void persist(Object entity) {
		this.ensureTx();

		this.em().persist(entity);
	}

	/**
	 * Persists the entity.
	 * 
	 * @param entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void refresh(Object entity) {
		this.em().refresh(entity);
	}

	/**
	 * Rolls the transaction back.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void rollback() {
		this.tx().rollback();

		this.tx = null;
	}

	/**
	 * Builds the session factory.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void setup() {
		if (!this.lazySetup()) {
			this.setupEmf();
		}
	}

	private void setupEmf() {
		final Thread currentThread = Thread.currentThread();
		this.oldContextClassLoader = currentThread.getContextClassLoader();

		final TestClassLoader cl = new TestClassLoader(this.oldContextClassLoader);
		currentThread.setContextClassLoader(cl);
		cl.setRoot(this.getRootPackage());

		this.emf = Persistence.createEntityManagerFactory(this.persistenceUnitName);

		Assert.assertNotNull("EntityManagerFactory is null", this.emf);
	}

	/**
	 * Cleans up the test
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@After
	public void teardown() {
		this.cleanupTx();

		this.cleanUpEm();

		if ((this.emf != null) && this.emf.isOpen()) {
			try {
				this.emf.close();
			}
			catch (final Exception e) {}
			this.emf = null;
		}

		try {
			DriverManager.getConnection("jdbc:derby:memory:test;drop=true");
		}
		catch (final SQLException e) {}

		Thread.currentThread().setContextClassLoader(this.oldContextClassLoader);
		this.oldContextClassLoader = null;
	}

	/**
	 * Returns the active transaction from the entity manager.
	 * 
	 * @return the active transaction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected EntityTransaction tx() {
		if (this.tx != null) {
			return this.tx;
		}

		return this.tx = this.em().getTransaction();
	}
}

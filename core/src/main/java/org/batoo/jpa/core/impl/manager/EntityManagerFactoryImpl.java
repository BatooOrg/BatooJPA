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
import java.util.Collections;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.JPASettings;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.jpql.JpqlQuery;
import org.batoo.jpa.core.impl.deployment.DdlManager;
import org.batoo.jpa.core.impl.deployment.LinkManager;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.PersistenceParser;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Implementation of {@link EntityManagerFactory}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;
	private final JdbcAdaptor jdbcAdaptor;
	private final Map<String, Object> properties;
	private boolean open;
	private final CriteriaBuilderImpl criteriaBuilder;

	LoadingCache<String, JpqlQuery> graphs = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, JpqlQuery>() {
		@Override
		public JpqlQuery load(String jpql) {
			return new JpqlQuery(EntityManagerFactoryImpl.this, jpql);
		}
	});

	/**
	 * @param name
	 *            the name of the entity manager factory
	 * @param parser
	 *            the persistence parser
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerFactoryImpl(String name, PersistenceParser parser) {
		super();

		this.jdbcAdaptor = this.createJdbcAdaptor(parser);
		this.datasource = this.createDatasource(parser);
		this.metamodel = new MetamodelImpl(this, this.jdbcAdaptor, parser.getMetadata());
		this.properties = parser.getProperties();

		this.metamodel.performSequencesDdl(this.datasource, DDLMode.DROP);
		this.metamodel.performTableGeneratorsDdl(this.datasource, DDLMode.DROP);

		LinkManager.perform(this.metamodel);
		DdlManager.perform(this.datasource, this.metamodel, DDLMode.DROP);

		this.metamodel.preFillGenerators(this.datasource);
		this.criteriaBuilder = new CriteriaBuilderImpl(this.metamodel);

		this.open = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addNamedQuery(String name, Query query) {
		// TODO Auto-generated method stub

	}

	private void assertOpen() {
		if (!this.open) {
			throw new IllegalStateException("EntityManagerFactory has been previously closed");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() {
		this.assertOpen();

		this.metamodel.stopIdGenerators();
		this.datasource.close();

		this.open = false;
	}

	private DataSourceImpl createDatasource(PersistenceParser parser) {
		final String jdbcUrl = (String) parser.getProperty(JPASettings.JDBC_URL);
		final String jdbcUser = (String) parser.getProperty(JPASettings.JDBC_USER);
		final String jdbcPassword = (String) parser.getProperty(JPASettings.JDBC_PASSWORD);

		try {
			return new DataSourceImpl(jdbcUrl, jdbcUser, jdbcPassword);
		}
		catch (final SQLException e) {
			throw new BatooException("Datasource cannot be created", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerImpl createEntityManager() {
		this.assertOpen();

		return new EntityManagerImpl(this, this.metamodel, this.datasource, Collections.<String, Object> emptyMap(), this.jdbcAdaptor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManager createEntityManager(Map<String, Object> map) {
		this.assertOpen();

		return new EntityManagerImpl(this, this.metamodel, this.datasource, map, this.jdbcAdaptor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map<String, Object> map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates the JDBC adaptor
	 * 
	 * @param parser
	 *            the persistence parser
	 * @return the JDBC Adaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private JdbcAdaptor createJdbcAdaptor(PersistenceParser parser) {
		final boolean scanExternal = Boolean.valueOf((String) parser.getProperty(BJPASettings.SCAN_EXTERNAL_JDBC_DRIVERS));
		final String jdbcDriver = (String) parser.getProperty(JPASettings.JDBC_DRIVER);

		return AbstractJdbcAdaptor.getAdapter(scanExternal, jdbcDriver);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Cache getCache() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaBuilderImpl getCriteriaBuilder() {
		this.assertOpen();

		return this.criteriaBuilder;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a lazy created {@link JpqlQuery} for the query.
	 * 
	 * @param qlString
	 *            the JPQL query string
	 * @return the Jpql Query object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JpqlQuery getpqlQuery(String qlString) {
		try {
			return this.graphs.get(qlString);
		}
		catch (final Exception e) {
			if (e.getCause() instanceof PersistenceException) {
				throw (PersistenceException) e.getCause();
			}

			if (e.getCause() instanceof IllegalArgumentException) {
				throw (IllegalArgumentException) e.getCause();
			}

			throw new PersistenceException("Cannot parse query: " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getProperties() {
		return this.properties;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> cls) {
		return (T) this;
	}
}

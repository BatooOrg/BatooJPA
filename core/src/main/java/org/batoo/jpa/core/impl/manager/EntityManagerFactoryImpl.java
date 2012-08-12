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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.JPASettings;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.jpql.JpqlQuery;
import org.batoo.jpa.core.impl.deployment.DdlManager;
import org.batoo.jpa.core.impl.deployment.LinkManager;
import org.batoo.jpa.core.impl.deployment.NamedQueriesManager;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.PersistenceParser;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

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
	private final Map<String, Object> properties = Maps.newHashMap();
	private final Map<String, JpqlQuery> namedQueries = Maps.newHashMap();
	private final CriteriaBuilderImpl criteriaBuilder;
	private final PersistenceUnitUtilImpl persistenceUtil;

	LoadingCache<String, JpqlQuery> graphs = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, JpqlQuery>() {
		@Override
		public JpqlQuery load(String jpql) {
			return new JpqlQuery(EntityManagerFactoryImpl.this, jpql);
		}
	});

	private boolean open;

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

		this.prepareProperties(parser);

		this.jdbcAdaptor = this.createJdbcAdaptor(parser);
		this.datasource = this.createDatasource(parser);
		this.metamodel = new MetamodelImpl(this, this.jdbcAdaptor, parser.getMetadata());

		this.metamodel.performSequencesDdl(this.datasource, DDLMode.DROP);
		this.metamodel.performTableGeneratorsDdl(this.datasource, DDLMode.DROP);

		LinkManager.perform(this.metamodel);
		DdlManager.perform(this.datasource, this.metamodel, DDLMode.DROP);

		this.metamodel.preFillGenerators(this.datasource);
		this.criteriaBuilder = new CriteriaBuilderImpl(this.metamodel);

		NamedQueriesManager.perform(this.metamodel, this.criteriaBuilder);

		this.persistenceUtil = new PersistenceUnitUtilImpl(this);

		this.open = true;
	}

	/**
	 * Adds the named query to the entity manager factory.
	 * 
	 * @param name
	 *            the name of the query
	 * @param jpqlQuery
	 *            the compiled query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addNamedQuery(String name, JpqlQuery jpqlQuery) {
		if (this.namedQueries.containsKey(name)) {
			throw new IllegalArgumentException("A named query with the same name already exists: " + name);
		}

		this.namedQueries.put(name, jpqlQuery);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addNamedQuery(final String name, Query query) {
		final QueryImpl<?> typedQuery = (QueryImpl<?>) query;
		final String jpql = typedQuery.getCriteriaQuery().getJpql();

		new JpqlQuery(this, this.criteriaBuilder, new NamedQueryMetadata() {

			@Override
			public Map<String, Object> getHints() {
				return typedQuery.getHints();
			}

			@Override
			public AbstractLocator getLocator() {
				return null;
			}

			@Override
			public LockModeType getLockMode() {
				return typedQuery.getLockMode();
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getQuery() {
				return jpql;
			}
		});
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
		final String jdbcUrl = (String) this.getProperty(JPASettings.JDBC_URL);
		final String jdbcUser = (String) this.getProperty(JPASettings.JDBC_USER);
		final String jdbcPassword = (String) this.getProperty(JPASettings.JDBC_PASSWORD);

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
		final boolean scanExternal = Boolean.valueOf((String) this.getProperty(BJPASettings.SCAN_EXTERNAL_JDBC_DRIVERS));
		final String jdbcDriver = (String) this.getProperty(JPASettings.JDBC_DRIVER);

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
	 * Returns a lazy created {@link JpqlQuery} for the query.
	 * 
	 * @param qlString
	 *            the JPQL query string
	 * @return the Jpql Query object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JpqlQuery getJpqlQuery(String qlString) {
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
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * Returns the named query with the name <code>name</code>.
	 * 
	 * @param name
	 *            the name of the query
	 * @return the named query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JpqlQuery getNamedQuery(String name) {
		return this.namedQueries.get(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return this.persistenceUtil;
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
	 * Returns the persistence property.
	 * 
	 * @param key
	 *            the key for the property
	 * @return the value of the property or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return this.open;
	}

	private void prepareProperties(PersistenceParser parser) {
		final Enumeration<?> e = System.getProperties().propertyNames();

		while (e.hasMoreElements()) {
			final Object key = e.nextElement();
			if (key instanceof String) {
				this.properties.put((String) key, System.getProperties().get(key));
			}
		}

		this.properties.putAll(parser.getProperties());
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

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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.transaction.TransactionManager;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.JPASettings;
import org.batoo.jpa.core.impl.cache.CacheImpl;
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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link EntityManagerFactory}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

	private static final BLogger LOG = BLoggerFactory.getLogger(EntityManagerFactoryImpl.class);

	private static final String[] TRANSACTION_MANAGERS = new String[] { //
	"java:appserver/TransactionManager", //
		"java:/TransactionManager" };

	private static final int NO_QUERIES_MAX = 1000;
	private static final int NO_QUERIES_TRIM = 100;

	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;
	private final TransactionManager transactionManager;
	private final CacheImpl cache;
	private final boolean jta;

	private final JdbcAdaptor jdbcAdaptor;
	private final Map<String, Object> properties = Maps.newHashMap();
	private final Map<String, JpqlQuery> namedQueries = Maps.newHashMap();
	private final CriteriaBuilderImpl criteriaBuilder;
	private final PersistenceUnitUtilImpl persistenceUtil;

	private final HashMap<String, JpqlQuery> jpqlCache = Maps.newHashMap();
	private final ClassLoader classloader;

	private final ValidatorFactory validationFactory;
	private final Class<?>[] persistValidators;
	private final Class<?>[] updateValidators;
	private final Class<?>[] removeValidators;

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

		this.classloader = parser.getClassloader();
		this.prepareProperties(parser);

		final boolean hasValidators = parser.hasValidators();
		if (hasValidators) {
			this.validationFactory = Validation.buildDefaultValidatorFactory();

			this.persistValidators = this.getValidatorsFor(parser, JPASettings.PERSIST_VALIDATION_GROUP);
			this.updateValidators = this.getValidatorsFor(parser, JPASettings.UPDATE_VALIDATION_GROUP);
			this.removeValidators = this.getValidatorsFor(parser, JPASettings.REMOVE_VALIDATION_GROUP);
		}
		else {
			this.validationFactory = null;
			this.persistValidators = null;
			this.updateValidators = null;
			this.removeValidators = null;
		}

		this.jta = StringUtils.isNotBlank(parser.getJtaDatasource());
		this.datasource = this.createDatasource(parser);
		this.transactionManager = this.lookupTransactionManager();
		this.cache = new CacheImpl(this.metamodel, parser.getSharedCacheMode());

		this.jdbcAdaptor = this.createJdbcAdaptor();
		this.metamodel = new MetamodelImpl(this, this.jdbcAdaptor, parser.getMetadata());

		LinkManager.perform(this.metamodel);

		this.metamodel.dropAllTables(this.datasource);
		DdlManager.perform(this.datasource, this.metamodel, DDLMode.DROP);

		this.metamodel.performSequencesDdl(this.datasource, DDLMode.DROP);
		this.metamodel.performTableGeneratorsDdl(this.datasource, DDLMode.DROP);

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
	 * Adds the query to the named queries.
	 * 
	 * @param name
	 *            the name fo the query
	 * @param query
	 *            the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
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
		if (StringUtils.isNotBlank(parser.getJtaDatasource())) {
			return new DataSourceImpl(parser.getJtaDatasource());
		}

		if (StringUtils.isNotBlank(parser.getNonJtaDatasource())) {
			return new DataSourceImpl(parser.getNonJtaDatasource());
		}

		final String jdbcDriver = (String) this.getProperty(JPASettings.JDBC_DRIVER);
		final String jdbcUrl = (String) this.getProperty(JPASettings.JDBC_URL);
		final String jdbcUser = (String) this.getProperty(JPASettings.JDBC_USER);
		final String jdbcPassword = (String) this.getProperty(JPASettings.JDBC_PASSWORD);

		try {
			this.classloader.loadClass(jdbcDriver).newInstance();

			return new DataSourceImpl(jdbcUrl, jdbcUser, jdbcPassword);
		}
		catch (final Exception e) {
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
	 * Creates the JDBC adaptor
	 * 
	 * @return the JDBC Adaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private JdbcAdaptor createJdbcAdaptor() {
		try {
			final Connection connection = this.datasource.getConnection();
			try {
				return AbstractJdbcAdaptor.getAdapter(this.classloader, connection.getMetaData().getDatabaseProductName());
			}
			finally {
				connection.close();
			}
		}
		catch (final SQLException e) {
			throw new BatooException("Unable to get connection from the datasource", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CacheImpl getCache() {
		return this.cache;
	}

	/**
	 * Returns the classloader of the entity manager factory.
	 * 
	 * @return the classloader of the entity manager factory
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ClassLoader getClassloader() {
		return this.classloader;
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
			EntityManagerFactoryImpl.LOG.debug("JPQL: {0}", qlString);

			JpqlQuery jpqlQuery = this.jpqlCache.get(qlString);
			if (jpqlQuery == null) {
				jpqlQuery = new JpqlQuery(EntityManagerFactoryImpl.this, qlString);

				// clean up job
				if (this.jpqlCache.size() == EntityManagerFactoryImpl.NO_QUERIES_MAX) {
					synchronized (this) {
						if (this.jpqlCache.size() == EntityManagerFactoryImpl.NO_QUERIES_MAX) {
							final JpqlQuery[] queries = Lists.newArrayList(this.jpqlCache.values()).toArray(new JpqlQuery[this.jpqlCache.size()]);
							Arrays.sort(queries, new Comparator<JpqlQuery>() {

								@Override
								public int compare(JpqlQuery o1, JpqlQuery o2) {
									if (o1.getLastUsed() > o2.getLastUsed()) {
										return 1;
									}

									return -1;
								}
							});

							for (int i = 0; i < EntityManagerFactoryImpl.NO_QUERIES_TRIM; i++) {
								this.jpqlCache.remove(queries[i].getQueryString());
							}
						}
					}
				}

				this.jpqlCache.put(qlString, jpqlQuery);
			}
			return jpqlQuery;
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
	 * Returns the set of persist validators.
	 * 
	 * @return the set of persist validators.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?>[] getPersistValidators() {
		return this.persistValidators;
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
	 * Returns the set of remove validators.
	 * 
	 * @return the set of remove validators.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?>[] getRemoveValidators() {
		return this.removeValidators;
	}

	/**
	 * Returns the transaction manager.
	 * 
	 * @return the transaction manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	/**
	 * Returns the set of update validators.
	 * 
	 * @return the set of update validators.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?>[] getUpdateValidators() {
		return this.updateValidators;
	}

	/**
	 * Returns the validation factory.
	 * 
	 * @return the validation factory.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ValidatorFactory getValidationFactory() {
		return this.validationFactory;
	}

	private Class<?>[] getValidatorsFor(PersistenceParser parser, String group) {

		final String groups = (String) parser.getProperties().get(group);
		if (StringUtils.isBlank(groups)) {
			return new Class[] { Default.class };
		}

		final Set<Class<?>> validationGroups = Sets.newHashSet();
		final Iterator<String> i = Splitter.on(",").trimResults().split(groups).iterator();
		while (i.hasNext()) {
			final String className = i.next();
			try {
				validationGroups.add(this.classloader.loadClass(className));
			}
			catch (final ClassNotFoundException e) {
				throw new PersistenceException("Cannot load class for validation group: " + className);
			}
		}

		return validationGroups.toArray(new Class[validationGroups.size()]);
	}

	/**
	 * Returns if the persistence unit has validators
	 * 
	 * @return true if the persistence unit has validators, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasValidators() {
		return this.validationFactory != null;
	}

	/**
	 * Returns the jta of the EntityManagerFactoryImpl.
	 * 
	 * @return the jta of the EntityManagerFactoryImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isJta() {
		return this.jta;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return this.open;
	}

	private TransactionManager lookupTransactionManager() {
		if (!this.jta) {
			return null;
		}

		for (final String jndiName : EntityManagerFactoryImpl.TRANSACTION_MANAGERS) {
			final TransactionManager manager = this.lookupTransactionManager(jndiName);
			if (manager != null) {
				EntityManagerFactoryImpl.LOG.info("Using JTA Transaction manager: {0}", jndiName);
				return manager;
			}
		}

		throw new PersistenceException("Unable to locate the transa ction manager");
	}

	private TransactionManager lookupTransactionManager(String jndiName) {
		try {
			EntityManagerFactoryImpl.LOG.debug("Trying JTA Transaction Manager: {0}", jndiName);

			return (TransactionManager) new InitialContext().lookup(jndiName);
		}
		catch (final NamingException e) {}

		return null;
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

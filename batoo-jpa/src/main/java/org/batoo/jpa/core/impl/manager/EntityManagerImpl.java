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
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaDeleteImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaUpdateImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.jpql.JpqlQuery;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMappingImpl;
import org.batoo.jpa.core.impl.nativeQuery.NativeQuery;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link EntityManager}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityManagerImpl implements EntityManager {

	private static final BLogger LOG = BLoggerFactory.getLogger(EntityManagerImpl.class);

	private final EntityManagerFactoryImpl emf;
	private final MetamodelImpl metamodel;
	private final CriteriaBuilderImpl criteriaBuilder;
	private final DataSource datasource;
	private final JdbcAdaptor jdbcAdaptor;
	private final Map<String, Object> properties;
	private final SessionImpl session;

	private boolean open;

	private Connection connection;
	private EntityTransactionImpl transaction;
	private boolean rollbackOnly;

	private FlushModeType flushMode;
	private boolean inFlush;

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
	 * 
	 * @since 2.0.0
	 */
	public EntityManagerImpl(EntityManagerFactoryImpl entityManagerFactory, MetamodelImpl metamodel, DataSource datasource, Map<String, Object> properties,
		JdbcAdaptor jdbcAdaptor) {
		super();

		this.emf = entityManagerFactory;
		this.metamodel = metamodel;
		this.datasource = datasource;
		this.jdbcAdaptor = jdbcAdaptor;
		this.session = new SessionImpl(this, metamodel);
		this.criteriaBuilder = this.emf.getCriteriaBuilder();

		this.properties = properties;
		this.flushMode = FlushModeType.AUTO;

		this.open = true;
	}

	/**
	 * Checks if the entity manager is open.
	 * 
	 * @since 2.0.0
	 */
	protected void assertOpen() {
		if (!this.open) {
			throw new IllegalStateException("EntityManager has been previously closed");
		}

		// if (this.rollbackOnly) {
		// throw new IllegalStateException("EntityManager previously encountered an exception.");
		// }
	}

	/**
	 * Asserts that a transaction is active.
	 * 
	 * @since 2.0.0
	 */
	public void assertTransaction() {
		this.assertOpen();

		if ((this.transaction == null) || !this.transaction.isActive()) {
			throw new TransactionRequiredException("No active transaction");
		}
	}

	/**
	 * Cascades the merge operation
	 * 
	 * @param type
	 *            the entity type
	 * @param entity
	 *            the entity
	 * @param requiresFlush
	 *            if an implicit flush is required
	 * @param processed
	 *            registry of processed entities
	 * @param instances
	 *            the persisted instances
	 * @param <T>
	 *            the type of the entity
	 * 
	 * @since 2.0.0
	 */
	public <T> void cascadeMerge(EntityTypeImpl<T> type, T entity, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed,
		LinkedList<ManagedInstance<?>> instances) {
		for (final AssociationMappingImpl<?, ?, ?> association : type.getAssociations()) {

			// if the association is a plural association
			if (association instanceof PluralAssociationMappingImpl) {
				final PluralAssociationMappingImpl<?, ?, ?> mapping = (PluralAssociationMappingImpl<?, ?, ?>) association;

				Collection<?> children;
				if (mapping.getAttribute().getCollectionType() == CollectionType.MAP) {
					final Map<?, ?> map = (Map<?, ?>) mapping.get(entity);
					children = map.values();
				}
				else {
					// get the children
					children = (Collection<?>) mapping.get(entity);

				}

				if (children != null) {
					// iterate over children and merge them all
					if (children instanceof List) {
						final List<?> childrenList = (List<?>) children;
						for (int i = 0; i < childrenList.size(); i++) {
							this.mergeImpl(childrenList.get(i), requiresFlush, processed, instances, association.cascadesMerge());
						}
					}
					else {
						for (final Object child : children) {
							this.mergeImpl(child, requiresFlush, processed, instances, association.cascadesMerge());
						}
					}
				}
			}
			// if the association is a singular association
			else {
				final Object associate = association.get(entity);

				// merge the entity
				this.mergeImpl(associate, requiresFlush, processed, instances, association.cascadesMerge());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.assertOpen();

		if ((this.transaction != null) && this.transaction.isActive()) {
			this.transaction.rollback();

			EntityManagerImpl.LOG.warn("Session cleared with active and transaction. Updated persistent types will become stale...");
		}

		this.session.clear();
	}

	/**
	 * Clears the transaction.
	 * 
	 * @since 2.0.0
	 */
	public void clearTransaction() {
		this.transaction = null;

		this.detachAllIfNotTransactionScoped();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() {
		this.assertOpen();

		if ((this.transaction != null) && this.transaction.isActive()) {
			this.transaction.rollback();

			EntityManagerImpl.LOG.warn("Entity manager closed with an active transaction. Updated persistent types will become stale...");
		}

		this.closeConnection();

		this.open = false;
	}

	/**
	 * Closes the database connection.
	 * 
	 * @since 2.0.0
	 */
	protected void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			}
			catch (final SQLException e) {}
		}

		this.connection = null;
	}

	/**
	 * Closes the connection if the connections is obtained from JTA managed datasource.
	 * 
	 * @since 2.0.0
	 */
	public void closeConnectionIfNecessary() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object entity) {
		this.assertOpen();

		final ManagedInstance<Object> instance = this.session.get(entity);

		return (instance != null) && (instance.getInstance() == entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNamedQuery(String name) {
		return this.createNamedQuery(name, Object.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		final JpqlQuery query = this.emf.getNamedQuery(name);

		if (query == null) {
			throw new IllegalArgumentException("No named query found with the name: " + name);
		}

		return query.createTypedQuery(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString) {
		return new NativeQuery(this, sqlString);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString, Class<?> resultClass) {
		return new NativeQuery(this, sqlString, resultClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		throw new NotImplementedException("Native queries are not yet implemented");
	}

	/**
	 * Creates and returns the query for delete operation.
	 * 
	 * @param deleteQuery
	 *            the delete criteria query
	 * @return the query for delete operation
	 * 
	 * @since $version
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Query createQuery(CriteriaDeleteImpl<?> deleteQuery) {
		return new QueryImpl(deleteQuery, this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> QueryImpl<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return new QueryImpl<T>((CriteriaQueryImpl<T>) criteriaQuery, this);
	}

	/**
	 * Creates and returns a query for the update operation.
	 * 
	 * @param updateQuery
	 *            the upate criteria query
	 * @return query for the update operation
	 * 
	 * @since $version
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Query createQuery(CriteriaUpdateImpl<?> updateQuery) {
		return new QueryImpl(updateQuery, this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createQuery(String qlString) {
		return this.emf.getJpqlQuery(qlString).createTypedQuery(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return this.emf.getJpqlQuery(qlString).<T> createTypedQuery(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void detach(Object entity) {
		this.assertOpen();

		final ManagedInstance<?> instance = this.session.remove(entity);
		if (instance != null) {
			instance.cascadeDetach(this);
		}
	}

	/**
	 * Detaches all the entities if there is no transaction in progress.
	 * 
	 * @since $version
	 */
	public void detachAllIfNotTransactionScoped() {
		if ((this.transaction == null) || !this.transaction.isActive()) {
			this.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return this.find(entityClass, primaryKey, null, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		return this.find(entityClass, primaryKey, lockMode, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		if (primaryKey == null) {
			throw new NullPointerException();
		}

		// try to locate in the session
		final EntityTypeImpl<T> type = this.metamodel.entity(entityClass);

		return this.findImpl(primaryKey, lockMode, properties, type);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		return this.find(entityClass, primaryKey, null, properties);
	}

	private <T> T findImpl(Object primaryKey, LockModeType lockMode, Map<String, Object> properties, final EntityTypeImpl<T> type) {
		this.session.setLoadTracker();

		try {
			final ManagedInstance<? extends T> instance = this.session.get(new ManagedId<T>(primaryKey, type));
			if (instance != null) {
				if (instance.getInstance() instanceof EnhancedInstance) {
					final EnhancedInstance enhanced = (EnhancedInstance) instance.getInstance();
					if (enhanced.__enhanced__$$__isInitialized()) {

						this.lock(instance, lockMode, properties);

						return instance.getInstance();
					}
				}
				else {
					if (instance.getStatus() == Status.REMOVED) {
						return null;
					}

					this.lock(instance, lockMode, properties);

					return instance.getInstance();
				}
			}

			try {
				return type.performSelect(this, primaryKey, lockMode);
			}
			catch (final NoResultException e) {
				return null;
			}
		}
		finally {
			this.session.releaseLoadTracker();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush() {
		if (this.inFlush) {
			return;
		}

		this.assertTransaction();

		this.inFlush = true;

		try {
			this.session.handleExternals();

			final ManagedInstance<?>[] instances = this.session.handleAdditions();
			this.session.cascadeRemovals(instances);
			this.session.handleOrphans(instances);

			this.session.flush(this.getConnection());
		}
		catch (final SQLException e) {
			EntityManagerImpl.LOG.error(e, "Flush failed");

			throw new PersistenceException("Flush failed", e);
		}
		catch (final ConstraintViolationException e) {
			EntityManagerImpl.LOG.debug(e, "Flush failed due to validation errors:\n\t" + Joiner.on("\n\t").join(e.getConstraintViolations()));

			throw e;
		}
		catch (final RuntimeException e) {
			EntityManagerImpl.LOG.error(e, "Flush failed");

			throw e;
		}
		finally {
			this.inFlush = false;
		}
	}

	/**
	 * Returns the active connection.
	 * 
	 * @return the connection
	 * 
	 * @since 2.0.0
	 */
	public Connection getConnection() {
		// if the connection exists then simply return it
		if (this.connection != null) {
			return this.connection;
		}

		try {
			this.joinTransaction();

			// create a new connection and return it
			return this.connection = this.datasource.getConnection();
		}
		catch (final SQLException e) {
			throw new PersistenceException("Unable to obtain connection from the datasource", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaBuilderImpl getCriteriaBuilder() {
		return this.criteriaBuilder;
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
		return this.flushMode;
	}

	/**
	 * Returns the JDBC adaptor.
	 * 
	 * @return the JDBC adaptor
	 * 
	 * @since 2.0.0
	 */
	public JdbcAdaptor getJdbcAdaptor() {
		return this.jdbcAdaptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode(Object entity) {
		final ManagedInstance<Object> instance = this.session.get(entity);

		return instance.getLockMode();
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
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		if (primaryKey == null) {
			throw new NullPointerException();
		}

		// try to locate in the session
		final EntityTypeImpl<T> type = this.metamodel.entity(entityClass);
		final ManagedId<T> managedId = new ManagedId<T>(primaryKey, type);

		// try to locate in the session
		ManagedInstance<? extends T> instance = this.session.get(managedId);
		if (instance != null) {
			return instance.getInstance();
		}

		// create a lazy instance
		instance = type.getManagedInstanceById(this.session, managedId, true);
		this.session.put(instance);

		// and return it
		return instance.getInstance();
	}

	/**
	 * Returns the session.
	 * 
	 * @return the session
	 * 
	 * @since 2.0.0
	 */
	public SessionImpl getSession() {
		return this.session;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTransactionImpl getTransaction() {
		// if the transaction exists simply return it
		if (this.transaction != null) {
			return this.transaction;
		}

		this.assertOpen();

		// create the new transaction and return it
		return this.transaction = new EntityTransactionImpl(this, this.getConnection());
	}

	/**
	 * Returns if the entity manager has an active transaction.
	 * 
	 * @return true if the entity manager has an active transaction, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean hasActiveTransaction() {
		return (this.transaction != null) && this.transaction.isActive();
	}

	/**
	 * Returns if the entity manager has a transaction which is marked for rollback.
	 * 
	 * @return true if the entity manager has a transaction which is marked for rollback, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean hasTransactionMarkedForRollback() {
		if (this.transaction != null) {
			return this.transaction.getRollbackOnly();
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isJoinedToTransaction() {
		return false;
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
	 * Check if the transaction is valid and belongs to this entity manager.
	 * 
	 * @param transaction
	 *            the transaction to test the validity
	 * 
	 * @since 2.0.0
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
	}

	/**
	 * Locks the entity.
	 * 
	 * @param instance
	 *            the managed instance
	 * @param lockMode
	 *            the lock mode
	 * @param properties
	 *            the properties
	 * 
	 * @since 2.0.0
	 */
	public void lock(ManagedInstance<?> instance, LockModeType lockMode, Map<String, Object> properties) {
		// check optimistic lock is supported
		if ((lockMode == LockModeType.OPTIMISTIC) || (lockMode == LockModeType.OPTIMISTIC_FORCE_INCREMENT)) {
			final EntityTypeImpl<?> type = instance.getType().getRootType();
			if (!type.hasVersionAttribute()) {
				throw new PersistenceException("OPTIMISTIC and OPTIMISTIC_FORCE_INCREMENT not supported on non-versioned entity "
					+ instance.getType().getName());
			}

			try {
				if (lockMode == LockModeType.OPTIMISTIC_FORCE_INCREMENT) {
					this.assertTransaction();

					instance.incrementVersion(this.getConnection(), true);
				}
				else {
					instance.incrementVersion(this.getConnection(), false);
				}
			}
			catch (final SQLException e) {
				throw new PersistenceException("Unabled to update the version", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void lock(Object entity, LockModeType lockMode) {
		this.lock(entity, lockMode, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		this.lock(this.session.get(entity), lockMode, properties);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T merge(T entity) {
		final MutableBoolean requiresFlush = new MutableBoolean(false);

		final LinkedList<ManagedInstance<?>> persistedInstances = Lists.newLinkedList();

		final T mergedEntity = this.mergeImpl(entity, requiresFlush, Maps.<Object, Object> newIdentityHashMap(), persistedInstances, true);

		if (requiresFlush.booleanValue()) {
			this.flush();
		}

		for (final ManagedInstance<?> instance : persistedInstances) {
			instance.fireCallbacks(EntityListenerType.PRE_PERSIST);
		}

		return mergedEntity;
	}

	/**
	 * Cascaded implementation of {@link #merge(Object)}.
	 * <p>
	 * Also manages a direct or indirect requirement to an implicit flush.
	 * 
	 * @param entity
	 *            the entity to cascade
	 * @param requiresFlush
	 *            if an implicit flush is required
	 * @param processed
	 *            registry of processed entities
	 * @param instances
	 *            the persisted instances
	 * @param cascade
	 *            cascades the merge operation
	 * @param <T>
	 *            the type of the entity
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> T mergeImpl(T entity, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed, LinkedList<ManagedInstance<?>> instances,
		boolean cascade) {
		if (entity == null) {
			return null;
		}

		// if already processed just return
		final T processedEntity = (T) processed.get(entity);
		if (processedEntity != null) {
			return processedEntity;
		}

		// try to locate the instance in the session
		ManagedInstance<T> instance = this.session.get(entity);

		Class<?> clazz = entity.getClass();
		if (entity instanceof EnhancedInstance) {
			clazz = clazz.getSuperclass();
		}

		final EntityTypeImpl<T> type = (EntityTypeImpl<T>) this.metamodel.entity(clazz);

		// if it is in the session then test its status
		if (instance != null) {
			// if it is a removed entity then throw
			if (instance.getStatus() == Status.REMOVED) {
				throw new IllegalArgumentException("Entity has been previously removed");
			}

			// if it is an existing instance then merge and return
			if ((instance.getStatus() == Status.MANAGED) || (instance.getStatus() == Status.NEW)) {
				processed.put(entity, instance.getInstance());
				if (instance.getStatus() == Status.NEW) {
					instances.add(instance);
				}

				if (instance.getInstance() != entity) {
					instance.mergeWith(this, entity, requiresFlush, processed, instances);
				}
				else {
					this.cascadeMerge(type, entity, requiresFlush, processed, instances);
				}

				return instance.getInstance();
			}
		}

		// get the id of the entity
		final Object id = type.getInstanceId(entity);

		// if it has an id try to locate instance in the database
		if (id != null) {
			T existingEntity = null;
			try {
				existingEntity = this.find((Class<T>) clazz, id);
			}
			catch (final NoResultException e) {}

			// if it is found in the database then merge and return
			if (existingEntity != null) {
				instance = (ManagedInstance<T>) ((EnhancedInstance) existingEntity).__enhanced__$$__getManagedInstance();

				processed.put(entity, instance.getInstance());

				instance.mergeWith(this, entity, requiresFlush, processed, instances);

				return instance.getInstance();
			}
		}

		// it is a new instance, create a new instance and merge with it
		final ManagedId<T> managedId = new ManagedId<T>(id, type);
		instance = type.getManagedInstanceById(this.session, managedId, false);

		instance.setStatus(Status.NEW);

		instance.enhanceCollections();

		this.session.putExternal(instance);
		processed.put(entity, instance.getInstance());
		instances.add(instance);

		instance.mergeWith(this, entity, requiresFlush, processed, instances);

		if (!instance.fillIdValues()) {
			requiresFlush.setValue(true);
		}

		return instance.getInstance();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void persist(Object entity) {
		this.assertTransaction();

		final LinkedList<ManagedInstance<?>> persistedInstances = Lists.newLinkedList();

		if (this.persistImpl(entity, Lists.newArrayList(), persistedInstances)) {
			this.flush();
		}

		for (final ManagedInstance<?> instance : persistedInstances) {
			instance.fireCallbacks(EntityListenerType.PRE_PERSIST);
		}
	}

	/**
	 * Cascaded implementation of {@link #persist(Object)}.
	 * <p>
	 * Also manages a direct or indirect requirement to an implicit flush.
	 * 
	 * @param entity
	 *            the entity to cascade
	 * @param processed
	 *            registry of processed entities
	 * @param instances
	 *            the managed instances
	 * @param <T>
	 *            the type of the entity
	 * @return true if an implicit flush is required, false otherwise
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public <T> boolean persistImpl(T entity, ArrayList<Object> processed, LinkedList<ManagedInstance<?>> instances) {
		if (processed.contains(entity)) {
			return false;
		}

		if (entity instanceof EnhancedInstance) {
			final ManagedInstance<T> instance = (ManagedInstance<T>) ((EnhancedInstance) entity).__enhanced__$$__getManagedInstance();
			if (instance.getStatus() == Status.DETACHED) {
				throw new EntityExistsException("Entity has been previously detached");
			}
		}

		final ManagedInstance<T> existing = this.session.get(entity);
		if (existing != null) {
			processed.add(entity);
			instances.add(existing);

			switch (existing.getStatus()) {
				case REMOVED:
					existing.setStatus(Status.MANAGED);

					return existing.cascadePersist(this, processed, instances);
				case NEW:
				case MANAGED:
					return existing.cascadePersist(this, processed, instances);
				case DETACHED:
					// noop
			}
		}

		final EntityTypeImpl<T> type = (EntityTypeImpl<T>) this.metamodel.entity(entity.getClass());
		final ManagedInstance<T> instance = type.getManagedInstance(this.session, entity);

		instance.setStatus(Status.NEW);

		instance.enhanceCollections();

		boolean requiresFlush = !instance.fillIdValues();
		this.session.putExternal(instance);

		processed.add(entity);
		instances.add(instance);

		requiresFlush |= instance.cascadePersist(this, processed, instances);

		return requiresFlush;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity) {
		this.refresh(entity, LockModeType.NONE, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		this.refresh(entity, lockMode, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		this.assertOpen();

		this.refreshImpl(entity, lockMode, Sets.newHashSet());

		this.closeConnectionIfNecessary();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		this.refresh(entity, LockModeType.NONE, properties);
	}

	/**
	 * Recursive refresh implementation.
	 * 
	 * @param entity
	 *            the entity to refresh
	 * @param lockMode
	 *            the lock mode
	 * @param processed
	 *            registry of processed entities
	 * @return the managed instance
	 * 
	 * @since 2.0.0
	 */
	public ManagedInstance<?> refreshImpl(Object entity, LockModeType lockMode, Set<Object> processed) {
		if (entity == null) {
			return null;
		}

		// if already processed just return
		if (processed.contains(entity)) {
			return null;
		}

		if (entity instanceof EnhancedInstance) {
			final ManagedInstance<?> instance = ((EnhancedInstance) entity).__enhanced__$$__getManagedInstance();

			if ((instance.getSession() == this.session) && (instance.getStatus() == Status.MANAGED)) {
				instance.refresh(this, this.getConnection(), lockMode, processed);

				processed.add(instance);

				return instance;
			}
		}

		throw new IllegalArgumentException("entity is not managed");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void remove(Object entity) {
		this.assertOpen();

		final LinkedList<ManagedInstance<?>> removedInstances = Lists.newLinkedList();

		this.removeImpl(entity, Lists.newArrayList(), removedInstances);

		for (final ManagedInstance<?> instance : removedInstances) {
			instance.fireCallbacks(EntityListenerType.PRE_REMOVE);
		}
	}

	/**
	 * Cascaded implementation of {@link #remove(Object)}.
	 * 
	 * @param entity
	 *            the entity to cascade
	 * @param processed
	 *            registry of processed entities
	 * @param instances
	 *            the managed instances
	 * 
	 * @since 2.0.0
	 */
	public void removeImpl(Object entity, ArrayList<Object> processed, LinkedList<ManagedInstance<?>> instances) {
		if ((processed != null) && processed.contains(entity)) {
			return;
		}

		if (entity instanceof EnhancedInstance) {
			final EnhancedInstance enhancedInstance = (EnhancedInstance) entity;
			final ManagedInstance<?> instance = enhancedInstance.__enhanced__$$__getManagedInstance();
			if ((instance != null) && (instance.getStatus() == Status.DETACHED)) {
				throw new IllegalArgumentException("Entity has been previously detached");
			}
		}

		final ManagedInstance<Object> instance = this.session.get(entity);
		if (instance != null) {
			if (instance.getStatus() == Status.MANAGED) {
				instance.setStatus(Status.REMOVED);
				this.session.setChanged(instance);

				if (processed != null) {
					processed.add(entity);
					instances.add(instance);
				}

				instance.cascadeRemove(this, processed, instances);
			}
			else if (instance.getStatus() == Status.NEW) {
				this.session.remove(instance.getInstance());
				instance.setStatus(Status.DETACHED);

				if (processed != null) {
					processed.add(entity);
					instances.add(instance);
				}

				instance.cascadeRemove(this, processed, instances);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFlushMode(FlushModeType flushMode) {
		this.flushMode = flushMode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setProperty(String propertyName, Object value) {
		this.properties.put(propertyName, value);
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	public void setRollbackOnly() {
		this.rollbackOnly = true;

		if (this.transaction != null) {
			this.transaction.setRollbackOnly();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> clazz) {
		if (clazz == DataSource.class) {
			return (T) this.datasource;
		}

		if (clazz == Connection.class) {
			return (T) this.connection;
		}

		return null;
	}
}

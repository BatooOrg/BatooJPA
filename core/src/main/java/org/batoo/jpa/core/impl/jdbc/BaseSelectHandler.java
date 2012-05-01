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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.OperationTookLongTimeWarning;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.mapping.OwnerAssociationMapping;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.PluralAttributeImpl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * @param <X>
 * @author hceylan
 * @since $version
 */
public abstract class BaseSelectHandler<X> implements ResultSetHandler<Collection<X>> {

	private static final BLogger LOG = BLogger.getLogger(SelectHandler.class);
	private static volatile int nextOperationNo = 0;
	protected final SessionImpl session;
	protected final EntityTypeImpl<X> rootType;
	protected final Map<PhysicalColumn, String>[] columnAliases;
	protected final QueryItem root;

	private boolean dbAudit;

	/**
	 * @param session
	 *            the active session
	 * @param rootType
	 *            the root type
	 * @param columnAliases
	 *            the aliases for the columns
	 * @param tableAliases
	 *            the aliases for all the tables
	 * @param root
	 *            the root query item
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseSelectHandler(SessionImpl session, EntityTypeImpl<X> rootType, Map<PhysicalColumn, String>[] columnAliases, QueryItem root) {

		this.session = session;
		this.rootType = rootType;
		this.columnAliases = columnAliases;
		this.root = root;
	}

	private void createLazyAssociation(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, int tableNo,
		final ManagedInstance<?> managedInstance, Association<?, ?> association) throws SQLException {

		// is the association a collection
		final AttributeImpl<?, ?> attribute = association.getDeclaringAttribute();
		if (attribute.isCollection()) {
			((PluralAttributeImpl<?, ?, ?>) attribute).newInstance(managedInstance, true);
		}
		else if (association instanceof OwnerAssociationMapping) {
			this.createLazyInstance(session, rs, cache, tableNo, managedInstance, association);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createLazyInstance(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, int tableNo,
		final ManagedInstance<?> managedInstance, Association<?, ?> association) throws SQLException {
		final OwnerAssociationMapping<?, ?> lazyAssociation = (OwnerAssociationMapping<?, ?>) association;
		final EntityTypeImpl<?> type = lazyAssociation.getType();

		// get the primary key and create the instance
		final Object primaryKey = this.getPrimaryKey(rs, tableNo, lazyAssociation);

		// if the primary key is null then no lazy instance created
		if (primaryKey != null) {
			final ManagedId<?> managedId = type.newManagedId(session, primaryKey, true);

			// look for it in the cache
			ManagedInstance<?> instance = cache.get(managedId);
			if (instance == null) { // if found reuse the cached instance
				// get it from the session
				instance = session.get(managedId);
				if (instance != null) { // if found in the session, put it to cache and reuse
					cache.put(instance.getId(), instance);
				}
				else {
					// create a lazy instance
					instance = new ManagedInstance(type, session, managedId);
					session.put(instance);
					cache.put(instance.getId(), instance);
				}
			}

			lazyAssociation.setValue(managedInstance.getInstance(), instance.getInstance());
		}
	}

	private Object getColumnValue(ResultSet rs, int depth, final PhysicalColumn column) throws SQLException {
		return rs.getObject(this.columnAliases[depth].get(column));
	}

	/**
	 * Returns the managed instance.
	 * <p>
	 * The managed instance is first searched in the query cache. If found then returned.
	 * <p>
	 * Then the managed instance is searched in the session. If found then returned.
	 * <p>
	 * Finally a new managed instance is created, put into session and query cache and returned.
	 * 
	 * @param session
	 *            the session
	 * @param cache
	 *            the query cache
	 * @param type
	 *            the type of the managed instance
	 * @param primaryKey
	 *            the primary key of the instance
	 * @param rs
	 *            the result set
	 * @param tableNo
	 *            the table no
	 * @return the managed instance, located or created
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ManagedInstance<?> getManagedInstance(SessionImpl session, Map<ManagedId<?>, ManagedInstance<?>> cache, EntityTypeImpl<?> type,
		final Object primaryKey, ResultSet rs, int tableNo) throws SQLException {
		final ManagedId<?> managedId = type.newManagedId(session, primaryKey, false);

		// look for it in the cache
		ManagedInstance<?> instance = cache.get(managedId);
		if (instance != null) { // if found return and reuse the cached instance
			return instance;
		}

		// get it from the session
		instance = session.get(managedId);
		if (instance != null) { // if found in the session
			cache.put(instance.getId(), instance);

			final Object entity = instance.getInstance();
			if (entity instanceof EnhancedInstance) {
				if (!((EnhancedInstance) entity).__enhanced__$$__isInitialized()) {
					this.loadInstance(type, rs, tableNo, instance);
				}
			}

			return instance;
		}

		// new managed instance
		instance = new ManagedInstance(type, session, managedId);

		session.put(instance);
		cache.put(managedId, instance);

		this.loadInstance(type, rs, tableNo, instance);

		return instance;
	}

	/**
	 * Extracts and returns the primary key for the type from the result set.
	 * 
	 * @param rs
	 *            the result set
	 * @param tableNo
	 *            the current depth
	 * @param type
	 *            the entity type
	 * @return the primary key
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <T> Object getPrimaryKey(ResultSet rs, int tableNo, EntityTypeImpl<T> type) throws SQLException {
		final EntityTable primaryTable = type.getPrimaryTable();

		Object primaryKeyValue = null;
		if (type.getTopType().hasSingleIdAttribute()) {

			if (type.getIdJavaType() != null) {
				final EmbeddableTypeImpl<?> embeddable = (EmbeddableTypeImpl<?>) type.getIdType();
				primaryKeyValue = embeddable.newInstance();

				for (final PhysicalColumn column : type.getPrimaryTable().getPrimaryKeys()) {
					final Object value = this.getColumnValue(rs, tableNo, column);
					column.getMapping().getDeclaringAttribute().set(primaryKeyValue, value);
				}
			}
			else {
				final PhysicalColumn primaryKey = primaryTable.getPrimaryKey();
				primaryKeyValue = this.getColumnValue(rs, tableNo, primaryKey);
			}

		}
		else {
			try {
				primaryKeyValue = type.getIdJavaType().newInstance();
			}
			catch (final Exception e) {} // not possible

			for (final PhysicalColumn column : primaryTable.getPrimaryKeys()) {
				final Object value = this.getColumnValue(rs, tableNo, column);
				column.getMapping().getDeclaringAttribute().set(primaryKeyValue, value);
			}
		}

		return primaryKeyValue;
	}

	/**
	 * Extracts and returns the primary key for a lazy association.
	 * 
	 * @param rs
	 *            the reault set
	 * @param depth
	 *            the depth
	 * @param lazyAssociation
	 *            the lazy association
	 * @return the primary key for the lazy association
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Object getPrimaryKey(ResultSet rs, int depth, OwnerAssociationMapping<?, ?> lazyAssociation) throws SQLException {
		final EntityTypeImpl<?> type = lazyAssociation.getType();

		Object primaryKeyValue = null;
		if (type.getTopType().hasSingleIdAttribute()) {

			if (type.getIdJavaType() != null) {
				final EmbeddableTypeImpl<?> embeddable = (EmbeddableTypeImpl<?>) type.getIdType();
				primaryKeyValue = embeddable.newInstance();

				for (final PhysicalColumn column : lazyAssociation.getPhysicalColumns()) {
					final Object value = this.getColumnValue(rs, depth, column);
					column.getMapping().getDeclaringAttribute().set(primaryKeyValue, value);
				}
			}
			else {
				final PhysicalColumn primaryKey = lazyAssociation.getPhysicalColumns()[0];
				primaryKeyValue = this.getColumnValue(rs, depth, primaryKey);
			}

		}
		else {
			try {
				primaryKeyValue = type.getIdJavaType().newInstance();
			}
			catch (final Exception e) {} // not possible

			for (final PhysicalColumn column : lazyAssociation.getPhysicalColumns()) {
				final Object value = this.getColumnValue(rs, depth, column);
				column.getMapping().getDeclaringAttribute().set(primaryKeyValue, value);
			}
		}

		return primaryKeyValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<X> handle(ResultSet rs) throws SQLException {
		if (!this.dbAudit) {
			return this.handle0(rs);
		}

		final int operationNo = nextOperationNo++;

		LOG.debug("{0}: handle()", operationNo);

		final long start = System.currentTimeMillis();

		try {
			return this.handle0(rs);
		}
		finally {
			final long time = System.currentTimeMillis() - start;
			if (time > DataSourceImpl.MAX_WAIT) {
				LOG.warn(new OperationTookLongTimeWarning(), "{0}: {1} msecs, handle()", operationNo, time);
			}
			else {
				LOG.debug("{0}: {1} msecs, handle()", operationNo, time);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<X> handle0(ResultSet rs) throws SQLException {
		final Map<ManagedId<?>, ManagedInstance<?>> cache = Maps.newHashMap();
		final Map<ManagedId<? super X>, ManagedInstance<X>> instances = Maps.newHashMap();

		while (rs.next()) {
			final ManagedInstance<X> managedInstance = (ManagedInstance<X>) this.processRow(this.session, rs, cache, this.root, null);
			managedInstance.setStatus(Status.MANAGED);
			instances.put(managedInstance.getId(), managedInstance);
		}

		for (final ManagedInstance<?> instance : cache.values()) {
			if (!instance.isLoaded()) {
				instance.setStatus(Status.MANAGED);
			}
		}

		final Collection<ManagedInstance<X>> handle0 = instances.values();
		return Collections2.transform(handle0, new Function<ManagedInstance<X>, X>() {

			@Override
			public X apply(ManagedInstance<X> input) {
				return input.getInstance();
			}
		});
	}

	private void loadInstance(EntityTypeImpl<?> type, ResultSet rs, int tableNo, ManagedInstance<?> instance) throws SQLException {
		for (final PhysicalColumn column : type.getBasicColumns()) {
			final Object columnValue = this.getColumnValue(rs, tableNo, column);
			column.getMapping().setValue(instance.getInstance(), columnValue);
		}
	}

	/**
	 * @param session
	 *            the active session
	 * @param rs
	 *            the result set
	 * @param cache
	 *            the cache of objects created
	 * @param queryItem
	 *            the current query item
	 * @param parent
	 *            the parent object
	 * @return the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ManagedInstance<?> processRow(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache,
		QueryItem queryItem, Object parent) throws SQLException {

		final EntityTypeImpl<?> currentType = queryItem.getType();

		ManagedInstance<?> managedInstance = null;

		final Object primaryKey = this.getPrimaryKey(rs, queryItem.getTableNo(), currentType);
		if (primaryKey != null) {
			managedInstance = this.getManagedInstance(session, cache, currentType, primaryKey, rs, queryItem.getTableNo());

			if (managedInstance != null) {
				for (final QueryItem childItem : queryItem.getChildren()) {
					final Association<?, ?> association = childItem.getAssociation();
					switch (childItem.getItemType()) {
						case INVERSE:
							if (association instanceof CollectionMapping) {
								managedInstance.enhanceCollection((CollectionMapping<?, ?, ?>) association);
							}
							association.setValue(managedInstance.getInstance(), parent);
							break;
						case LAZY:
							this.createLazyAssociation(session, rs, cache, queryItem.getTableNo(), managedInstance,
								childItem.getAssociation());
							break;
						case EAGER:
							final ManagedInstance<?> child = this.processRow(session, rs, cache, childItem, managedInstance.getInstance());
							if (child != null) {
								if (association instanceof CollectionMapping) {
									managedInstance.enhanceCollection((CollectionMapping<?, ?, ?>) association);
								}
								association.setValue(managedInstance.getInstance(), child.getInstance());
							}
					}
				}
			}

		}

		return managedInstance;
	}

	/**
	 * Subclasses must implement to provide the behavior
	 * 
	 * @param managedInstance
	 * @return true if the instance should be handled
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract boolean shouldHandle(final ManagedInstance<?> managedInstance);

}

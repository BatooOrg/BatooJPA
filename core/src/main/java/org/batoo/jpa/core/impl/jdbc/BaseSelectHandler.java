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
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.OperationTookLongTimeWarning;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.mapping.Mapping;
import org.batoo.jpa.core.impl.mapping.Mapping.AssociationType;
import org.batoo.jpa.core.impl.mapping.OwnerAssociationMapping;
import org.batoo.jpa.core.impl.types.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.util.Path;

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
	protected final List<Path> entityPaths;
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
	 * @param entityPaths
	 *            the entity path
	 * @param lazyPaths
	 *            the inverse paths
	 * @param lazyPaths
	 *            the lazy paths
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseSelectHandler(SessionImpl session, EntityTypeImpl<X> rootType, Map<PhysicalColumn, String>[] columnAliases,
		List<Path> entityPaths) {

		this.session = session;
		this.rootType = rootType;
		this.columnAliases = columnAliases;
		this.entityPaths = entityPaths;
	}

	private void createLazyInstance(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, int depth,
		final ManagedInstance<?> managedInstance, Deque<Association<?, ?>> path) throws SQLException {
		final Association<?, ?> association = path.getLast();
		if (association instanceof OwnerAssociationMapping) {
			final OwnerAssociationMapping<?, ?> lazyAssociation = (OwnerAssociationMapping<?, ?>) association;

			for (final PhysicalColumn column : lazyAssociation.getPhysicalColumns()) {
				final Object value = this.getColumnValue(rs, depth, column);
				if (value != null) {
					final ManagedInstance<?> lazyInstance = this.createManagedInstance(session, cache, lazyAssociation.getType(), value,
						true);
					lazyAssociation.setValue(managedInstance.getInstance(), lazyInstance.getInstance());
				}
			}
		}
		else if (association instanceof CollectionMapping) {
			final CollectionMapping<?, ?, ?> lazyAssociation = (CollectionMapping<?, ?, ?>) association;
			final ManagedCollection<?> collection = lazyAssociation.getDeclaringAttribute().newInstance(session, managedInstance);
			lazyAssociation.setCollection(managedInstance.getInstance(), collection);
		}
	}

	/**
	 * @param session
	 *            the active session
	 * @param cache
	 *            the cache
	 * @param type
	 *            the entity type
	 * @param primaryKeyValue
	 *            the primary key value
	 * @return the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	private <T> ManagedInstance<? super T> createManagedInstance(SessionImpl session, Map<ManagedId<?>, ManagedInstance<?>> cache,
		EntityTypeImpl<T> type, final Object primaryKeyValue, boolean lazy) {
		if (primaryKeyValue == null) {
			return null;
		}

		final ManagedId<T> managedId = type.getManagedId(session, primaryKeyValue);

		// look for it in the cache
		final ManagedInstance<? super T> cached = (ManagedInstance<? super T>) cache.get(managedId);
		if (cached != null) { // if found return and reuse the cached instance
			return cached;
		}

		// get it from the session
		final ManagedInstance<? super T> managed = session.get(managedId);
		if (managed != null) { // if found in the session
			cache.put(managed.getId(), managed);
			return managed;
		}
		else { // new managed instances
			final ManagedInstance<? super T> newInstance = type.newInstanceWithId(session, managedId, lazy);
			newInstance.setLoaded(false);

			session.put(newInstance);
			cache.put(managedId, newInstance);

			return newInstance;
		}
	}

	/**
	 * @param session
	 *            the active session
	 * @param rs
	 *            the resultset
	 * @param depth
	 *            the current depth
	 * @param currentType
	 *            the current entity type
	 * @param cache
	 *            the cache
	 * @return the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <T> ManagedInstance<? super T> createManagedInstance(SessionImpl session, ResultSet rs,
		Map<ManagedId<?>, ManagedInstance<?>> cache, int depth, EntityTypeImpl<T> currentType) throws SQLException {
		final EntityTable primaryTable = currentType.getPrimaryTable();

		if (currentType.getTopType().hasSingleIdAttribute()) {

			Object primaryKeyValue;

			if (currentType.getIdJavaType() != null) {
				final EmbeddableTypeImpl<?> embeddable = (EmbeddableTypeImpl<?>) currentType.getIdType();
				primaryKeyValue = embeddable.newInstance();

				for (final PhysicalColumn column : currentType.getPrimaryTable().getPrimaryKeys()) {
					final Object value = this.getColumnValue(rs, depth, column);
					column.getMapping().getDeclaringAttribute().set(primaryKeyValue, value);
				}
			}
			else {
				final PhysicalColumn primaryKey = primaryTable.getPrimaryKey();
				primaryKeyValue = this.getColumnValue(rs, depth, primaryKey);
			}

			return this.createManagedInstance(session, cache, currentType, primaryKeyValue, false);
		}
		else {
			Object id = null;
			try {
				id = currentType.getIdJavaType().newInstance();
			}
			catch (final Exception e) {
				// noop;
			}

			for (final PhysicalColumn column : primaryTable.getPrimaryKeys()) {
				final Object value = this.getColumnValue(rs, depth, column);
				column.getMapping().getDeclaringAttribute().set(id, value);
			}

			return this.createManagedInstance(session, cache, currentType, id, false);
		}
	}

	private Object getColumnValue(ResultSet rs, int depth, final PhysicalColumn column) throws SQLException {
		return rs.getObject(this.columnAliases[depth].get(column));
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
		final Map<ManagedInstance<?>, Set<Mapping<?, ?>>> associationsPrepared = Maps.newHashMap();

		final Map<ManagedId<? super X>, ManagedInstance<X>> instances = Maps.newHashMap();
		while (rs.next()) {
			final ManagedInstance<X> managedInstance = (ManagedInstance<X>) this.processRow(this.session, rs, cache, null, 0,
				new MutableInt(0), null, associationsPrepared);
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

	/**
	 * Default implementation does nothing.
	 * 
	 * @param managedInstance
	 *            the managed instance
	 * @param association
	 *            the association
	 * @param associationsPrepared
	 *            the associations that have been prepared
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void prepareAssociation(ManagedInstance<?> managedInstance, Association<?, ?> association,
		Map<ManagedInstance<?>, Set<Mapping<?, ?>>> associationsPrepared) {
		// noop
	}

	/**
	 * @param session
	 *            the active session
	 * @param rs
	 *            the resultset
	 * @param cache
	 *            the cache of objects created
	 * @param root
	 *            the current root
	 * @param depth
	 *            the current depth
	 * @param associationsPrepared
	 *            the associations that have been prepared
	 * @return the managed instance
	 * @param parent
	 *            the parent object
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ManagedInstance<?> processRow(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, Object root,
		int depth, MutableInt associationNo, Object parent, Map<ManagedInstance<?>, Set<Mapping<?, ?>>> associationsPrepared)
		throws SQLException {
		EntityTypeImpl<?> currentType;

		if (depth == 0) {
			currentType = this.rootType;
		}
		else {
			currentType = this.entityPaths.get(associationNo.intValue() - 1).getLast().getType();
		}

		final ManagedInstance<?> managedInstance = this.createManagedInstance(session, rs, cache, depth, currentType);
		if (this.shouldHandle(managedInstance)) {

			managedInstance.setLoaded(false);

			for (final EntityTable table : currentType.getTables().values()) {
				for (final PhysicalColumn column : table.getColumns()) {
					if (column.getMapping().getAssociationType() == AssociationType.BASIC) {
						if (column.isId()) { // primary key values already handled
							continue;
						}

						final Object columnValue = this.getColumnValue(rs, depth, column);
						column.getMapping().setValue(managedInstance.getInstance(), columnValue);
					}
				}
			}
		}

		if (managedInstance == null) {
			return null;
		}

		while (this.entityPaths.size() > associationNo.intValue()) {
			final Path path = this.entityPaths.get(associationNo.intValue());
			final Association<?, ?> association = path.getLast();

			if (association.getOwner() != currentType) {
				break;
			}

			associationNo.increment();

			if (path.isInverse()) { // do inverse set
				if (!managedInstance.isLoaded()) {
					association.setValue(managedInstance.getInstance(), parent);
				}
				continue;
			}
			else if (path.isLazy()) { // do lazy set
				if (!managedInstance.isLoaded()) {
					this.createLazyInstance(session, rs, cache, depth, managedInstance, path);
				}
				continue;
			}

			this.prepareAssociation(managedInstance, association, associationsPrepared);
			final ManagedInstance<?> child = this.processRow(session, rs, cache, root, ++depth, associationNo,
				managedInstance.getInstance(), associationsPrepared);
			if (child != null) {
				association.setValue(managedInstance.getInstance(), child.getInstance());
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

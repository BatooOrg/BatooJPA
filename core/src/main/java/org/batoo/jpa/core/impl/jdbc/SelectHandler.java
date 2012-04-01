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

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.OperationTookLongTimeWarning;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.mapping.OwnedOneToManyMapping;
import org.batoo.jpa.core.impl.mapping.OwnerAssociation;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair2;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * A {@link ResultSetHandler} to load a single result from resultset.
 * 
 * @author hceylan
 * @since $version
 */
public class SelectHandler<X> implements ResultSetHandler<Collection<X>> {

	private static final BLogger LOG = BLogger.getLogger(SelectHandler.class);

	private static volatile int nextOperationNo = 0;

	private final SessionImpl session;

	private final EntityTypeImpl<X> rootType;
	private final Map<Pair2<Integer, PhysicalColumn>, String> columnAliases;
	private final List<Deque<Association<?, ?>>> entityPaths;
	private final List<Deque<Association<?, ?>>> lazyPaths;
	private final List<Deque<Association<?, ?>>> inversePaths;

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
	public SelectHandler(SessionImpl session, EntityTypeImpl<X> rootType, Map<Pair2<Integer, PhysicalColumn>, String> columnAliases,
		List<Deque<Association<?, ?>>> entityPaths, List<Deque<Association<?, ?>>> inversePaths, List<Deque<Association<?, ?>>> lazyPaths) {
		super();

		this.session = session;
		this.rootType = rootType;
		this.columnAliases = columnAliases;
		this.entityPaths = entityPaths;
		this.inversePaths = inversePaths;
		this.lazyPaths = lazyPaths;
	}

	private void createLazyInstance(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, int depth,
		final ManagedInstance<?> managedInstance, Deque<Association<?, ?>> path) throws SQLException {
		final Association<?, ?> association = path.getLast();
		if (association instanceof OwnerAssociation) {
			final OwnerAssociation<?, ?> lazyAssociation = (OwnerAssociation<?, ?>) association;

			for (final PhysicalColumn column : lazyAssociation.getPhysicalColumns().values()) {
				final Object value = this.getColumnValue(rs, depth, column);
				if (value != null) {
					final ManagedInstance<?> lazyInstance = this.createManagedInstance(session, cache, lazyAssociation.getType(), value,
						true);
					lazyInstance.addReference(managedInstance, lazyAssociation);
					lazyAssociation.setValue(managedInstance.getInstance(), lazyInstance.getInstance());
				}
			}
		}
		else if (association instanceof OwnedOneToManyMapping) {
			final CollectionMapping<?, ?, ?> lazyAssociation = (CollectionMapping<?, ?, ?>) association;
			lazyAssociation.setValue(managedInstance.getInstance(),
				lazyAssociation.getDeclaringAttribute().newInstance(session, managedInstance, lazyAssociation));
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

		final ManagedId<? super T> managedId = type.getManagedId(session, primaryKeyValue);

		// look for it in the cache
		final ManagedInstance<? super T> cached = (ManagedInstance<? super T>) cache.get(managedId);
		if (cached != null) { // if found return and reuse the cached instance
			return cached;
		}

		// get it from the session
		final ManagedInstance<? super T> managed = (ManagedInstance<? super T>) session.get(managedId);
		if ((managed != null) && (lazy || (managed.getStatus() != Status.LAZY))) { // if found in the session
			cache.put(managed.getId(), managed);
			return managed;
		}
		else { // new managed instances
			final ManagedInstance<? super T> newInstance = type.newInstanceWithId(session, managedId, lazy);
			newInstance.setStatus(Status.LAZY);

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
		if (currentType.hasSingleIdAttribute()) {
			final EntityTable primaryTable = currentType.getPrimaryTable();
			final PhysicalColumn primaryKey = primaryTable.getPrimaryKeys().iterator().next();
			final Object primaryKeyValue = this.getColumnValue(rs, depth, primaryKey);

			return this.createManagedInstance(session, cache, currentType, primaryKeyValue, false);
		}
		else {
			// TODO handle composite id.
			throw new NotImplementedException();
		}
	}

	private Object getColumnValue(ResultSet rs, int depth, final PhysicalColumn column) throws SQLException {
		final String columnAlias = this.columnAliases.get(Pair2.create(depth, column));
		return rs.getObject(columnAlias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<X> handle(ResultSet rs) throws SQLException {
		final int operationNo = nextOperationNo++;

		LOG.debug("{0}: handle()", operationNo);

		final long start = System.currentTimeMillis();

		try {
			return Collections2.transform(this.handle0(rs), new Function<ManagedInstance<X>, X>() {

				@Override
				public X apply(ManagedInstance<X> input) {
					return input.getInstance();
				}
			});
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
	private Collection<ManagedInstance<X>> handle0(ResultSet rs) throws SQLException {
		final Map<ManagedId<?>, ManagedInstance<?>> cache = Maps.newHashMap();

		final Map<ManagedId<? super X>, ManagedInstance<X>> instances = Maps.newHashMap();
		while (rs.next()) {
			final ManagedInstance<X> managedInstance = (ManagedInstance<X>) this.processRow(this.session, rs, cache, null, 0,
				new MutableInt(0), null);
			managedInstance.setStatus(Status.MANAGED);
			instances.put(managedInstance.getId(), managedInstance);
		}

		for (final ManagedInstance<?> instance : cache.values()) {
			if (instance.getStatus() == Status.LOADING) {
				instance.setStatus(Status.MANAGED);
			}
		}

		return instances.values();
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
	 * @return the managed instance
	 * @param parent
	 *            the parent object
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ManagedInstance<?> processRow(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, Object root,
		int depth, MutableInt associationNo, Object parent) throws SQLException {
		EntityTypeImpl<?> currentType;

		if (depth == 0) {
			currentType = this.rootType;
		}
		else {
			currentType = this.entityPaths.get(associationNo.intValue() - 1).getLast().getType();
		}

		final ManagedInstance<?> managedInstance = this.createManagedInstance(session, rs, cache, depth, currentType);
		if ((managedInstance != null) && (managedInstance.getStatus() == Status.LAZY)) {

			managedInstance.setStatus(Status.LOADING);

			for (final EntityTable table : currentType.getTables().values()) {
				for (final PhysicalColumn column : table.getColumns()) {
					if (column.getMapping() instanceof BasicMapping) {
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
			final Deque<Association<?, ?>> path = this.entityPaths.get(associationNo.intValue());
			if (path.getLast().getOwner() != currentType) {
				break;
			}

			associationNo.increment();

			if (this.inversePaths.contains(path)) { // do inverse set
				if (managedInstance.getStatus() == Status.LOADING) {
					path.getLast().setValue(managedInstance.getInstance(), parent);
				}
				continue;
			}
			else if (this.lazyPaths.contains(path)) { // do lazy set
				if (managedInstance.getStatus() == Status.LOADING) {
					this.createLazyInstance(session, rs, cache, depth, managedInstance, path);
				}
				continue;
			}

			final ManagedInstance<?> child = this.processRow(session, rs, cache, root, ++depth, associationNo,
				managedInstance.getInstance());
			if (child != null) {
				path.getLast().setValue(managedInstance.getInstance(), child.getInstance());
			}
		}

		return managedInstance;
	}
}

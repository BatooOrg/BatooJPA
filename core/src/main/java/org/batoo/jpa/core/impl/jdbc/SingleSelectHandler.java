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
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.OperationTookLongTimeWarning;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance.Status;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;

/**
 * A {@link ResultSetHandler} to load a single result from resultset.
 * 
 * @author hceylan
 * @since $version
 */
public class SingleSelectHandler<X> implements ResultSetHandler<ManagedInstance<X>> {

	private static final BLogger LOG = BLogger.getLogger(SingleSelectHandler.class);

	private static volatile int nextOperationNo = 0;

	private final SessionImpl session;

	private final EntityTypeImpl<X> rootType;
	private final BiMap<String, PhysicalColumn> columnAliases;
	private final List<Deque<Association<?, ?>>> entityPaths;

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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingleSelectHandler(SessionImpl session, EntityTypeImpl<X> rootType, BiMap<String, PhysicalColumn> columnAliases,
		List<Deque<Association<?, ?>>> entityPaths) {
		super();

		this.session = session;
		this.rootType = rootType;
		this.columnAliases = columnAliases;
		this.entityPaths = entityPaths;
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
	 * @return the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 * @param cache
	 */
	private ManagedInstance<?> createManagedInstance(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache,
		int depth, EntityTypeImpl<?> currentType) throws SQLException {
		if (currentType.hasSingleIdAttribute()) {
			final EntityTable primaryTable = currentType.getPrimaryTable();
			final PhysicalColumn primaryKey = primaryTable.getPrimaryKeys().iterator().next();
			final Object primaryKeyValue = this.getColumnValue(rs, primaryKey);

			// Create a model of the instance
			ManagedInstance<?> managedInstance = currentType.newInstanceWithId(session, primaryKeyValue);

			// look for it in the cache
			final ManagedInstance<?> cached = cache.get(managedInstance.getId());
			if (cached != null) { // if found return and reuse the cached instance
				return cached;
			}

			// get it from the session
			final ManagedInstance<?> existing = session.get(managedInstance.getId());
			if (existing != null) {
				managedInstance = existing;
			}
			else {
				session.put(managedInstance);
			}

			// put it into the cache
			cache.put(managedInstance.getId(), managedInstance);

			// finally return it
			return managedInstance;
		}
		else {
			// TODO handle composite id.
			throw new NotImplementedException();
		}
	}

	private Object getColumnValue(ResultSet rs, final PhysicalColumn column) throws SQLException {
		final String columnAlias = this.columnAliases.inverse().get(column);
		return rs.getObject(columnAlias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ManagedInstance<X> handle(ResultSet rs) throws SQLException {
		final int operationNo = nextOperationNo++;

		LOG.debug("{0}: handle()", operationNo);

		final long start = System.currentTimeMillis();

		try {
			final Map<ManagedId<?>, ManagedInstance<?>> cache = Maps.newHashMap();
			ManagedInstance<X> managedInstance = null;
			while (rs.next()) {
				final X root = managedInstance != null ? managedInstance.getInstance() : null;
				managedInstance = (ManagedInstance<X>) this.processRow(this.session, rs, cache, root, 0);
			}

			return managedInstance;
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
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ManagedInstance<?> processRow(SessionImpl session, ResultSet rs, Map<ManagedId<?>, ManagedInstance<?>> cache, Object root,
		int depth) throws SQLException {
		EntityTypeImpl<?> currentType;

		if (depth == 0) {
			currentType = this.rootType;
		}
		else {
			currentType = this.entityPaths.get(depth - 1).getLast().getType();
		}

		final ManagedInstance<?> managedInstance = this.createManagedInstance(session, rs, cache, depth, currentType);
		if (managedInstance.getStatus() == Status.MANAGED) {
			return managedInstance;
		}

		cache.put(managedInstance.getId(), managedInstance);

		for (final EntityTable table : currentType.getTables().values()) {
			for (final PhysicalColumn column : table.getColumns()) {
				if (column.getMapping() instanceof BasicMapping) {
					if (column.isId()) { // primary key values already handled
						continue;
					}

					final Object columnValue = this.getColumnValue(rs, column);
					column.getMapping().setValue(managedInstance.getInstance(), columnValue);
				}
			}
		}

		if (this.entityPaths.size() > depth) {
			final ManagedInstance<?> child = this.processRow(session, rs, cache, root, depth + 1);

			final Deque<Association<?, ?>> path = this.entityPaths.get(depth);
			path.getLast().setValue(managedInstance.getInstance(), child.getInstance());
		}

		return managedInstance;
	}
}

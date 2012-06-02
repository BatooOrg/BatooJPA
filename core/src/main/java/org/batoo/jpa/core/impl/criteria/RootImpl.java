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
package org.batoo.jpa.core.impl.criteria;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.persistence.criteria.Root;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/**
 * A root type in the from clause. Query roots always reference entities.
 * 
 * @param <X>
 *            the entity type referenced by the root
 * @author hceylan
 * @since $version
 */
public class RootImpl<X> extends FromImpl<X, X> implements Root<X> {

	private final EntityTypeImpl<X> entity;
	private final HashBiMap<String, BasicColumn> idFields = HashBiMap.create();
	private final HashBiMap<String, BasicColumn> fields = HashBiMap.create();
	private final HashBiMap<String, EntityTable> tableAliases = HashBiMap.create();

	/**
	 * @param entity
	 *            the entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RootImpl(EntityTypeImpl<X> entity) {
		super();

		this.entity = entity;
	}

	/**
	 * Returns the generated from SQL fragment.
	 * 
	 * @param query
	 *            the query the root belongs to
	 * @return the generated from SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateFrom(BaseQueryImpl<?> query) {
		final EntityTable defaultTable = this.entity.getPrimaryTable();

		this.tableAliases.put("T0", defaultTable);
		return defaultTable.getQName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSelect() {
		final ArrayList<String> fields = Lists.newArrayList();

		for (final EntityTable table : this.entity.getTables()) {

			final Collection<BasicColumn> basicColumns = table.getColumns();
			for (final BasicColumn basicColumn : basicColumns) {

				this.tableAliases.inverse().get(basicColumn.getTable());

				final String field = Joiner.on(".").skipNulls().join(this.getAlias(), basicColumn.getName());
				if (basicColumn instanceof PkColumn) {
					this.idFields.put(field, basicColumn);
				}
				else {
					this.fields.put(field, basicColumn);
				}

				fields.add(field);
			}
		}

		return "SELECT " + Joiner.on(", ").join(fields);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ManagedInstance<X> handleRow(EntityManagerImpl entityManager, ResultSet rs) throws SQLException {
		final SessionImpl session = entityManager.getSession();

		// create the id of for the instance
		Object id = null;
		if (this.entity.hasSingleIdAttribute()) {
			final String field = this.idFields.keySet().iterator().next();
			id = rs.getObject(field);
		}

		final ManagedInstance<X> instance = this.entity.getManagedInstanceById(session, id);
		for (final Entry<String, BasicColumn> entry : this.fields.entrySet()) {
			final String field = entry.getKey();
			final BasicColumn basicColumn = entry.getValue();
			final Object value = rs.getObject(field);
			basicColumn.setValue(instance.getInstance(), value);
		}

		return instance;
	}
}

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

import java.sql.SQLException;
import java.util.Collection;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * Select helper to select against the primary key
 * 
 * @param <X>
 * @author hceylan
 * @since $version
 */
public class RefreshHelper<X> extends BaseSelectHelper<X> {

	/**
	 * @param type
	 *            the type to select against
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RefreshHelper(EntityTypeImpl<X> type) {
		super(type, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean cascades(Association<?, ?> association) {
		return association.cascadeRefresh();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getWhere() {
		// Generate the where statement
		// T1_F1 = ? [AND T1_F2 = ? [...]]
		return Joiner.on(" AND ").join(//
			Collections2.transform(this.predicates, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return "T0." + input.getPhysicalName() + " = ?";
				}
			}));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void preparePredicates() {
		for (final PhysicalColumn column : this.type.getPrimaryTable().getColumns()) {
			if (column.isId()) {
				RefreshHelper.this.predicates.add(column);
			}
		}
	}

	/**
	 * Performs the select and refresh the managed instance.
	 * 
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public void refresh(SessionImpl session, final ManagedInstance<X> managedInstance) throws SQLException {
		// Do not inline, generation of the select SQL will initialize the predicates!
		final String selectSql = this.getSelectSql();

		final Collection<Object> params = Collections2.transform(this.predicates, new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				return input.getPhysicalValue(managedInstance.getSession(), managedInstance.getInstance());
			}
		});

		final RefreshHandler<X> rsHandler = new RefreshHandler<X>(session, this.type, this.columnAliases, this.entityPaths);

		this.runner.query(session.getConnection(), selectSql, rsHandler, params.toArray());
	}

}

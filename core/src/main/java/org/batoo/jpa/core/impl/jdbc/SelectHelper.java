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
import java.util.Set;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.OwnerOneToManyMapping;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

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
public class SelectHelper<X> extends BaseSelectHelper<X> {

	/**
	 * @param type
	 *            the type to select against
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SelectHelper(EntityTypeImpl<X> type) {
		super(type, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean cascades(Association<?, ?> association) {
		return association.isEager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected OwnerOneToManyMapping<?, ?, ?> getRootAssociation() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String preparePredicate(PhysicalColumn column) {
		if (!column.isDiscriminator()) {
			return "T0." + column.getPhysicalName() + " = ?";
		}

		final Set<Object> values = this.type.getDiscriminatorValues();
		final String valuesAsString = Joiner.on(", ").join(Collections2.transform(values, new Function<Object, String>() {

			@Override
			public String apply(Object input) {
				if (input instanceof Integer) {
					return ((Integer) input).toString();
				}

				return "'" + input + "'";
			}
		}));

		return "T0." + column.getPhysicalName() + " IN (" + valuesAsString + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void preparePredicates() {
		for (final PhysicalColumn column : this.type.getRoot().getPrimaryTable().getColumns()) {
			if (column.isId() || column.isDiscriminator()) {
				this.predicates.add(column);
			}

			if (column.isId()) {
				this.parameters.add(column);
			}
		}
	}

	/**
	 * Performs the select and populates the managed instance.
	 * 
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the the managed instance with the id
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public X select(SessionImpl session, final ManagedInstance<X> managedInstance) throws SQLException {
		// Do not inline, generation of the select SQL will initialize the predicates!
		final String selectSql = this.getSelectSql();

		final Object[] params = new Object[this.parameters.size()];
		for (int i = 0; i < params.length; i++) {
			params[i] = this.parameters.get(i).getPhysicalValue(managedInstance.getSession(), managedInstance.getInstance());
		}

		final SelectHandler<X> rsHandler = new SelectHandler<X>(session, this.type, this.columnAliases, this.root);

		final Collection<X> result = this.runner.query(session.getConnection(), selectSql, rsHandler, params);

		return (result != null) && (result.size() > 0) ? result.iterator().next() : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "SelectHelper [type=" + this.type.getName() + ", predicates=" + this.getPredicatesAsString() + "]";
	}

}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Selection;

import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The definition of an item that is to be returned in a query result.
 * 
 * @param <X>
 *            the type of the selection item
 * @author hceylan
 * @since $version
 */
public abstract class AbstractSelection<X> extends TupleElementImpl<X> implements Selection<X> {

	private String alias;

	/**
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractSelection(Class<X> javaType) {
		super(javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Selection<X> alias(String alias) {
		if (alias == null) {
			throw new IllegalStateException("Alias has already been set");
		}

		this.alias = alias;

		return this;
	}

	/**
	 * Returns the JPQL select fragment.
	 * 
	 * @return the JPQL select fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generateJpqlSelect();

	/**
	 * Generates SQL joins fragment that connects to root.
	 * 
	 * @param query
	 *            the query
	 * @param joins
	 *            the map of joins
	 * @since $version
	 * @author hceylan
	 */
	public void generateSqlJoinsUp(CriteriaQueryImpl<?> query, Map<Joinable, String> joins) {
	}

	/**
	 * Returns the SQL select fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the SQL select fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generateSqlSelect(CriteriaQueryImpl<?> query);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias() {
		return this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return null;
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the row
	 * @param instances
	 *            the set of instances processed
	 * @return the managed instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract X handle(SessionImpl session, ResultSet row, HashMap<ManagedInstance<?>, ManagedInstance<?>> instances) throws SQLException;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCompoundSelection() {
		return false;
	}
}

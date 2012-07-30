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
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.Subquery;

import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for all / any modifiers.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class AllAnyExpression<T> extends AbstractExpression<T> {

	private final boolean all;
	private final SubqueryImpl<T> subQuery;

	/**
	 * @param all
	 *            the type of the expression
	 * @param subquery
	 *            the sub query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AllAnyExpression(boolean all, Subquery<T> subquery) {
		super(((Class<T>) subquery.getJavaType()));

		this.all = all;
		this.subQuery = (SubqueryImpl<T>) subquery;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractQueryImpl<?> query) {
		return (this.all ? "all " : "any ") + this.subQuery.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractQueryImpl<?> query) {
		return new String[] { (this.all ? "ALL " : "ANY ") + this.subQuery.getSqlRestrictionFragments(query)[0] };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}
}

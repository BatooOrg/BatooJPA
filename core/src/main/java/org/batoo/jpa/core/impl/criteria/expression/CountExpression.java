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

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for count function.
 * 
 * @author hceylan
 * @since $version
 */
public class CountExpression extends AbstractExpression<Long> {

	private final AbstractExpression<?> inner;
	private final boolean distinct;

	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * @param distinct
	 *            if the count is distinct
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CountExpression(Expression<?> inner, boolean distinct) {
		super(Long.class);

		this.inner = (AbstractExpression<?>) inner;
		this.distinct = distinct;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("count(");

		if (this.distinct) {
			builder.append("distinct ");
		}

		builder.append(this.inner.generateJpqlRestriction(query));
		builder.append(")");

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (selected) {
			return this.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
		}

		return this.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("COUNT(");

		if (this.distinct) {
			builder.append("DISTINCT ");
		}

		builder.append(this.inner.getSqlRestrictionFragments(query)[0]);
		builder.append(")");

		return new String[] { builder.toString() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Long handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return row.getLong(this.alias);
	}
}

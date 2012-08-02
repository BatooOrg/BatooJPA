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
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for substring function.
 * 
 * @author hceylan
 * @since $version
 */
public class SubstringExpression extends AbstractExpression<String> {

	private String alias;
	private final AbstractExpression<String> inner;
	private final AbstractExpression<Integer> start;
	private final AbstractExpression<Integer> end;

	/**
	 * @param inner
	 *            the inner expression
	 * @param start
	 *            the start expression
	 * @param end
	 *            the end expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SubstringExpression(Expression<String> inner, Expression<Integer> start, Expression<Integer> end) {
		super(String.class);

		this.inner = (AbstractExpression<String>) inner;
		this.start = (AbstractExpression<Integer>) start;
		this.end = (AbstractExpression<Integer>) end;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		if (this.end != null) {
			return "substring(" + this.inner.generateJpqlRestriction(query) + "," //
				+ this.start.generateJpqlRestriction(query) + ")";
		}

		return "substring(" + this.inner.generateJpqlRestriction(query) + "," //
			+ this.start.generateJpqlRestriction(query) + "," //
			+ this.end.generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (selected && StringUtils.isBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		if (this.end == null) {
			return new String[] { "SUBSTR(" + this.inner.getSqlRestrictionFragments(query)[0] + //
				"," + this.start.getSqlRestrictionFragments(query)[0] + ")" };
		}

		return new String[] { "SUBSTR(" + this.inner.getSqlRestrictionFragments(query)[0] + "," //
			+ this.start.getSqlRestrictionFragments(query)[0] + "," //
			+ this.end.getSqlRestrictionFragments(query)[0] + ")" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (String) row.getObject(this.alias);
	}
}

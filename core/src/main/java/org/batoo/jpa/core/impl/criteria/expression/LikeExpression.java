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
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for like operation.
 * 
 * @author hceylan
 * @since $version
 */
public class LikeExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<String> inner;
	private final AbstractExpression<String> pattern;
	private final AbstractExpression<Character> escape;
	private String alias;
	private final boolean not;

	/**
	 * @param inner
	 *            the inner expression
	 * @param pattern
	 *            the pattern expression
	 * @param escape
	 *            the escape expression
	 * @param not
	 *            true if not
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public LikeExpression(Expression<String> inner, Expression<String> pattern, Expression<Character> escape, boolean not) {
		super(Boolean.class);

		this.inner = (AbstractExpression<String>) inner;
		this.pattern = (AbstractExpression<String>) pattern;
		this.escape = (AbstractExpression<Character>) escape;
		this.not = not;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.inner.generateJpqlRestriction(query));
		if (this.not) {
			builder.append(" not");
		}

		builder.append(" like ");

		builder.append(this.pattern.generateJpqlRestriction(query));
		if (this.escape != null) {
			builder.append(" escape ");
			builder.append(this.escape.generateJpqlRestriction(query));
		}

		return builder.toString();
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
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.inner.getSqlRestrictionFragments(query)[0]);
		if (this.not) {
			builder.append(" NOT");
		}

		builder.append(" LIKE ");

		builder.append(this.pattern.getSqlRestrictionFragments(query)[0]);
		if (this.escape != null) {
			builder.append(" {ESCAPE ");
			builder.append(this.escape.getSqlRestrictionFragments(query)[0]);
			builder.append("}");
		}

		return new String[] { builder.toString() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (Boolean) row.getObject(this.alias);
	}
}

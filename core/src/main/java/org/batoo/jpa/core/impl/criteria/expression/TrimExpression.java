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
import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder.Trimspec;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for trim function.
 * 
 * @author hceylan
 * @since $version
 */
public class TrimExpression extends AbstractExpression<String> {

	private final Trimspec trimspec;
	private final AbstractExpression<Character> trimChar;
	private final AbstractExpression<String> inner;

	private String alias;

	/**
	 * @param trimspec
	 *            the trim spec expression
	 * @param trimChar
	 *            the trim chararacter expression
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TrimExpression(Trimspec trimspec, Expression<Character> trimChar, Expression<String> inner) {
		super(String.class);

		this.trimspec = trimspec;
		this.trimChar = (AbstractExpression<Character>) trimChar;
		this.inner = (AbstractExpression<String>) inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("trim(");

		if (this.trimspec != null) {
			builder.append(this.trimspec.toString().toLowerCase(Locale.ENGLISH)).append(" ");
		}

		if (this.trimChar != null) {
			builder.append(this.trimChar.generateJpqlRestriction(query)).append(" ");
		}

		if ((this.trimspec != null) || (this.trimChar != null)) {
			builder.append("from ");
		}

		return builder//
		.append(this.inner.generateJpqlRestriction(query))//
		.append(")").toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
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
	public String generateSqlSelect(AbstractQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(AbstractQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("TRIM(");

		if (this.trimspec != null) {
			builder.append(this.trimspec.toString()).append(" ");
		}

		if (this.trimChar != null) {
			builder.append(this.trimChar.getSqlRestrictionFragments(query)[0]).append(" ");
		}

		if ((this.trimspec != null) || (this.trimChar != null)) {
			builder.append("FROM ");
		}

		return new String[] { builder//
		.append(this.inner.getSqlRestrictionFragments(query)[0])//
		.append(")").toString() };
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

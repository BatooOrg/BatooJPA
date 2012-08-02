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
import java.text.MessageFormat;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for string <code>upper</code> and <code>lower</code> functions.
 * 
 * @author hceylan
 * @since $version
 */
public class CaseTransformationExpression extends AbstractExpression<String> {

	@SuppressWarnings("javadoc")
	public enum CaseTransformationType {
		UPPER("UPPER({0})", "upper({0})"),

		LOWER("LOWER({0})", "lower{0}");

		private final String sqlFragment;
		private final String jpqlFragment;

		private CaseTransformationType(String sqlfragment, String jpqlFragment) {
			this.sqlFragment = sqlfragment;
			this.jpqlFragment = jpqlFragment;
		}
	}

	private final AbstractExpression<?> inner;
	private String alias;
	private final CaseTransformationType function;

	/**
	 * @param inner
	 *            the inner expression
	 * @param function
	 *            the string function
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CaseTransformationExpression(Expression<String> inner, CaseTransformationType function) {
		super(String.class);

		this.inner = (AbstractExpression<?>) inner;
		this.function = function;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return MessageFormat.format(this.function.jpqlFragment, this.inner.generateJpqlRestriction(query));
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

	private String generateSqlRestriction(BaseQueryImpl<?> query) {
		return MessageFormat.format(this.function.sqlFragment, this.inner.getSqlRestrictionFragments(query)[0]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (selected) {
			return this.generateSqlRestriction(query) + " AS " + this.alias;
		}

		return this.generateSqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return new String[] { this.generateSqlRestriction(query) };
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

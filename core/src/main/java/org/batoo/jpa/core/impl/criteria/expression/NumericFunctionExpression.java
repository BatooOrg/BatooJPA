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
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for numeric functions
 * 
 * @param <N>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class NumericFunctionExpression<N extends Number> extends AbstractExpression<N> {

	@SuppressWarnings("javadoc")
	public enum NumericFunctionType {
		MOD("MOD({0}, {1})", "mod({0}, {1})"),

		ABS("ABS({0})", "abs({0})"),

		SQRT("SQRT({0})", "sqrt({0})"),

		LENGTH("LENGTH({0})", "length({0})");

		private final String sqlFragment;
		private final String jpqlFragment;

		private NumericFunctionType(String sqlfragment, String jpqlFragment) {
			this.sqlFragment = sqlfragment;
			this.jpqlFragment = jpqlFragment;
		}
	}

	private final NumericFunctionType type;
	private final AbstractExpression<?> x;
	private final AbstractExpression<Integer> y;
	private String alias;

	/**
	 * @param type
	 *            the type of the function
	 * @param x
	 *            the first parameter
	 * @param y
	 *            the optional second parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public NumericFunctionExpression(NumericFunctionType type, Expression<?> x, Expression<Integer> y) {
		super((Class<N>) x.getJavaType());

		this.type = type;
		this.x = (AbstractExpression<?>) x;
		this.y = (AbstractExpression<Integer>) y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		final String xExpr = this.x.generateJpqlRestriction(query);
		final String yExpr = this.y != null ? this.y.generateJpqlRestriction(query) : null;

		return MessageFormat.format(this.type.jpqlFragment, xExpr, yExpr);
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
		final String xExpr = this.x.getSqlRestrictionFragments(query)[0];
		final String yExpr = this.y != null ? this.y.getSqlRestrictionFragments(query)[0] : null;

		return new String[] { MessageFormat.format(this.type.sqlFragment, xExpr, yExpr) };

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public N handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (N) row.getObject(this.alias);
	}
}

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
import java.util.List;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Expression for db functions.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class FunctionExpression<T> extends AbstractExpression<T> {

	private final List<Expression<?>> arguments = Lists.newArrayList();
	private String alias;
	private final String function;

	/**
	 * @param javaType
	 *            the return type
	 * @param function
	 *            the db function
	 * @param arguments
	 *            the expressions as arguments
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FunctionExpression(Class<T> javaType, String function, Expression<?>... arguments) {
		super(javaType);

		this.function = function;

		for (final Expression<?> argument : arguments) {
			this.arguments.add(argument);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		return this.function + "(" + Joiner.on(", ").join(Lists.transform(this.arguments, new Function<Expression<?>, String>() {

			@Override
			public String apply(Expression<?> input) {
				return ((AbstractExpression<?>) input).generateJpqlRestriction(query);
			}
		})) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(final BaseQueryImpl<?> query) {
		return new String[] { this.function + Joiner.on("").join(Lists.transform(this.arguments, new Function<Expression<?>, String>() {

			@Override
			public String apply(Expression<?> input) {
				if (input instanceof ConstantExpression) {
					final String result = ((AbstractExpression<?>) input).getSqlRestrictionFragments(query)[0];
					if (result.startsWith("'")) {
						return result.substring(1, result.length() - 1);
					}

					return result;
				}

				return ((AbstractExpression<?>) input).getSqlRestrictionFragments(query)[0];
			}
		})) };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (T) row.getObject(this.alias);
	}
}

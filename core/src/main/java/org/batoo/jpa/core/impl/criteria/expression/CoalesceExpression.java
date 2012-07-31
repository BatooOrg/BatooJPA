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

import javax.persistence.criteria.CriteriaBuilder.Coalesce;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Expression for colesce function.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class CoalesceExpression<T> extends AbstractExpression<T> implements Coalesce<T> {

	private final List<Expression<? extends T>> values = Lists.newArrayList();
	private String alias;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public CoalesceExpression() {
		super((Class<T>) Object.class);
	}

	/**
	 * @param x
	 *            the x expression
	 * @param y
	 *            the y expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CoalesceExpression(Expression<? extends T> x, Expression<? extends T> y) {
		this();

		this.value(x);
		this.value(y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final AbstractQueryImpl<?> query) {
		return "coalesce(" + Joiner.on(", ").join(Lists.transform(this.values, new Function<Expression<? extends T>, String>() {

			@Override
			public String apply(Expression<? extends T> input) {
				return ((AbstractExpression<? extends T>) input).generateJpqlRestriction(query);
			}
		})) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(final AbstractQueryImpl<?> query) {
		return new String[] { "COALESCE(" + Joiner.on(", ").join(Lists.transform(this.values, new Function<Expression<? extends T>, String>() {

			@Override
			public String apply(Expression<? extends T> input) {
				return ((AbstractExpression<? extends T>) input).getSqlRestrictionFragments(query)[0];
			}
		})) + ")" };
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CoalesceExpression<T> value(Expression<? extends T> value) {
		this.values.add(value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CoalesceExpression<T> value(T value) {
		return this.value(new ConstantExpression<T>(null, value));
	}
}

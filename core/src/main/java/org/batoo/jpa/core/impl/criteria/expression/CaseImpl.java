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

import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link Case}.
 * 
 * @param <T>
 *            the type of the case expression
 * 
 * @author hceylan
 * @since $version
 */
public class CaseImpl<T> extends AbstractExpression<T> implements Case<T> {

	private final List<Pair<Expression<Boolean>, Expression<? extends T>>> conditions = Lists.newArrayList();
	private Expression<? extends T> otherwise;
	private String alias;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public CaseImpl() {
		super((Class<T>) Object.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final AbstractQueryImpl<?> query) {
		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<Boolean>, Expression<? extends T>>, String>() {

				@Override
				public String apply(Pair<Expression<Boolean>, Expression<? extends T>> input) {
					final AbstractExpression<Boolean> when = (AbstractExpression<Boolean>) input.getFirst();
					final AbstractExpression<? extends T> then = (AbstractExpression<? extends T>) input.getSecond();

					return "when " + when.generateJpqlRestriction(query) + " then " + then.generateJpqlRestriction(query);
				}
			}));

		final String otherwise = "\n\telse " + ((AbstractExpression<? extends T>) this.otherwise).generateJpqlRestriction(query);

		return "case\n\t" + whens + otherwise + "\nend";
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
		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<Boolean>, Expression<? extends T>>, String>() {

				@Override
				public String apply(Pair<Expression<Boolean>, Expression<? extends T>> input) {
					final AbstractExpression<Boolean> when = (AbstractExpression<Boolean>) input.getFirst();
					final AbstractExpression<? extends T> then = (AbstractExpression<? extends T>) input.getSecond();

					return "WHEN " + when.getSqlRestrictionFragments(query)[0] + " THEN " + then.getSqlRestrictionFragments(query)[0];
				}
			}));

		final String otherwise = "\n\tELSE " + ((AbstractExpression<? extends T>) this.otherwise).getSqlRestrictionFragments(query)[0];

		return new String[] { "CASE\n\t" + whens + otherwise + "\nEND" };
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
	public Expression<T> otherwise(Expression<? extends T> result) {
		this.otherwise = result;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<T> otherwise(T result) {
		return this.otherwise(new ConstantExpression<T>(null, result));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Case<T> when(Expression<Boolean> condition, Expression<? extends T> result) {
		this.conditions.add(new Pair<Expression<Boolean>, Expression<? extends T>>(condition, result));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Case<T> when(Expression<Boolean> condition, T result) {
		return this.when(condition, new ConstantExpression<T>(null, result));
	}
}

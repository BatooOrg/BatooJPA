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

import javax.persistence.criteria.CriteriaBuilder.SimpleCase;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link SimpleCase}.
 * 
 * @param <C>
 *            the type of the case expression
 * @param <R>
 *            the type of the result expression
 * 
 * @author hceylan
 * @since $version
 */
public class SimpleCaseImpl<C, R> extends AbstractExpression<R> implements SimpleCase<C, R> {

	private final Expression<? extends C> inner;
	private final List<Pair<Expression<C>, Expression<? extends R>>> conditions = Lists.newArrayList();
	private Expression<? extends R> otherwise;
	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public SimpleCaseImpl(Expression<? extends C> inner) {
		super((Class<R>) Object.class);

		this.inner = inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final AbstractCriteriaQueryImpl<?> query) {
		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<C>, Expression<? extends R>>, String>() {

				@Override
				public String apply(Pair<Expression<C>, Expression<? extends R>> input) {
					final AbstractExpression<C> when = (AbstractExpression<C>) input.getFirst();
					final AbstractExpression<? extends R> then = (AbstractExpression<? extends R>) input.getSecond();

					return "when " + when.generateJpqlRestriction(query) + " then " + then.generateJpqlRestriction(query);
				}
			}));

		final String otherwise = "\n\telse " + ((AbstractExpression<? extends R>) this.otherwise).generateJpqlRestriction(query);

		return "case " + ((AbstractExpression<? extends C>) this.inner).generateJpqlRestriction(query) + "\n\t" + whens + otherwise + "\nend";
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
	@SuppressWarnings("unchecked")
	public Expression<C> getExpression() {
		return (Expression<C>) this.inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(final AbstractCriteriaQueryImpl<?> query) {
		final String innerFragment = ((AbstractExpression<? extends C>) this.inner).getSqlRestrictionFragments(query)[0];

		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<C>, Expression<? extends R>>, String>() {

				@Override
				public String apply(Pair<Expression<C>, Expression<? extends R>> input) {
					final String conditionFragment = ((AbstractExpression<C>) input.getFirst()).getSqlRestrictionFragments(query)[0];
					final String resultFragment = ((AbstractExpression<? extends R>) input.getSecond()).getSqlRestrictionFragments(query)[0];

					return "WHEN " + innerFragment + " = " + conditionFragment + " THEN " + resultFragment;
				}
			}));

		final String otherwise = "\n\tELSE " + ((AbstractExpression<? extends R>) this.otherwise).getSqlRestrictionFragments(query)[0];

		return new String[] { "CASE\n\t" + whens + otherwise + "\nEND" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public R handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (R) row.getObject(this.alias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<R> otherwise(Expression<? extends R> result) {
		this.otherwise = result;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<R> otherwise(R result) {
		this.otherwise(new ConstantExpression<R>(null, result));

		return this;
	}

	/**
	 * Add a when/then clause to the case expression.
	 * 
	 * @param condition
	 *            "when" condition
	 * @param result
	 *            "then" result expression
	 * @return simple case expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SimpleCaseImpl<C, R> when(AbstractExpression<C> condition, AbstractExpression<R> result) {
		this.conditions.add(new Pair<Expression<C>, Expression<? extends R>>(condition, result));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SimpleCase<C, R> when(C condition, Expression<? extends R> result) {
		this.conditions.add(new Pair<Expression<C>, Expression<? extends R>>(new ConstantExpression<C>(null, condition), result));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SimpleCase<C, R> when(C condition, R result) {
		return this.when(condition, new ConstantExpression<R>(null, result));
	}
}

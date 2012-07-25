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
package org.batoo.jpa.core.impl.criteria;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.BooleanExpression;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link Predicate}.
 * 
 * @author hceylan
 * @since $version
 */
public class PredicateImpl extends BooleanExpression implements Predicate {

	private final BooleanOperator operator;
	private final boolean negated;
	private final List<BooleanExpression> expressions = Lists.newArrayList();
	private String alias;

	/**
	 * @param expression
	 *            the expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public PredicateImpl(AbstractExpression<Boolean> expression) {
		this(false, BooleanOperator.AND, expression);
	}

	/**
	 * @param expressions
	 *            the expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl(AbstractExpression<Boolean>... expressions) {
		this(false, BooleanOperator.AND, expressions);
	}

	/**
	 * @param negated
	 *            if negated
	 * @param operator
	 *            the operator
	 * @param expressions
	 *            the expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl(boolean negated, BooleanOperator operator, Expression<Boolean>... expressions) {
		super();

		this.negated = negated;
		this.operator = operator;

		for (final Expression<Boolean> expression : expressions) {
			if (expression instanceof BooleanExpression) {
				this.expressions.add((BooleanExpression) expression);
			}
			else {
				this.expressions.add(new BooleanExpression(expression));
			}
		}
	}

	/**
	 * @param negated
	 *            if negated
	 * @param operator
	 *            the operator
	 * @param predicates
	 *            the predicates
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl(boolean negated, BooleanOperator operator, Predicate... predicates) {
		super();

		this.negated = negated;
		this.operator = operator;

		for (final Predicate predicate : predicates) {
			this.expressions.add((PredicateImpl) predicate);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		if (this.negated) {
			builder.append("not (");
		}

		final List<String> expressions = Lists.transform(this.expressions, new Function<AbstractExpression<Boolean>, String>() {

			@Override
			public String apply(AbstractExpression<Boolean> input) {
				return input.generateJpqlRestriction(query);
			}
		});

		builder.append(Joiner.on(" " + this.operator.name() + " ").join(expressions));

		if (this.negated) {
			builder.append(")");
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlRestriction(final CriteriaQueryImpl<?> query) {

		final List<String> converted = Lists.transform(this.expressions, new Function<BooleanExpression, String>() {

			@Override
			public String apply(BooleanExpression input) {
				return input.generateSqlRestriction(query);
			}
		});

		if (this.negated) {
			return "NOT (" + Joiner.on(" " + this.operator.name() + " ").join(converted) + ")";
		}

		return Joiner.on(" " + this.operator.name() + " ").join(converted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		return this.generateSqlRestriction(query) + " AS " + this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<Boolean>> getExpressions() {
		final List<Expression<Boolean>> expressions = Lists.newArrayList();
		expressions.addAll(this.expressions);

		return expressions;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BooleanOperator getOperator() {
		return this.operator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return row.getBoolean(this.alias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isNegated() {
		return this.negated;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate not() {
		return new PredicateImpl(true, this.operator, this.expressions.toArray(new Expression[this.expressions.size()]));
	}
}

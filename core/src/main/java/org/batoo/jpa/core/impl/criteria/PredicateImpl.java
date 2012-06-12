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

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class PredicateImpl extends ExpressionImpl<Boolean> implements Predicate {

	private final BooleanOperator operator;
	private final boolean negated;
	private final List<ExpressionImpl<Boolean>> expressions = Lists.newArrayList();

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

		for (final Expression<Boolean> predicate : expressions) {
			this.expressions.add((ExpressionImpl<Boolean>) predicate);
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

		for (final Expression<Boolean> predicate : predicates) {
			this.expressions.add((ExpressionImpl<Boolean>) predicate);
		}
	}

	/**
	 * @param expressions
	 *            the expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl(ExpressionImpl<Boolean>... expressions) {
		this(false, BooleanOperator.AND, expressions);
	}

	/**
	 * Returns the description of the prediction.
	 * 
	 * @return the description of the prediction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String describe() {
		final StringBuilder builder = new StringBuilder();

		if (this.negated) {
			builder.append("not (\n");
		}

		final List<String> expressions = Lists.transform(this.expressions, new Function<ExpressionImpl<Boolean>, String>() {

			@Override
			public String apply(ExpressionImpl<Boolean> input) {
				return input.describe();
			}
		});

		builder.append(Joiner.on("\t\n " + this.operator.name()).join(expressions));

		if (this.negated) {
			builder.append("\n)");
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(final CriteriaQueryImpl<?> query) {
		final List<String> converted = Lists.transform(this.expressions, new Function<Expression<Boolean>, String>() {

			@Override
			public String apply(Expression<Boolean> input) {
				return ((ExpressionImpl<Boolean>) input).generate(query);
			}
		});

		return "(" + Joiner.on(this.operator.name()).join(converted) + ")";
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
	public List<Boolean> handle(SessionImpl session, BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, MutableInt rowNo) {
		// TODO Auto-generated method stub
		return null;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.describe();
	}
}

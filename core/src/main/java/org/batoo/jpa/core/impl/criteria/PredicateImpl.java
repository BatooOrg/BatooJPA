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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

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
	private final List<Expression<Boolean>> expressions;

	private PredicateImpl(boolean negated, BooleanOperator operator, List<Expression<Boolean>> expressions) {
		super();

		this.negated = negated;
		this.operator = operator;
		this.expressions = expressions;
	}

	/**
	 * @param expressions
	 *            the expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl(Expression<Boolean>... expressions) {
		this(false, BooleanOperator.AND, Lists.newArrayList(expressions));
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

		return Joiner.on(this.operator.name()).join(converted);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<Boolean>> getExpressions() {
		return Lists.newArrayList(this.expressions);
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
	public boolean isNegated() {
		return this.negated;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate not() {
		return new PredicateImpl(true, this.operator, this.expressions);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (this.negated) {
			builder.append("not");
		}

		builder.append(Joiner.on("\t\n " + this.operator.name()).join(this.expressions));

		return builder.toString();
	}
}

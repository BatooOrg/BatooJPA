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
package org.batoo.jpa.core.impl.criteria2;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link Predicate}.
 * 
 * @author hceylan
 * @since $version
 */
public class PredicateImpl extends AbstractExpression<Boolean> implements Predicate {

	/**
	 * The comparison types
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum Comparison {

		/**
		 * Equal comparison
		 */
		EQUAL(" = ");

		private final String fragment;

		Comparison(String fragment) {
			this.fragment = fragment;
		}

		/**
		 * Returns the fragment of the Comparison.
		 * 
		 * @return the fragment of the Comparison
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public String getFragment() {
			return this.fragment;
		}
	}

	private final AbstractExpression<?> x;
	private final AbstractExpression<?> y;
	private final boolean negated;
	private final Comparison comparison;

	/**
	 * @param x
	 *            the left expression
	 * @param y
	 *            the right expression
	 * @param negated
	 *            if the negated
	 * @param comparison
	 *            the comparison
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl(AbstractExpression<?> x, AbstractExpression<?> y, boolean negated, Comparison comparison) {
		super(Boolean.class);

		this.x = x;
		this.y = y;
		this.negated = negated;
		this.comparison = comparison;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpql() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(")//
		.append(this.x.generateJpql())//
		.append(this.comparison.fragment)//
		.append(this.y.generateJpql())//
		.append(")");

		if (StringUtils.isNotBlank(this.getAlias())) {
			sb.append(" as ").append(this.getAlias());
		}

		return sb.toString();
	}

	/**
	 * Returns the <code>where</code> fragment for the <code>JPQL</code> generation.
	 * 
	 * @return the <code>where</code> fragment for the <code>JPQL</code> generation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateWhereJpql() {
		return this.x.generateJpql() + this.comparison.fragment + this.y.generateJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<Boolean>> getExpressions() {
		final List<Expression<Boolean>> expressions = Lists.newArrayList();

		expressions.add(this);

		return expressions;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BooleanOperator getOperator() {
		return BooleanOperator.AND;
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
		return new PredicateImpl(this.x, this.y, !this.negated, this.comparison);
	}
}

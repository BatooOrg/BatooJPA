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

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Expression;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ConstantExpression;
import org.batoo.jpa.core.impl.criteria.expression.InExpression;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;

/**
 * Predicate for In expressions.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class InPredicate<T> extends PredicateImpl implements In<T> {

	private final Expression<T> inner;
	private final InExpression inExpr;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public InPredicate(Expression<? extends T> inner) {
		super(new InExpression((AbstractExpression<? extends T>) inner, new Expression[] {}));

		this.inner = (Expression<T>) inner;
		this.inExpr = (InExpression) this.getExpressions().get(0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<T> getExpression() {
		return this.inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public In<T> value(Expression<? extends T> value) {
		this.inExpr.add((AbstractExpression<?>) value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public In<T> value(T value) {
		return this.value(new ConstantExpression<T>(null, value));
	}
}

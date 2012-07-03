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

import java.util.Collection;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.SelectionImpl;
import org.batoo.jpa.core.impl.criteria.expression.CompoundExpression.Comparison;

/**
 * Type for query expressions.
 * 
 * @param <T>
 *            the type of the expression
 * @author hceylan
 * @since $version
 */
public abstract class AbstractExpression<T> extends SelectionImpl<T> implements Expression<T> {

	/**
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractExpression(Class<T> javaType) {
		super(javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> Expression<X> as(Class<X> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the description of the expression.
	 * 
	 * @return the description of the expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String describe();

	/**
	 * Returns the generated SQL fragment with comparison to parameter expression.
	 * 
	 * @param query
	 *            the query
	 * @param comparison
	 *            the comparison
	 * @param parameter
	 *            the parameter expression
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Collection<?> values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Expression<?>... values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Expression<Collection<?>> values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isNotNull() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isNull() {
		// TODO Auto-generated method stub
		return null;
	}
}

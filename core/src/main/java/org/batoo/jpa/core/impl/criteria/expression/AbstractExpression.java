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

import org.batoo.jpa.core.impl.criteria.AbstractSelection;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;

import com.google.common.collect.Lists;

/**
 * Type for query expressions.
 * 
 * @param <T>
 *            the type of the expression
 * @author hceylan
 * @since $version
 */
public abstract class AbstractExpression<T> extends AbstractSelection<T> implements Expression<T> {

	private ExpressionConverter<?> converter;

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
	 * Returns the JPQL where fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the JPQL where fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String generateJpqlRestriction(CriteriaQueryImpl<?> query);

	/**
	 * Returns the converter of the expression.
	 * 
	 * @return the converter of the expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ExpressionConverter<?> getConverter() {
		return this.converter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Collection<?> values) {
		return new PredicateImpl(new InExpression(this, values));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Expression<?>... values) {
		return new PredicateImpl(new InExpression(this, values));
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
		return new PredicateImpl(new InExpression(this, Lists.newArrayList(values)));
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

	/**
	 * Sets the numeric converter of the expression
	 * 
	 * @param converter
	 *            the numeric converter instance
	 * @param <N>
	 *            the type of the conversion
	 * @return the same expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <N> Expression<N> setConverter(ExpressionConverter<N> converter) {
		this.converter = converter;

		return (Expression<N>) this;
	}
}

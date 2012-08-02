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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.batoo.jpa.core.impl.criteria.AbstractSelection;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;

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
	@SuppressWarnings("unchecked")
	public <X> Expression<X> as(Class<X> type) {
		if (this.getJavaType() == type) {
			return (Expression<X>) this;
		}

		if (type == BigDecimal.class) {
			this.setConverter(ExpressionConverter.BIG_DECIMAL);
		}
		else if (type == BigInteger.class) {
			this.setConverter(ExpressionConverter.BIG_INTEGER);
		}
		else if (type == Double.class) {
			this.setConverter(ExpressionConverter.DOUBLE);
		}
		else if (type == Float.class) {
			this.setConverter(ExpressionConverter.FLOAT);
		}
		else if (type == Integer.class) {
			this.setConverter(ExpressionConverter.INTEGER);
		}
		else if (type == Long.class) {
			this.setConverter(ExpressionConverter.LONG);
		}
		else if (type == String.class) {
			this.setConverter(ExpressionConverter.STRING);
		}

		throw new PersistenceException("Cannot cast to :" + type);
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
	public abstract String generateJpqlRestriction(BaseQueryImpl<?> query);

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
	public PredicateImpl in(Expression<?>... values) {
		return new PredicateImpl(new InExpression(this, values));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl in(Expression<Collection<?>> values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl in(Object... values) {
		return new PredicateImpl(new InExpression(this, Lists.newArrayList(values)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl isNotNull() {
		return new PredicateImpl(new IsNullExpression(true, this));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl isNull() {
		return new PredicateImpl(new IsNullExpression(false, this));
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

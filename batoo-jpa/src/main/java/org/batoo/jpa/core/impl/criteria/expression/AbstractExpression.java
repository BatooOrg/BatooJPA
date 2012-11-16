/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.criteria.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.Expression;

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
	 */
	public abstract String generateJpqlRestriction(BaseQueryImpl<?> query);

	/**
	 * Returns the converter of the expression.
	 * 
	 * @return the converter of the expression
	 * 
	 * @since $version
	 */
	public ExpressionConverter<?> getConverter() {
		return this.converter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl in(Collection<?> values) {
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
	 */
	@SuppressWarnings("unchecked")
	public <N> Expression<N> setConverter(ExpressionConverter<N> converter) {
		this.converter = converter;

		return (Expression<N>) this;
	}
}

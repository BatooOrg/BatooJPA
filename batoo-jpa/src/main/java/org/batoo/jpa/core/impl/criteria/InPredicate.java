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
package org.batoo.jpa.core.impl.criteria;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Expression;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.EntityConstantExpression;
import org.batoo.jpa.core.impl.criteria.expression.InExpression;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;

/**
 * Predicate for In expressions.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class InPredicate<T> extends PredicateImpl implements In<T> {

	private final Expression<T> inner;
	private final InExpression inExpr;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since 2.0.0
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
		return this.value(new EntityConstantExpression<T>(null, value));
	}
}

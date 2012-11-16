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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;

/**
 * Implementation of {@link Order}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class OrderImpl implements Order {

	private final AbstractExpression<?> inner;
	private final boolean reverse;

	/**
	 * @param inner
	 *            the inner expression
	 * @param reverse
	 *            if the ordering is in reverse order
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public OrderImpl(Expression<?> inner, boolean reverse) {
		super();

		this.inner = (AbstractExpression<?>) inner;
		this.reverse = reverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractExpression<?> getExpression() {
		return this.inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAscending() {
		return !this.reverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Order reverse() {
		return new OrderImpl(this.inner, !this.reverse);
	}
}

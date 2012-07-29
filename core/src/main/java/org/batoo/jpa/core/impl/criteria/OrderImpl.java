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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;

/**
 * Implementation of {@link Order}.
 * 
 * @author hceylan
 * @since $version
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
	 * @since $version
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

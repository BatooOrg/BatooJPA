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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.batoo.jpa.core.impl.model.MetamodelImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Base of the {@link CriteriaQueryImpl} that performs the SQL generations.
 * 
 * @param <T>
 *            the type of the result
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaQueryImpl<T> extends AbstractCriteriaQueryImpl<T> implements CriteriaQuery<T> {

	private final ArrayList<OrderImpl> orderList = Lists.newArrayList();;

	/**
	 * @param metamodel
	 *            the metamodel;
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaQueryImpl(MetamodelImpl metamodel) {
		super(metamodel, null);
	}

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaQueryImpl(MetamodelImpl metamodel, Class<T> resultType) {
		super(metamodel, resultType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> distinct(boolean distinct) {
		return (CriteriaQueryImpl<T>) super.distinct(distinct);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String generateJpql() {
		if (this.orderList.size() > 0) {
			final String orderBy = Joiner.on(", ").join(Lists.transform(this.orderList, new Function<OrderImpl, String>() {

				@Override
				public String apply(OrderImpl input) {
					return input.isAscending() ? //
						input.getExpression().generateJpqlRestriction(CriteriaQueryImpl.this) + " asc" : //
						input.getExpression().generateJpqlRestriction(CriteriaQueryImpl.this) + " desc";
				}
			}));

			return super.generateJpql() + "\norder by\n\t" + orderBy;
		}

		return super.generateJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String generateSql() {
		if (this.orderList.size() > 0) {
			final String orderBy = Joiner.on(", ").join(Lists.transform(this.orderList, new Function<OrderImpl, String>() {

				@Override
				public String apply(OrderImpl input) {
					return input.isAscending() ? //
						input.getExpression().generateSqlSelect(CriteriaQueryImpl.this, false) + " asc" : //
						input.getExpression().generateSqlSelect(CriteriaQueryImpl.this, false) + " desc";
				}
			}));

			return super.generateSql() + "\nORDER BY\n\t" + orderBy;
		}

		return super.generateSql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Order> getOrderList() {
		final List<Order> orderList = Lists.newArrayList();
		orderList.addAll(this.orderList);

		return orderList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> groupBy(Expression<?>... grouping) {
		return (CriteriaQuery<T>) super.groupBy(grouping);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> groupBy(List<Expression<?>> grouping) {
		return (CriteriaQuery<T>) super.groupBy(grouping);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> having(Expression<Boolean> restriction) {
		return (CriteriaQuery<T>) super.having(restriction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> having(Predicate... restrictions) {
		return (CriteriaQuery<T>) super.having(restrictions);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> multiselect(List<Selection<?>> selectionList) {
		return this.select(new CompoundSelectionImpl<T>(this.getResultType(), selectionList));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> multiselect(Selection<?>... selections) {
		return this.select(new CompoundSelectionImpl<T>(this.getResultType(), selections));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> orderBy(List<Order> o) {
		this.orderList.clear();

		for (final Order order : o) {
			this.orderList.add((OrderImpl) order);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> orderBy(Order... o) {
		this.orderList.clear();

		for (final Order order : o) {
			this.orderList.add((OrderImpl) order);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> select(Selection<? extends T> selection) {
		return (CriteriaQueryImpl<T>) super.select(selection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> where(Expression<Boolean> restriction) {
		return (CriteriaQueryImpl<T>) super.where(restriction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> where(Predicate... restrictions) {
		return (CriteriaQueryImpl<T>) super.where(restrictions);
	}
}

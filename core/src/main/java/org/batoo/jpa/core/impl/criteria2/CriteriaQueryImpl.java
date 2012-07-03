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
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * The implementation of {@link CriteriaQuery}.
 * 
 * @param <T>
 *            type of the defined result
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaQueryImpl<T> extends AbstractQueryImpl<T> implements CriteriaQuery<T> {

	private static final BLogger LOG = BLoggerFactory.getLogger(CriteriaQueryImpl.class);

	private AbstractSelection<T> selection;
	private PredicateImpl restriction;

	private String jpql;
	private String sql;

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
	public CriteriaQuery<T> distinct(boolean distinct) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Generates and returns the JPQL query.
	 * 
	 * @return the JPQL query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String generateJpql() {
		if (this.jpql != null) {
			return this.jpql;
		}

		synchronized (this) {
			if (this.jpql != null) {
				return this.jpql;
			}

			return this.generateJpqlImpl();
		}
	}

	@SuppressWarnings("unchecked")
	private String generateJpqlImpl() {
		final List<String> roots = Lists.newArrayList();
		for (final Root<?> root : this.getRoots()) {
			roots.add(((RootImpl<?>) root).generateFromJpql());
		}

		if (this.selection == null) {
			this.selection = (AbstractSelection<T>) this.getRoots().iterator().next();
		}

		final StringBuilder builder = new StringBuilder("select\n\t").append(this.selection.generateJpql());
		builder.append("\nfrom\n\t").append(Joiner.on(", ").join(roots));
		if (this.restriction != null) {
			builder.append("\nwhere\n\t").append(this.restriction.generateWhereJpql());
		}

		return builder.toString();
	}

	/**
	 * Generates and returns the SQL query.
	 * 
	 * @return the JPQL query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSql() {
		if (this.sql != null) {
			return this.sql;
		}

		synchronized (this) {
			if (this.sql != null) {
				return this.sql;
			}

			return this.generateSqlImpl();
		}
	}

	private String generateSqlImpl() {
		CriteriaQueryImpl.LOG.debug("Preparing SQL for {0}", CriteriaQueryImpl.LOG.lazyBoxed(this.generateJpql()));

		// generate from fragment
		final List<String> froms = Lists.newArrayList();
		for (final Root<?> r : this.getRoots()) {
			final RootImpl<?> root = (RootImpl<?>) r;
			froms.add(root.generateFromSql(this));
		}

		final StringBuilder builder = new StringBuilder("select\n\t").append(this.selection.generateSelectSql(this));
		builder.append("\nfrom\n\t").append(Joiner.on(", ").join(froms));
		if (this.restriction != null) {
			builder.append("\nwhere\n\t").append(this.restriction.generateWhereJpql());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Order> getOrderList() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ParameterExpression<?>> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl getRestriction() {
		return this.restriction;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractSelection<T> getSelection() {
		return this.selection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> groupBy(Expression<?>... grouping) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> groupBy(List<Expression<?>> grouping) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> having(Expression<Boolean> restriction) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> having(Predicate... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isDistinct() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> multiselect(List<Selection<?>> selectionList) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> multiselect(Selection<?>... selections) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> orderBy(List<Order> o) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> orderBy(Order... o) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CriteriaQueryImpl<T> select(Selection<? extends T> selection) {
		this.selection = (AbstractSelection<T>) selection;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.generateJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> where(Expression<Boolean> restriction) {
		this.restriction = (PredicateImpl) restriction;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> where(Predicate... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}
}

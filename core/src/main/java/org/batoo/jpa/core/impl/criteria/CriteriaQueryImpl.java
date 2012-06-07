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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Base of the {@link CriteriaQueryImpl} that performs the SQL generations.
 * 
 * @param <T>
 *            the type of the result
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaQueryImpl<T> extends AbstractQueryImpl<T> implements CriteriaQuery<T> {

	private final Map<String, ParameterExpressionImpl<?>> parameterMap = Maps.newHashMap();
	private final List<ParameterExpressionImpl<?>> parameters = Lists.newArrayList();
	protected boolean distinct;

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
	 * Adds the parameter to the query.
	 * 
	 * @param parameter
	 *            the parameter to add
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addParameter(ParameterExpressionImpl<?> parameter) {
		this.parameters.add(parameter);
		this.parameterMap.put(parameter.getName(), parameter);

		parameter.setPosition(this.parameters.size());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> distinct(boolean distinct) {
		this.distinct = distinct;

		return null;
	}

	/**
	 * Generates the SQL and the parameters.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void generate() {
		if (this.sql == null) {
			this.sql = this.prepareSql();
		}
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
	 * Returns the parameter at the position.
	 * 
	 * @param position
	 *            the position of the parameter
	 * @return the parameter at the position
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl<?> getParameter(int position) {
		this.generate();

		return this.parameters.get(position - 1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ParameterExpression<?>> getParameters() {
		final Set<ParameterExpression<?>> parameters = Sets.newHashSet();
		parameters.addAll(this.parameterMap.values());

		return parameters;
	}

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSql() {
		this.generate();

		return this.sql;
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
	public boolean isDistinct() {
		return this.distinct;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> multiselect(List<Selection<?>> selectionList) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> multiselect(Selection<?>... selections) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> orderBy(List<Order> o) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<T> orderBy(Order... o) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private synchronized String prepareSql() {
		// other thread prepared already?
		if (this.sql != null) {
			return this.sql;
		}

		final PredicateImpl restriction = this.getRestriction();

		// generate from chunk
		final List<String> froms = Lists.newArrayList();
		final List<String> joins = Lists.newArrayList();
		for (final Root<?> r : this.getRoots()) {
			final RootImpl<?> root = (RootImpl<?>) r;
			froms.add(root.generateFrom(this));
			joins.add(root.generateJoins(this));
		}

		// generate the select chunk
		final String select = "SELECT\n\t" + this.selection.generate(this);
		final String where = restriction != null ? "WHERE " + restriction.generate(this) : null;

		final String from = "FROM " + Joiner.on(",").join(froms);
		final String join = Joiner.on(",").join(joins);

		return Joiner.on("\n").skipNulls().join(select, //
			from, //
			StringUtils.isBlank(join) ? null : join, //
			StringUtils.isBlank(where) ? null : where);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CriteriaQueryImpl<T> select(Selection<? extends T> selection) {
		this.selection = (SelectionImpl<T>) selection;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CriteriaQueryImpl<T> where(Expression<Boolean> restriction) {
		this.restriction = new PredicateImpl(restriction);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> where(Predicate... restrictions) {
		this.restriction = new PredicateImpl(restrictions);

		return this;
	}
}

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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.criteria.path.EntityPath;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.util.BatooUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
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

	private static final BLogger LOG = BLoggerFactory.getLogger(CriteriaQueryImpl.class);

	private final Map<String, ParameterExpressionImpl<?>> parameterMap = Maps.newHashMap();
	private final List<ParameterExpressionImpl<?>> parameters = Lists.newArrayList();
	private boolean distinct;
	private boolean internal;

	private String sql;
	private String jpql;

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
	public CriteriaQueryImpl<T> distinct(boolean distinct) {
		this.distinct = distinct;

		return this;
	}

	private String generateJpql() {
		final StringBuilder builder = new StringBuilder();

		if (this.selection != null) {
			builder.append("select ");

			// append distinct if necessary
			if (this.distinct) {
				builder.append("distinct ");
			}

			builder.append(this.selection.generateJpqlSelect());
		}

		final Collection<String> roots = Collections2.transform(this.getRoots(), new Function<Root<?>, String>() {

			@Override
			public String apply(Root<?> input) {
				final StringBuilder builder = new StringBuilder(input.getModel().getName());

				if (StringUtils.isNotBlank(input.getAlias())) {
					builder.append(" as ").append(input.getAlias());
				}

				return builder.toString();
			}
		});
		builder.append("\nfrom ").append(Joiner.on(", ").join(roots));

		if (this.selection instanceof EntityPath) {
			final String join = ((EntityPath<?, ?>) this.selection).generateJpqlFetches();
			if (StringUtils.isNotBlank(join)) {
				builder.append("\n").append(BatooUtils.indent(join));
			}
		}

		if (this.getRestriction() != null) {
			builder.append("\nwhere\n\t").append(this.getRestriction().generateJpqlRestriction());
		}

		return builder.toString();
	}

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String generateSql() {
		CriteriaQueryImpl.LOG.debug("Preparing SQL for {0}", CriteriaQueryImpl.LOG.lazyBoxed(this));

		final Map<Joinable, String> joins = Maps.newLinkedHashMap();

		// generate the select chunk
		final StringBuilder select = new StringBuilder();
		select.append("SELECT");
		if (this.distinct && !this.internal) {
			select.append(" DISTINCT");
		}
		select.append("\n\t");
		select.append(this.selection.generateSqlSelect(this));

		// generate from chunk
		final List<String> froms = Lists.newArrayList();
		for (final Root<?> r : this.getRoots()) {
			final RootImpl<?> root = (RootImpl<?>) r;
			froms.add(root.generateSqlFrom(this));
		}

		if (this.selection instanceof EntityPath) {
			((EntityPath<?, ?>) this.selection).generateSqlJoins(this, joins);
		}

		final String restriction = this.generateSqlRestriction();
		final String where = StringUtils.isNotBlank(restriction) ? "WHERE " + restriction : null;

		final String from = "FROM " + Joiner.on(",").join(froms);
		final String join = Joiner.on("\n").skipNulls().join(joins.values());

		return Joiner.on("\n").skipNulls().join(select, //
			from, //
			StringUtils.isBlank(join) ? null : join, //
			StringUtils.isBlank(where) ? null : where);
	}

	/**
	 * Returns the restriction for the query.
	 * 
	 * @return the restriction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String generateSqlRestriction() {
		final String[] restrictions = new String[this.getRoots().size() + 1];

		if (this.restriction != null) {
			restrictions[0] = this.restriction.generateSqlSelect(this);
		}

		int i = 0;
		final Iterator<Root<?>> j = this.getRoots().iterator();
		while (j.hasNext()) {
			final Root<?> root = j.next();
			restrictions[++i] = ((RootImpl<?>) root).generateDiscrimination();

		}

		return Joiner.on(" AND ").skipNulls().join(restrictions);
	}

	/**
	 * Returns the JPQL for the query.
	 * 
	 * @return the the JPQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getJpql() {
		if (this.jpql != null) {
			return this.jpql;
		}

		synchronized (this) {
			if (this.jpql != null) {
				return this.jpql;
			}

			return this.jpql = this.generateJpql();
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
		this.getSql();

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
	 * Returns the SQL for the query.
	 * 
	 * @return the the SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSql() {
		if (this.sql != null) {
			return this.sql;
		}

		synchronized (this) {
			if (this.sql != null) {
				return this.sql;
			}

			return this.sql = this.generateSql();
		}
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
	 * Marks the query as internal entity query.
	 * 
	 * @return self
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaQueryImpl<T> internal() {
		this.internal = true;
		this.distinct = true;

		return this;
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
		return this.getJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CriteriaQueryImpl<T> where(Expression<Boolean> restriction) {
		if (restriction instanceof PredicateImpl) {
			this.restriction = (PredicateImpl) restriction;
		}
		else {
			this.restriction = new PredicateImpl((AbstractExpression<Boolean>) restriction);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQueryImpl<T> where(Predicate... predicates) {
		this.restriction = new PredicateImpl(false, BooleanOperator.AND, predicates);

		return this;
	}
}

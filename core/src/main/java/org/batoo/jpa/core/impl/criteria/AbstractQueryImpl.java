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
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * the definition of the functionality that is common to both top-level queries and subqueries.
 * <p>
 * It is not intended to be used directly in query construction.
 * 
 * <p>
 * All queries must have a set of root entities (which may in turn own joins).
 * <p>
 * All queries may have a conjunction of restrictions.
 * 
 * @param <T>
 *            the type of the result
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("unchecked")
public abstract class AbstractQueryImpl<T> implements AbstractQuery<T> {

	private final MetamodelImpl metamodel;
	private final Class<T> resultType;

	private final Set<RootImpl<?>> roots = Sets.newHashSet();
	private int nextEntityAlias;
	private final List<ParameterExpressionImpl<?>> sqlParameters = Lists.newArrayList();

	/**
	 * The selection
	 */
	protected AbstractSelection<T> selection;

	private PredicateImpl restriction;
	private PredicateImpl groupRestriction;
	private boolean distinct;
	private final List<AbstractExpression<?>> groupList = Lists.newArrayList();

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractQueryImpl(MetamodelImpl metamodel, Class<T> resultType) {
		super();

		this.metamodel = metamodel;
		this.resultType = resultType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> distinct(boolean distinct) {
		this.distinct = distinct;

		return this;
	}

	/**
	 * Ensures that there is a valid selection.
	 * 
	 * @return the selection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AbstractSelection<T> ensureSelection() {
		if (this.selection == null) {
			if (this.getRoots().size() == 1) {
				return this.selection = (AbstractSelection<T>) this.getRoots().iterator().next();
			}
			else {
				throw new PersistenceException("Selection is not specified");
			}
		}

		return this.selection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> RootImpl<X> from(Class<X> entityClass) {
		final EntityTypeImpl<X> entity = this.getMetamodel().entity(entityClass);

		return this.from(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> RootImpl<X> from(EntityType<X> entity) {
		final RootImpl<X> r = new RootImpl<X>((EntityTypeImpl<X>) entity);

		this.roots.add(r);

		return r;
	}

	/**
	 * Returns the generated entity alias.
	 * 
	 * @param entity
	 *            true if the table is an entity table, false for element collections
	 * @return the generated entity alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateTableAlias(boolean entity) {
		return "E" + (!entity ? "C" : "") + this.nextEntityAlias++;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<?>> getGroupList() {
		final List<Expression<?>> groupList = Lists.newArrayList();
		groupList.addAll(this.groupList);

		return groupList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl getGroupRestriction() {
		return this.groupRestriction;
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since $version
	 */
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
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
	public Class<T> getResultType() {
		return this.resultType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Root<?>> getRoots() {
		return Sets.<Root<?>> newHashSet(this.roots);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractSelection<T> getSelection() {
		return this.ensureSelection();
	}

	/**
	 * Returns the SQL parameters of the query.
	 * 
	 * @return the SQL Parameters of the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<ParameterExpressionImpl<?>> getSqlParameters() {
		return this.sqlParameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> groupBy(Expression<?>... grouping) {
		this.groupList.clear();

		for (final Expression<?> expression : grouping) {
			this.groupList.add((AbstractExpression<?>) expression);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> groupBy(List<Expression<?>> grouping) {
		this.groupList.clear();

		for (final Expression<?> expression : grouping) {
			this.groupList.add((AbstractExpression<?>) expression);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> having(Expression<Boolean> restriction) {
		if (restriction instanceof PredicateImpl) {
			this.groupRestriction = (PredicateImpl) restriction;
		}
		else {
			this.groupRestriction = new PredicateImpl((AbstractExpression<Boolean>) restriction);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> having(Predicate... restrictions) {
		this.groupRestriction = new PredicateImpl(false, BooleanOperator.AND, restrictions);

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
	 * Adds the parameter to the SQL parameters queue.
	 * 
	 * @param parameter
	 *            the parameter to add
	 * @return the positional number of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int setNextSqlParam(ParameterExpressionImpl<?> parameter) {
		this.sqlParameters.add(parameter);

		return this.sqlParameters.size() - 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <U> Subquery<U> subquery(Class<U> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> where(Expression<Boolean> restriction) {
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
	public AbstractQuery<T> where(Predicate... restrictions) {
		this.restriction = new PredicateImpl(false, BooleanOperator.AND, restrictions);

		return this;
	}
}

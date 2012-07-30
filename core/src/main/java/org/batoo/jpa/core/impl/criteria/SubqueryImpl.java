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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.util.BatooUtils;

import com.google.common.collect.Sets;

/**
 * The implementation of sub query.
 * 
 * @param <T>
 *            the type of the sub query.
 * 
 * @author hceylan
 * @since $version
 */
public class SubqueryImpl<T> extends AbstractExpression<T> implements Subquery<T> {

	private final SubQueryStub<T> query;
	private final AbstractQueryImpl<?> parent;
	private final Set<AbstractFrom<?, ?>> correlatedJoins = Sets.newHashSet();

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent query
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SubqueryImpl(MetamodelImpl metamodel, AbstractQueryImpl<?> parent, Class<T> javaType) {
		super(javaType);

		this.parent = parent;
		this.query = new SubQueryStub<T>(parent, metamodel, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> CollectionJoin<X, Y> correlate(CollectionJoin<X, Y> parentCollection) {
		if (!this.correlatedJoins.contains(parentCollection)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentCollection);
		}

		return parentCollection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> Join<X, Y> correlate(Join<X, Y> parentJoin) {
		if (!this.correlatedJoins.contains(parentJoin)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentJoin);
		}

		return parentJoin;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> parentList) {
		if (!this.correlatedJoins.contains(parentList)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentList);
		}

		return parentList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> parentMap) {
		if (!this.correlatedJoins.contains(parentMap)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentMap);
		}

		return parentMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Root<Y> correlate(Root<Y> parentRoot) {
		if (!this.correlatedJoins.contains(parentRoot)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentRoot);
		}

		return parentRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> parentSet) {
		if (!this.correlatedJoins.contains(parentSet)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentSet);
		}

		return parentSet;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> distinct(boolean distinct) {
		return (SubqueryImpl<T>) this.query.distinct(distinct);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> Root<X> from(Class<X> entityClass) {
		return this.query.from(entityClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> Root<X> from(EntityType<X> entity) {
		return this.query.from(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractQueryImpl<?> query) {
		return "(\n" + BatooUtils.indent(BatooUtils.indent(this.query.generateJpql())) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		return null; // N/A;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Join<?, ?>> getCorrelatedJoins() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<?>> getGroupList() {
		return this.query.getGroupList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate getGroupRestriction() {
		return this.query.getGroupRestriction();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<?> getParent() {
		return this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate getRestriction() {
		return this.query.getRestriction();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<T> getResultType() {
		return this.query.getResultType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Root<?>> getRoots() {
		return this.query.getRoots();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Expression<T> getSelection() {
		return (Expression<T>) this.query.getSelection();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractQueryImpl<?> query) {
		return new String[] { "(\n" + BatooUtils.indent(BatooUtils.indent(this.query.getSql())) + ")" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> groupBy(Expression<?>... grouping) {
		return (SubqueryImpl<T>) this.query.groupBy(grouping);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> groupBy(List<Expression<?>> grouping) {
		return (SubqueryImpl<T>) this.query.groupBy(grouping);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> having(Expression<Boolean> restriction) {
		return (SubqueryImpl<T>) this.query.having(restriction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> having(Predicate... restrictions) {
		return (SubqueryImpl<T>) this.query.having(restrictions);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isDistinct() {
		return this.query.isDistinct();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Subquery<T> select(Expression<T> expression) {
		this.query.select(expression);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <U> SubqueryImpl<U> subquery(Class<U> type) {
		return this.query.subquery(type);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.query.getJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Subquery<T> where(Expression<Boolean> restriction) {
		this.query.where(restriction);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Subquery<T> where(Predicate... restrictions) {
		return (Subquery<T>) this.query.where(restrictions);
	}
}

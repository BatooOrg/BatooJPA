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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.expression.CompoundExpression.Comparison;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.path.EntityPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Represents a bound type, usually an entity that appears in the from clause, but may also be an embeddable belonging to an entity in the
 * from clause.
 * <p>
 * Serves as a factory for Joins of associations, embeddables, and collections belonging to the type, and for Paths of attributes belonging
 * to the type.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractFrom<Z, X> extends EntityPath<Z, X> implements From<Z, X> {

	private final EntityTypeImpl<X> entity;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractFrom(EntityTypeImpl<X> entity) {
		super(entity);

		this.entity = entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		return this.getFetchRoot().fetch(attribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		return this.getFetchRoot().fetch(attribute, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		return this.getFetchRoot().fetch(attribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.getFetchRoot().fetch(attribute, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(String attributeName) {
		return this.getFetchRoot().fetch(attributeName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
		return this.getFetchRoot().fetch(attributeName, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the restriction based on discrimination.
	 * 
	 * @return the restriction based on discrimination, <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateDiscrimination() {
		return this.getFetchRoot().generateDiscrimination();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public From<Z, X> getCorrelationParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Fetch<X, ?>> getFetches() {
		return this.getFetchRoot().getFetches();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Join<X, ?>> getJoins() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<X> handle(SessionImpl session, List<Map<String, Object>> data, MutableInt rowNo) {
		return this.getFetchRoot().handle(session, data, rowNo, 1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Collection<?> values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Expression<?>... values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Expression<Collection<?>> values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate in(Object... values) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCorrelated() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isNotNull() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isNull() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> Join<T, Y> join(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> Join<T, Y> join(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> CollectionJoin<T, Y> joinCollection(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> CollectionJoin<T, Y> joinCollection(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> ListJoin<T, Y> joinList(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> ListJoin<T, Y> joinList(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, K, V> MapJoin<T, K, V> joinMap(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, K, V> MapJoin<T, K, V> joinMap(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> SetJoin<T, Y> joinSet(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T, Y> SetJoin<T, Y> joinSet(String attributeName, JoinType jt) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getFetchRoot().generateJpqlFetches("");
	}
}

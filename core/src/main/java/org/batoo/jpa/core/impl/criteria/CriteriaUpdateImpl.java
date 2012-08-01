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

import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Implementation of {@link CriteriaUpdate}.
 * 
 * @param <T>
 *            the entity type that is the target of the update
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaUpdateImpl<T> extends BaseQuery<T> implements CriteriaUpdate<T> {

	private RootImpl<T> root;
	private final MetamodelImpl metamodel;
	private PredicateImpl restriction;

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaUpdateImpl(MetamodelImpl metamodel) {
		super();

		this.metamodel = metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Root<T> from(Class<T> entityClass) {
		if (this.root != null) {
			throw new IllegalStateException("Root has already assigned");
		}

		return this.root = new RootImpl<T>((EntityTypeImpl<T>) this.metamodel.getEntity(entityClass));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Root<T> from(EntityType<T> entity) {
		if (this.root != null) {
			throw new IllegalStateException("Root has already assigned");
		}

		return this.root = new RootImpl<T>((EntityTypeImpl<T>) entity);
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
	public Root<T> getRoot() {
		return this.root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CriteriaUpdate<T> set(Path<Y> attribute, Expression<? extends Y> value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y, X extends Y> CriteriaUpdate<T> set(Path<Y> attribute, X value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> attribute, Expression<? extends Y> value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y, X extends Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> attribute, X value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaUpdate<T> set(String attributeName, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <U> Subquery<U> subquery(Class<U> type) {
		return new SubqueryImpl<U>(this.metamodel, this, type);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaUpdate<T> where(Expression<Boolean> restriction) {
		this.restriction = new PredicateImpl((AbstractExpression<Boolean>) restriction);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaUpdate<T> where(Predicate... restrictions) {
		this.restriction = new PredicateImpl(this.restriction);

		return this;
	}
}

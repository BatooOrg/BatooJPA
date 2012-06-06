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

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;

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
public abstract class AbstractQueryImpl<T> implements AbstractQuery<T> {

	private final MetamodelImpl metamodel;
	private final Class<T> resultType;

	private final Set<RootImpl<?>> roots = Sets.newHashSet();
	protected PredicateImpl restriction;
	private int nextAlias;

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
	 * Returns the generated alias.
	 * 
	 * @return the generated alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateAlias() {
		return "E" + this.nextAlias++;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<?>> getGroupList() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate getGroupRestriction() {
		// TODO Auto-generated method stub
		return null;
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
	public <U> Subquery<U> subquery(Class<U> type) {
		// TODO Auto-generated method stub
		return null;
	}
}

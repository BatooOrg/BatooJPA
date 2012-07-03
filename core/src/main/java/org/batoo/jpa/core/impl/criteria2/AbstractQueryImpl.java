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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Sets;

/**
 * The implementation of {@link AbstractQuery}.
 * 
 * @param <T>
 *            type of the result
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractQueryImpl<T> implements AbstractQuery<T> {

	private final MetamodelImpl metamodel;
	private final Class<T> resultType;
	private final Set<RootImpl<?>> roots = Sets.newHashSet();

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
	public final <X> RootImpl<X> from(Class<X> entityClass) {
		return this.from(this.metamodel.entity(entityClass));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <X> RootImpl<X> from(EntityType<X> entity) {
		final RootImpl<X> root = new RootImpl<X>(((EntityTypeImpl<X>) entity).getRootMapping(), "R" + this.roots.size());
		this.roots.add(root);

		return root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final List<Expression<?>> getGroupList() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Predicate getGroupRestriction() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the metamodel of the AbstractQueryImpl.
	 * 
	 * @return the metamodel of the AbstractQueryImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<T> getResultType() {
		return this.resultType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Root<?>> getRoots() {
		final HashSet<Root<?>> roots = Sets.newHashSet();
		roots.addAll(this.roots);

		return roots;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <U> Subquery<U> subquery(Class<U> type) {
		// TODO Auto-generated method stub
		return null;
	}
}

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
package org.batoo.jpa.core.impl.criteria.join;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;

/**
 * Implementation of {@link Fetch}.
 * 
 * @param <Z>
 *            the source type of the fetch
 * @param <X>
 *            the target type of the fetch
 * 
 * @author hceylan
 * @since $version
 */
public class FetchImpl<Z, X> extends FetchParentImpl<Z, X> implements Fetch<Z, X> {

	private final FetchParentImpl<?, Z> parent;
	private final JoinedMapping<? super Z, ?, X> mapping;
	private final JoinType joinType;

	/**
	 * @param parent
	 *            the parent of the join
	 * @param mapping
	 *            the mapping of the join
	 * @param joinType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FetchImpl(FetchParentImpl<?, Z> parent, JoinedMapping<? super Z, ?, X> mapping, JoinType joinType) {
		super(mapping);

		this.parent = parent;
		this.mapping = mapping;
		this.joinType = joinType;
	}

	/**
	 * Returns the description of the fetch.
	 * 
	 * @param parent
	 *            the parent
	 * @return the description of the fetch
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String generateJpqlFetches(String parent) {
		final StringBuilder builder = new StringBuilder();

		builder.append("left join fetch ");

		builder.append(parent).append(".").append(this.mapping.getAttribute().getName());

		final String children = super.generateJpqlFetches(parent + "." + this.mapping.getAttribute().getName());
		if (StringUtils.isNotBlank(children)) {
			builder.append("\n").append(children);
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, List<String> selfJoins) {
		final String parentAlias = this.getParent().getPrimaryTableAlias(query);
		final String alias = this.getPrimaryTableAlias(query);

		selfJoins.add(this.mapping.join(parentAlias, alias, this.joinType));

		super.generateSqlJoins(query, selfJoins);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributeImpl<? super Z, ?> getAttribute() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinType getJoinType() {
		return this.joinType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinedMapping<? super Z, ?, X> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate getOn() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, Z> getParent() {
		return this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPrimaryTableAlias(AbstractCriteriaQueryImpl<?> query) {
		if (this.mapping.getType().getPersistenceType() == PersistenceType.EMBEDDABLE) {
			return this.parent.getPrimaryTableAlias(query);
		}

		return super.getPrimaryTableAlias(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Fetch<Z, X> on(Expression<Boolean> restriction) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Fetch<Z, X> on(Predicate... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}
}

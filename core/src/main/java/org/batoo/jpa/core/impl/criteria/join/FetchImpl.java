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
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;

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
	private final AssociationMapping<? super Z, ?, X> mapping;
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
	public FetchImpl(FetchParentImpl<?, Z> parent, AssociationMapping<? super Z, ?, X> mapping, JoinType joinType) {
		super(mapping.getType());

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

		switch (this.joinType) {
			case INNER:
				builder.append("inner");
				break;
			case LEFT:
				builder.append("left");
				break;
			case RIGHT:
				builder.append("right");
				break;
		}

		builder.append(" join fetch ");

		builder.append(parent).append(".").append(this.mapping.getAttribute().getName());

		final String children = super.generateJpqlFetches(this.mapping.getType().getName());
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
	public void generateSqlJoins(CriteriaQueryImpl<?> query, List<String> selfJoins) {
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
	public AssociationMapping<? super Z, ?, X> getMapping() {
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean shouldContinue(SessionImpl session, ManagedInstance<?> parent, Map<String, Object> row) {
		if (parent == null) {
			return true;
		}

		final ManagedInstance<? extends Z> instance = this.getParent().getInstance(session, row);

		return parent.equals(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.generateJpqlFetches(this.getParent().toString());
	}
}

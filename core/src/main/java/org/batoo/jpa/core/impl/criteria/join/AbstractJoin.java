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

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;

/**
 * Abstract implementation of joins.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractJoin<Z, X> extends AbstractFrom<Z, X> implements Join<Z, X> {

	private final AbstractFrom<?, Z> parent;
	private final JoinedMapping<? super Z, ?, X> mapping;
	private final JoinType joinType;

	/**
	 * @param parent
	 *            the parent
	 * @param mapping
	 *            the mapping
	 * @param jointType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractJoin(AbstractFrom<?, Z> parent, JoinedMapping<? super Z, ?, X> mapping, JoinType jointType) {
		super(parent, mapping.getType(), mapping, jointType);

		this.parent = parent;
		this.mapping = mapping;
		this.joinType = jointType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParent().generateJpqlSelect(query));
		builder.append(".").append(this.mapping.getAttribute().getName());
		if (StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Attribute<? super Z, ?> getAttribute() {
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
	public Predicate getOn() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractFrom<?, Z> getParent() {
		return this.parent;
	}
}

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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.ListAttribute;

import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

/**
 * Implementation of {@link ListJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * 
 * @author hceylan
 * @since $version
 */
public class ListJoinImpl<Z, E> extends AbstractJoin<Z, E> implements ListJoin<Z, E> {

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
	public ListJoinImpl(AbstractFrom<?, Z> parent, PluralMapping<? super Z, List<E>, E> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ListAttribute<? super Z, E> getModel() {
		return (ListAttribute<? super Z, E>) this.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> index() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListJoin<Z, E> on(Expression<Boolean> restriction) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListJoin<Z, E> on(Predicate... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}
}

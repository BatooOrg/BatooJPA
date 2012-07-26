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

import java.util.Collection;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.CollectionAttribute;

import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

/**
 * Implementation of {@link CollectionJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * 
 * @author hceylan
 * @since $version
 */
public class CollectionJoinImpl<Z, E> extends AbstractJoin<Z, E> implements CollectionJoin<Z, E> {

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
	public CollectionJoinImpl(AbstractFrom<?, Z> parent, PluralMapping<? super Z, Collection<E>, E> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CollectionAttribute<? super Z, E> getModel() {
		return (CollectionAttribute<? super Z, E>) this.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionJoin<Z, E> on(Expression<Boolean> restriction) {
		throw this.notSupported();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionJoin<Z, E> on(Predicate... restrictions) {
		throw this.notSupported();
	}
}

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

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import org.batoo.jpa.core.impl.model.EntityTypeImpl;

/**
 * A join to an entity, embeddable, or basic type.
 * 
 * @param <Z>
 *            the source type of the join
 * @param <X>
 *            the target type of the join
 * 
 * @author hceylan
 * @since $version
 */
public abstract class JoinImpl<Z, X> extends AbstractFromImpl<Z, X> implements Join<Z, X> {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinImpl(EntityTypeImpl<X> entity) {
		super(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Attribute<? super Z, ?> getAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinType getJoinType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public From<?, Z> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

}

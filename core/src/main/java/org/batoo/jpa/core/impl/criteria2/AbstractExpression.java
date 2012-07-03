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

import java.util.Collection;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * The abstract implementation of {@link Expression}.
 * 
 * @param <X>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractExpression<X> extends AbstractSelection<X> implements Expression<X> {

	/**
	 * @param javaType
	 *            the java type
	 * @param generatedAlias
	 *            the generated alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractExpression(Class<X> javaType, String generatedAlias) {
		super(javaType, generatedAlias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> as(Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
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
}

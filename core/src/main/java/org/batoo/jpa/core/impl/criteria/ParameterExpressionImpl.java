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

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.ParameterExpression;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;

/**
 * Type of org.batoo.jpa.core.impl.criteria query parameter expressions.
 * 
 * @param <T>
 *            the type of the parameter expression
 * @author hceylan
 * @since $version
 */
public class ParameterExpressionImpl<T> extends ExpressionImpl<T> implements ParameterExpression<T> {

	private final Class<T> paramClass;

	/**
	 * @param paramClass
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl(Class<T> paramClass) {
		super();

		this.paramClass = paramClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSelect() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<T> getParameterType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ManagedInstance<T> handleRow(EntityManagerImpl entityManager, ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}

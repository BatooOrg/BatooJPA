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
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Expression for constants.
 * 
 * @param <T>
 *            the type of the constant expression
 * 
 * @author hceylan
 * @since $version
 */
public class ConstantExpression<T> extends ParameterExpressionImpl<T> {

	private final T value;

	/**
	 * @param type
	 *            the type of the constant.
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ConstantExpression(TypeImpl<T> type, T value) {
		super(type, type.getJavaType(), null);

		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		this.ensureAlias(query);

		return this.value.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.ensureAlias(query);

		return this.value.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.value;
	}

	/**
	 * Sets the parameters expanding if necessary.
	 * 
	 * @param parameters
	 *            the SQL parameters
	 * @param sqlIndex
	 *            the index corresponding to expanded SQL parameter
	 * @param value
	 *            the value to set to the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public void setParameter(Object[] parameters, MutableInt sqlIndex, Object value) {
		super.setParameter(parameters, sqlIndex, this.value);
	}
}

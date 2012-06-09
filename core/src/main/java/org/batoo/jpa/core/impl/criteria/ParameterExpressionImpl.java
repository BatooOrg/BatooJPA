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

import javax.persistence.criteria.ParameterExpression;

import org.apache.commons.lang.StringUtils;

/**
 * Type of criteria query parameter expressions.
 * 
 * @param <T>
 *            the type of the parameter expression
 * @author hceylan
 * @since $version
 */
public class ParameterExpressionImpl<T> extends ExpressionImpl<T> implements ParameterExpression<T> {

	private final Class<T> paramClass;
	private final String name;
	private Integer position;

	/**
	 * @param paramClass
	 *            the class of the parameter
	 * @param name
	 *            the name of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl(Class<T> paramClass, String name) {
		super();

		this.paramClass = paramClass;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String describe() {
		final StringBuilder builder = new StringBuilder();

		if (StringUtils.isNotBlank(this.name)) {
			return builder.append(":").append(this.name).toString();
		}

		if (this.position != null) {
			return builder.append("?").append(this.position).toString();
		}

		return builder.append("?").toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		if (this.position == null) {
			query.addParameter(this);
		}

		return "?";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<T> getParameterType() {
		return this.paramClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getPosition() {
		return this.position;
	}

	/**
	 * Sets the position of the parameter
	 * 
	 * @param position
	 *            the position
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.describe();
	}
}

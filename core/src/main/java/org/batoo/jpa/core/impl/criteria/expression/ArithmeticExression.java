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

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Artithmetic operation expression.
 * 
 * @param <N>
 *            the type of the expression
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class ArithmeticExression<N extends Number> extends AbstractExpression<N> {

	/**
	 * The types of the arithmetic operations.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	@SuppressWarnings("javadoc")
	public enum ArithmeticOperation {
		ADD(" + "),

		SUBTRACT(" - "),

		MULTIPLY(" * "),

		DIVIDE(" / ");

		private final String fragment;

		private ArithmeticOperation(String fragment) {
			this.fragment = fragment;
		}

		public String getFragment() {
			return this.fragment;
		}
	}

	private final ArithmeticOperation operation;
	private final AbstractExpression<N> x;
	private final AbstractExpression<N> y;
	private String alias;

	/**
	 * @param operation
	 *            the operation
	 * @param x
	 *            the left side expression
	 * @param y
	 *            the right side expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ArithmeticExression(ArithmeticOperation operation, Expression<? extends N> x, Expression<? extends N> y) {
		super((Class<N>) x.getJavaType());

		this.operation = operation;
		this.x = (AbstractExpression<N>) x;
		this.y = (AbstractExpression<N>) y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.x.generateJpqlRestriction(query) + this.operation.getFragment() + this.y.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.x.generateJpqlRestriction(query));
		builder.append(this.operation.getFragment());
		builder.append(this.y.generateJpqlRestriction(query));

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
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (selected) {
			return this.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
		}

		return this.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return new String[] { this.x.getSqlRestrictionFragments(query)[0] //
			+ this.operation.getFragment() //
			+ this.y.getSqlRestrictionFragments(query)[0] };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public N handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final N value = (N) row.getObject(this.alias);

		return (N) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}
}

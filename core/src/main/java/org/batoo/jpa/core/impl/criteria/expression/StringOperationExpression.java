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
import java.text.MessageFormat;
import java.util.List;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.collect.Lists;

/**
 * Expression for string functions.
 * 
 * @author hceylan
 * @since $version
 */
public class StringOperationExpression extends AbstractExpression<String> {

	@SuppressWarnings("javadoc")
	public enum StringFunction {
		UPPER(false, "UPPER({0})", "upper({0})"),

		LOWER(false, "LOWER({0})", "lower{0}");

		private final boolean takesArguments;
		private final String sqlFragment;
		private final String jpqlFragment;

		private StringFunction(boolean takesArguments, String sqlfragment, String jpqlFragment) {
			this.takesArguments = takesArguments;
			this.sqlFragment = sqlfragment;
			this.jpqlFragment = jpqlFragment;
		}
	}

	private final AbstractExpression<?> inner;
	private final Object[] parameters;
	private String alias;
	private final StringFunction function;

	/**
	 * @param inner
	 *            the inner expression
	 * @param function
	 *            the string function
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public StringOperationExpression(Expression<String> inner, StringFunction function) {
		super(String.class);

		this.inner = (AbstractExpression<?>) inner;
		this.function = function;
		this.parameters = null;
	}

	/**
	 * @param inner
	 *            the inner expression
	 * @param function
	 *            the string function
	 * @param parameters
	 *            the parameters to the function
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public StringOperationExpression(Expression<String> inner, StringFunction function, Object... parameters) {
		super(String.class);

		this.inner = (AbstractExpression<?>) inner;
		this.function = function;
		this.parameters = parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		if (this.function.takesArguments) {
			final List<Object> arguments = Lists.newArrayList();
			arguments.add(this.inner.generateJpqlRestriction(query));
			for (final Object parameter : this.parameters) {
				arguments.add(parameter);
			}

			return MessageFormat.format(this.function.jpqlFragment, arguments.toArray());
		}

		return MessageFormat.format(this.function.jpqlFragment, this.inner.generateJpqlRestriction(query));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		if (selected && StringUtils.isBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	private String generateSqlRestriction(CriteriaQueryImpl<?> query) {
		if (this.function.takesArguments) {
			final List<Object> arguments = Lists.newArrayList();
			arguments.add(this.inner.getSqlRestrictionFragments(query)[0]);
			for (final Object parameter : this.parameters) {
				arguments.add(parameter);
			}

			return MessageFormat.format(this.function.sqlFragment, arguments.toArray());
		}

		return MessageFormat.format(this.function.sqlFragment, this.inner.getSqlRestrictionFragments(query)[0]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		return this.generateSqlRestriction(query) + " AS " + this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		return new String[] { this.generateSqlRestriction(query) };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (String) row.getObject(this.alias);
	}
}

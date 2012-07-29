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
import java.util.ArrayList;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Expression for string <code>concat</code> function.
 * 
 * @author hceylan
 * @since $version
 */
public class ConcatExpression extends AbstractExpression<String> {

	private final ArrayList<Expression<String>> arguments;
	private String alias;

	/**
	 * @param arguments
	 *            the argument expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ConcatExpression(Expression<String>... arguments) {
		super(String.class);

		this.arguments = Lists.newArrayList(arguments);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final CriteriaQueryImpl<?> query) {
		final String expressions = Joiner.on(", ").join(Lists.transform(this.arguments, new Function<Expression<String>, String>() {

			@Override
			public String apply(Expression<String> input) {
				return ((AbstractExpression<String>) input).generateJpqlRestriction(query);
			}
		}));

		return "concat (" + expressions + ")";
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

	private String generateSqlRestriction(final CriteriaQueryImpl<?> query) {
		return Joiner.on(" || ").join(Lists.transform(this.arguments, new Function<Expression<String>, String>() {

			@Override
			public String apply(Expression<String> input) {
				return ((AbstractExpression<String>) input).getSqlRestrictionFragments(query)[0];
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (selected) {
			return this.generateSqlRestriction(query) + " AS " + this.alias;
		}

		return this.generateSqlRestriction(query);
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

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
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for trim function.
 * 
 * @author hceylan
 * @since $version
 */
public class LocateExpression extends AbstractExpression<Integer> {

	private String alias;
	private final AbstractExpression<String> find;
	private final AbstractExpression<String> in;
	private final AbstractExpression<Integer> start;

	/**
	 * @param find
	 *            the expression to find
	 * @param in
	 *            the expression to search in
	 * @param start
	 *            the start index
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public LocateExpression(Expression<String> find, Expression<String> in, Expression<Integer> start) {
		super(Integer.class);

		this.find = (AbstractExpression<String>) find;
		this.in = (AbstractExpression<String>) in;
		this.start = (AbstractExpression<Integer>) start;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("locate(");

		builder.append(this.find.generateJpqlRestriction(query));
		builder.append(", ").append(this.in.generateJpqlRestriction(query));

		if (this.start != null) {
			builder.append(", ").append(this.start.generateJpqlRestriction(query));
		}

		return builder.append(")").toString();
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("LOCATE(");

		builder.append(this.find.getSqlRestrictionFragments(query)[0]);
		builder.append(", ").append(this.in.getSqlRestrictionFragments(query)[0]);

		if (this.start != null) {
			builder.append(", ").append(this.start.getSqlRestrictionFragments(query)[0]);
		}

		return new String[] { builder.append(")").toString() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (Integer) row.getObject(this.alias);
	}
}

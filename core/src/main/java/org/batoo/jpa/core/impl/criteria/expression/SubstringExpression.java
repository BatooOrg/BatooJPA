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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for substring function.
 * 
 * @author hceylan
 * @since $version
 */
public class SubstringExpression extends AbstractExpression<String> {

	private String alias;
	private final AbstractExpression<?> inner;
	private final AbstractExpression<? extends Number> start;
	private final AbstractExpression<? extends Number> end;

	/**
	 * @param inner
	 *            the inner expression
	 * @param start
	 *            the start expression
	 * @param end
	 *            the end expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SubstringExpression(AbstractExpression<?> inner, AbstractExpression<? extends Number> start, AbstractExpression<? extends Number> end) {
		super(String.class);

		this.inner = inner;
		this.start = start;
		this.end = end;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		if (this.end != null) {
			return "substring(" + this.inner.generateJpqlRestriction(query) + "," //
				+ this.start.generateJpqlRestriction(query) + ")";
		}

		return "substring(" + this.inner.generateJpqlRestriction(query) + "," //
			+ this.start.generateJpqlRestriction(query) + "," //
			+ this.end.generateJpqlRestriction(query) + ")";
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

		return this.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		if (this.end == null) {
			return new String[] { "SUBSTR(" + this.inner.getSqlRestrictionFragments(query)[0] + //
				"," + this.start.getSqlRestrictionFragments(query)[0] + ")" };
		}

		return new String[] { "SUBSTR(" + this.inner.getSqlRestrictionFragments(query)[0] + "," //
			+ this.start.getSqlRestrictionFragments(query)[0] + "," //
			+ this.end.getSqlRestrictionFragments(query)[0] + ")" };
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

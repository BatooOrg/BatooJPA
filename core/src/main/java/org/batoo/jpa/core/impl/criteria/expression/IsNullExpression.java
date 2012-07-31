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
import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for null checks.
 * 
 * @author hceylan
 * @since $version
 */
public class IsNullExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<?> inner;
	private final boolean not;
	private String alias;

	/**
	 * @param not
	 *            true if <code>not null</code>, false otherwise
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IsNullExpression(boolean not, AbstractExpression<?> inner) {
		super(Boolean.class);

		this.not = not;
		this.inner = inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractQueryImpl<?> query) {
		if (this.not) {
			return this.inner.generateJpqlRestriction(query) + " is not null";
		}

		return this.inner.generateJpqlRestriction(query) + " is null";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
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
	public String generateSqlSelect(AbstractQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(AbstractQueryImpl<?> query) {
		if (this.not) {
			return new String[] { this.inner.getSqlRestrictionFragments(query)[0] + " IS NOT NULL" };
		}

		return new String[] { this.inner.getSqlRestrictionFragments(query)[0] + " IS NULL" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (Boolean) row.getObject(this.alias);
	}
}

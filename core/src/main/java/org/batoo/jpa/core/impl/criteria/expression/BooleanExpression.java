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

import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Wrapper expression for boolean expressions.
 * 
 * @author hceylan
 * @since $version
 */
public class BooleanExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<Boolean> inner;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BooleanExpression(Expression<Boolean> inner) {
		super(Boolean.class);

		this.inner = (AbstractExpression<Boolean>) inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractQueryImpl<?> query) {
		return this.inner.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		return this.inner.generateJpqlSelect(null, selected);
	}

	/**
	 * Returns the SQL where fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the SQL select fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSqlRestriction(final AbstractQueryImpl<?> query) {
		return this.inner.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		return this.inner.generateSqlSelect(query, selected);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractQueryImpl<?> query) {
		if (this.inner != null) {
			return this.inner.getSqlRestrictionFragments(query);
		}

		return new String[] { this.generateSqlRestriction(query) };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.inner.handle(query, session, row);
	}
}

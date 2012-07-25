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

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class BooleanExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<Boolean> inner;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BooleanExpression() {
		super(Boolean.class);

		this.inner = null;
	}

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
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		return this.inner.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
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
	public String generateSqlRestriction(final CriteriaQueryImpl<?> query) {
		return this.inner.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		return this.inner.generateSqlSelect(query, selected);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		return this.inner.getSqlRestrictionFragments(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.inner.handle(query, session, row);
	}
}

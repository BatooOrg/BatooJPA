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
import javax.persistence.criteria.Root;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for empty
 * 
 * @author hceylan
 * @since $version
 */
public class IsEmptyExpression extends AbstractExpression<Boolean> {

	private final CollectionExpression<?, ?> inner;
	private final boolean not;

	/**
	 * @param inner
	 *            the inner expression
	 * @param not
	 *            if not empty
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IsEmptyExpression(Expression<?> inner, boolean not) {
		super(Boolean.class);

		this.inner = (CollectionExpression<?, ?>) inner;
		this.not = not;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		return this.inner.generateJpqlRestriction(query) + (this.not ? " is not empty" : " is empty");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection expressions cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection expressions cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		final CriteriaBuilderImpl cb = query.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		final Joinable rp = this.inner.getParentPath().getRootPath();

		final SubqueryImpl<Integer> s = query.subquery(Integer.class);

		final Root<?> r = s.from(rp.getEntity());
		r.join(this.inner.getMapping().getAttribute().getName());

		final PredicateImpl p = this.not ? cb.exists(s).not() : cb.exists(s);
		s.where(cb.equal(r, (AbstractExpression<?>) rp));

		s.select(cb.literal(1));

		return p.getSqlRestrictionFragments(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}
}

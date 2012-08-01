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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for size operations.
 * 
 * @param <C>
 *            the type of the collection.
 * 
 * @author hceylan
 * @since $version
 */
public class SizeExpression<C> extends AbstractExpression<Integer> {

	private final CollectionExpression<?, ?> collection;
	private String alias;

	/**
	 * @param collection
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SizeExpression(Expression<C> collection) {
		super(Integer.class);

		this.collection = (CollectionExpression<?, ?>) collection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		return "size(" + this.collection.generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
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
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		final CriteriaBuilderImpl cb = query.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		final Joinable rp = this.collection.getParentPath().getRootPath();

		final SubqueryImpl<Long> s = query.subquery(Long.class);

		final Root<?> r = s.from(rp.getEntity());
		r.join(this.collection.getMapping().getAttribute().getName());

		s.where(cb.equal(r, (AbstractExpression<?>) rp));

		s.select(cb.count(cb.literal(1)));

		return s.getSqlRestrictionFragments(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return Long.valueOf(row.getLong(this.alias)).intValue();
	}
}

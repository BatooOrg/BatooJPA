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
import java.text.MessageFormat;
import java.util.List;

import javax.persistence.criteria.Expression;

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ComparisonExpression extends BooleanExpression {

	private final Comparison comparison;
	private final AbstractExpression<?> x;
	private final AbstractExpression<?> y;
	private String alias;

	/**
	 * @param comparison
	 *            the comparison
	 * @param x
	 *            the left side expression
	 * @param y
	 *            the right side expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ComparisonExpression(Comparison comparison, Expression<?> x, Expression<?> y) {
		super();

		this.comparison = comparison;
		this.x = (AbstractExpression<?>) x;
		this.y = (AbstractExpression<?>) y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		return MessageFormat.format(this.comparison.getFragment(), this.x.generateJpqlRestriction(query), this.y.generateJpqlRestriction(query));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		return MessageFormat.format(this.comparison.getFragment(), this.x.generateJpqlSelect(null, selected), this.y.generateJpqlSelect(null, selected));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlRestriction(CriteriaQueryImpl<?> query) {
		final String[] left = this.x.getSqlRestrictionFragments(query);
		final String[] right = this.y.getSqlRestrictionFragments(query);

		final List<String> restrictions = Lists.newArrayList();

		for (int i = 0; i < left.length; i++) {
			restrictions.add(MessageFormat.format(this.comparison.getFragment(), left[i], right[i]));
		}

		return Joiner.on(" AND ").join(restrictions);
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
	public Boolean handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) {
		// TODO Auto-generated method stub
		return null;
	}
}

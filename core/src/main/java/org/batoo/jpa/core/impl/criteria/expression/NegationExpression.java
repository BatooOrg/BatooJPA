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
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.path.BasicPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The negation expression.
 * 
 * @param <N>
 *            the type of the number
 * 
 * @author hceylan
 * @since $version
 */
public class NegationExpression<N extends Number> extends AbstractExpression<N> {

	private final AbstractExpression<N> inner;
	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public NegationExpression(Expression<N> inner) {
		super((Class<N>) inner.getJavaType());

		this.inner = (AbstractExpression<N>) inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		return "-(" + this.inner.generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder("-");
		if (!(this.inner instanceof BasicPath)) {
			builder.append("(").append(this.inner.generateJpqlRestriction(query)).append(")");
		}
		else {
			builder.append(this.inner.generateJpqlRestriction(query));
		}

		if (StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (this.inner instanceof BasicPath) {
			if (selected) {
				return "-" + this.inner.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
			}

			return "-" + this.inner.getSqlRestrictionFragments(query)[0];
		}

		if (selected) {
			return "-" + this.inner.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
		}

		return "-" + this.inner.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		return new String[] { "-(" + this.inner.getSqlRestrictionFragments(query)[0] + ")" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public N handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final N value = (N) row.getObject(this.alias);

		return (N) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}
}

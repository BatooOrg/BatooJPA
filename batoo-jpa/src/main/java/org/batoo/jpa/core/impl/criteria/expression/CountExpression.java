/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for count function.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CountExpression extends AbstractExpression<Long> {

	private final AbstractExpression<?> inner;
	private final boolean distinct;

	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * @param distinct
	 *            if the count is distinct
	 * 
	 * @since 2.0.0
	 */
	public CountExpression(Expression<?> inner, boolean distinct) {
		super(Long.class);

		this.inner = (AbstractExpression<?>) inner;
		this.distinct = distinct;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("count(");

		if (this.distinct) {
			builder.append("distinct ");
		}

		builder.append(this.inner.generateJpqlRestriction(query));
		builder.append(")");

		return builder.toString();
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
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("COUNT(");

		if (this.distinct) {
			builder.append("DISTINCT ");
		}

		builder.append(this.inner.getSqlRestrictionFragments(query)[0]);
		builder.append(")");

		return new String[] { builder.toString() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Long handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return row.getLong(this.alias);
	}
}

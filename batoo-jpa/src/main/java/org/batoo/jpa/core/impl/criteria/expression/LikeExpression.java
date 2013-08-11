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
 * The expression for like operation.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class LikeExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<String> inner;
	private final AbstractExpression<String> pattern;
	private final AbstractExpression<Character> escape;
	private String alias;
	private final boolean not;

	/**
	 * @param inner
	 *            the inner expression
	 * @param pattern
	 *            the pattern expression
	 * @param escape
	 *            the escape expression
	 * @param not
	 *            true if not
	 * 
	 * @since 2.0.0
	 */
	public LikeExpression(Expression<String> inner, Expression<String> pattern, Expression<Character> escape, boolean not) {
		super(Boolean.class);

		this.inner = (AbstractExpression<String>) inner;
		this.pattern = (AbstractExpression<String>) pattern;
		this.escape = (AbstractExpression<Character>) escape;
		this.not = not;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.inner.generateJpqlRestriction(query));
		if (this.not) {
			builder.append(" not");
		}

		builder.append(" like ");

		builder.append(this.pattern.generateJpqlRestriction(query));
		if (this.escape != null) {
			builder.append(" escape ");
			builder.append(this.escape.generateJpqlRestriction(query));
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
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
		final StringBuilder builder = new StringBuilder();

		builder.append(this.inner.getSqlRestrictionFragments(query)[0]);
		if (this.not) {
			builder.append(" NOT");
		}

		builder.append(" LIKE ");

		builder.append(this.pattern.getSqlRestrictionFragments(query)[0]);
		if (this.escape != null) {
			builder.append(query.getJdbcAdaptor().applyLikeEscape(this.escape.getSqlRestrictionFragments(query)[0]));
		}

		return new String[] { builder.toString() };
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

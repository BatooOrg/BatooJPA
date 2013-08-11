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
import org.batoo.jpa.core.impl.criteria.path.BasicPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The negation expression.
 * 
 * @param <N>
 *            the type of the number
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class NegationExpression<N extends Number> extends AbstractExpression<N> {

	private final AbstractExpression<N> inner;
	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since 2.0.0
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
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
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
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
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

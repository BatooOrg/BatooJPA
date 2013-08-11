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
import java.text.MessageFormat;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.jdbc.NumericFunctionType;

/**
 * The expression for numeric functions
 * 
 * @param <N>
 *            the type of the expression
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class NumericFunctionExpression<N extends Number> extends AbstractExpression<N> {

	private final NumericFunctionType type;
	private final AbstractExpression<?> x;
	private final AbstractExpression<Integer> y;
	private String alias;

	/**
	 * @param type
	 *            the type of the function
	 * @param x
	 *            the first parameter
	 * @param y
	 *            the optional second parameter
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public NumericFunctionExpression(NumericFunctionType type, Expression<?> x, Expression<Integer> y) {
		super((Class<N>) (Number.class.isAssignableFrom(x.getJavaType()) ? x.getJavaType() : Integer.class));

		this.type = type;
		this.x = (AbstractExpression<?>) x;
		this.y = (AbstractExpression<Integer>) y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final String xExpr = this.x.generateJpqlRestriction(query);
		final String yExpr = this.y != null ? this.y.generateJpqlRestriction(query) : null;

		return MessageFormat.format(this.type.getJpqlFragment(), xExpr, yExpr);
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
		final String xExpr = this.x.getSqlRestrictionFragments(query)[0];
		final String yExpr = this.y != null ? this.y.getSqlRestrictionFragments(query)[0] : null;

		return new String[] { MessageFormat.format(query.getJdbcAdaptor().getNumericFunctionTemplate(this.type), xExpr, yExpr) };

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public N handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (N) row.getObject(this.alias);
	}
}

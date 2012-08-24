/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
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
 * Expression for substring function.
 * 
 * @author hceylan
 * @since $version
 */
public class SubstringExpression extends AbstractExpression<String> {

	private String alias;
	private final AbstractExpression<String> inner;
	private final AbstractExpression<Integer> start;
	private final AbstractExpression<Integer> end;

	/**
	 * @param inner
	 *            the inner expression
	 * @param start
	 *            the start expression
	 * @param end
	 *            the end expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SubstringExpression(Expression<String> inner, Expression<Integer> start, Expression<Integer> end) {
		super(String.class);

		this.inner = (AbstractExpression<String>) inner;
		this.start = (AbstractExpression<Integer>) start;
		this.end = (AbstractExpression<Integer>) end;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		if (this.end != null) {
			return "substring(" + this.inner.generateJpqlRestriction(query) + "," //
				+ this.start.generateJpqlRestriction(query) + ")";
		}

		return "substring(" + this.inner.generateJpqlRestriction(query) + "," //
			+ this.start.generateJpqlRestriction(query) + "," //
			+ this.end.generateJpqlRestriction(query) + ")";
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
		String innerFragment = this.inner.getSqlRestrictionFragments(query)[0];
		String startFragment = this.start.getSqlRestrictionFragments(query)[0];
		String endFragment = this.end != null ? this.end.getSqlRestrictionFragments(query)[0] : null;

		return new String[] { query.getJdbcAdaptor().applySubStr(innerFragment, startFragment, endFragment) };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (String) row.getObject(this.alias);
	}
}

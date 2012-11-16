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
 * Expression for trim function.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class LocateExpression extends AbstractExpression<Integer> {

	private String alias;
	private final AbstractExpression<String> find;
	private final AbstractExpression<String> in;
	private final AbstractExpression<Integer> start;

	/**
	 * @param find
	 *            the expression to find
	 * @param in
	 *            the expression to search in
	 * @param start
	 *            the start index
	 * 
	 * @since 2.0.0
	 */
	public LocateExpression(Expression<String> find, Expression<String> in, Expression<Integer> start) {
		super(Integer.class);

		this.find = (AbstractExpression<String>) find;
		this.in = (AbstractExpression<String>) in;
		this.start = (AbstractExpression<Integer>) start;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("locate(");

		builder.append(this.find.generateJpqlRestriction(query));
		builder.append(", ").append(this.in.generateJpqlRestriction(query));

		if (this.start != null) {
			builder.append(", ").append(this.start.generateJpqlRestriction(query));
		}

		return builder.append(")").toString();
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
		final StringBuilder builder = new StringBuilder("LOCATE(");

		builder.append(this.find.getSqlRestrictionFragments(query)[0]);
		builder.append(", ").append(this.in.getSqlRestrictionFragments(query)[0]);

		if (this.start != null) {
			builder.append(", ").append(this.start.getSqlRestrictionFragments(query)[0]);
		}

		return new String[] { builder.append(")").toString() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (Integer) row.getObject(this.alias);
	}
}

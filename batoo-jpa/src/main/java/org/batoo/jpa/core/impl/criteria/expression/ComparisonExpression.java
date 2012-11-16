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
import java.text.MessageFormat;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Expression for comparisons.
 * 
 * @author hceylan
 * @since $version
 */
public class ComparisonExpression extends AbstractExpression<Boolean> {

	/**
	 * The comparison types
	 * 
	 * @author hceylan
	 * @since $version
	 */
	@SuppressWarnings("javadoc")
	public enum Comparison {

		EQUAL("{0} = {1}"),

		NOT_EQUAL("{0} <> {1}"),

		LESS("{0} < {1}"),

		LESS_OR_EQUAL("{0} <= {1}"),

		GREATER("{0} > {1}"),

		GREATER_OR_EQUAL("{0} >= {1}"),

		BETWEEN("{0} BETWEEN {1} AND {2}");

		private final String fragment;

		Comparison(String fragment) {
			this.fragment = fragment;
		}
	}

	private final Comparison comparison;
	private final AbstractExpression<?> x;
	private final AbstractExpression<?> y;
	private final AbstractExpression<?> z;
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
	 */
	public ComparisonExpression(Comparison comparison, Expression<?> x, Expression<?> y) {
		this(comparison, x, y, null);
	}

	/**
	 * @param comparison
	 *            the comparison
	 * @param x
	 *            the left side expression
	 * @param y
	 *            the first right side expression
	 * @param z
	 *            the second right side expression
	 * 
	 * @since $version
	 */
	public ComparisonExpression(Comparison comparison, Expression<?> x, Expression<?> y, Expression<?> z) {
		super(Boolean.class);

		this.comparison = comparison;

		if (x instanceof ParameterExpression) {
			this.x = (AbstractExpression<?>) y;
			this.y = (AbstractExpression<?>) x;
		}
		else {
			this.x = (AbstractExpression<?>) x;
			this.y = (AbstractExpression<?>) y;
		}

		this.z = (AbstractExpression<?>) z;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		if (this.z != null) {
			return MessageFormat.format(this.comparison.fragment, //
				this.x.generateJpqlRestriction(query), //
				this.y.generateJpqlRestriction(query), //
				this.z.generateJpqlRestriction(query));
		}

		return MessageFormat.format(this.comparison.fragment, //
			this.x.generateJpqlRestriction(query), //
			this.y.generateJpqlRestriction(query));
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
		final String[] left = this.x.getSqlRestrictionFragments(query);
		final String[] right1 = this.y.getSqlRestrictionFragments(query);
		final String[] right2 = this.z != null ? this.z.getSqlRestrictionFragments(query) : null;

		final List<String> restrictions = Lists.newArrayList();

		for (int i = 0; i < left.length; i++) {
			if (this.z != null) {
				restrictions.add(MessageFormat.format(this.comparison.fragment, left[i], right1[i], right2[i]));
			}
			else {
				restrictions.add(MessageFormat.format(this.comparison.fragment, left[i], right1[i]));
			}
		}

		return new String[] { Joiner.on(" AND ").join(restrictions) };
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

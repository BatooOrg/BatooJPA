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

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for aggregate functions
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @since 2.0.0
 */
public class AggregationExpression<T> extends AbstractExpression<T> {

	@SuppressWarnings("javadoc")
	public enum AggregationFunctionType {
		AVG("AVG({0})", "avg({0})"),

		SUM("SUM({0})", "sum({0})"),

		MIN("MIN({0})", "min({0})"),

		MAX("MAX({0})", "max({0})");

		private final String sqlFragment;
		private final String jpqlFragment;

		private AggregationFunctionType(String sqlfragment, String jpqlFragment) {
			this.sqlFragment = sqlfragment;
			this.jpqlFragment = jpqlFragment;
		}
	}

	private final AggregationFunctionType type;
	private final AbstractExpression<?> x;
	private String alias;

	/**
	 * @param type
	 *            the type of the function
	 * @param x
	 *            the first parameter
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public AggregationExpression(AggregationFunctionType type, Expression<?> x) {
		super((Class<T>) x.getJavaType());

		this.type = type;
		this.x = (AbstractExpression<?>) x;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return MessageFormat.format(this.type.jpqlFragment, this.x.generateJpqlRestriction(query));
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
		return new String[] { MessageFormat.format(this.type.sqlFragment, this.x.getSqlRestrictionFragments(query)[0]) };

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		if (this.getJavaType() == Long.class) {
			return (T) (Long) row.getLong(this.alias);
		}

		if (this.getJavaType() == Double.class) {
			return (T) (Double) row.getDouble(this.alias);
		}

		return (T) row.getObject(this.alias);
	}
}

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
 * Artithmetic operation expression.
 * 
 * @param <N>
 *            the type of the expression
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ArithmeticExression<N extends Number> extends AbstractExpression<N> {

	/**
	 * The types of the arithmetic operations.
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("javadoc")
	public enum ArithmeticOperation {
		ADD(" + "),

		SUBTRACT(" - "),

		MULTIPLY(" * "),

		DIVIDE(" / ");

		private final String fragment;

		private ArithmeticOperation(String fragment) {
			this.fragment = fragment;
		}

		public String getFragment() {
			return this.fragment;
		}
	}

	private final ArithmeticOperation operation;
	private final AbstractExpression<N> x;
	private final AbstractExpression<N> y;
	private String alias;

	/**
	 * @param operation
	 *            the operation
	 * @param x
	 *            the left side expression
	 * @param y
	 *            the right side expression
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public ArithmeticExression(ArithmeticOperation operation, Expression<? extends N> x, Expression<? extends N> y) {
		super((Class<N>) x.getJavaType());

		this.operation = operation;
		this.x = (AbstractExpression<N>) x;
		this.y = (AbstractExpression<N>) y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.x.generateJpqlRestriction(query) + this.operation.getFragment() + this.y.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.x.generateJpqlRestriction(query));
		builder.append(this.operation.getFragment());
		builder.append(this.y.generateJpqlRestriction(query));

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
		return new String[] { this.x.getSqlRestrictionFragments(query)[0] //
			+ this.operation.getFragment() //
			+ this.y.getSqlRestrictionFragments(query)[0] };
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

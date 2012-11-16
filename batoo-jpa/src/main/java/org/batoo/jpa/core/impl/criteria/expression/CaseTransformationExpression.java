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
 * Expression for string <code>upper</code> and <code>lower</code> functions.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CaseTransformationExpression extends AbstractExpression<String> {

	@SuppressWarnings("javadoc")
	public enum CaseTransformationType {
		UPPER("UPPER({0})", "upper({0})"),

		LOWER("LOWER({0})", "lower{0}");

		private final String sqlFragment;
		private final String jpqlFragment;

		private CaseTransformationType(String sqlfragment, String jpqlFragment) {
			this.sqlFragment = sqlfragment;
			this.jpqlFragment = jpqlFragment;
		}
	}

	private final AbstractExpression<?> inner;
	private String alias;
	private final CaseTransformationType function;

	/**
	 * @param inner
	 *            the inner expression
	 * @param function
	 *            the string function
	 * 
	 * @since 2.0.0
	 */
	public CaseTransformationExpression(Expression<String> inner, CaseTransformationType function) {
		super(String.class);

		this.inner = (AbstractExpression<?>) inner;
		this.function = function;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return MessageFormat.format(this.function.jpqlFragment, this.inner.generateJpqlRestriction(query));
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

	private String generateSqlRestriction(BaseQueryImpl<?> query) {
		return MessageFormat.format(this.function.sqlFragment, this.inner.getSqlRestrictionFragments(query)[0]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		if (selected) {
			return this.generateSqlRestriction(query) + " AS " + this.alias;
		}

		return this.generateSqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return new String[] { this.generateSqlRestriction(query) };
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

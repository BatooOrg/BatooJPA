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
import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder.Trimspec;
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
 * @since $version
 */
public class TrimExpression extends AbstractExpression<String> {

	private final Trimspec trimspec;
	private final AbstractExpression<Character> trimChar;
	private final AbstractExpression<String> inner;

	private String alias;

	/**
	 * @param trimspec
	 *            the trim spec expression
	 * @param trimChar
	 *            the trim chararacter expression
	 * @param inner
	 *            the inner expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TrimExpression(Trimspec trimspec, Expression<Character> trimChar, Expression<String> inner) {
		super(String.class);

		this.trimspec = trimspec;
		this.trimChar = (AbstractExpression<Character>) trimChar;
		this.inner = (AbstractExpression<String>) inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder("trim(");

		if (this.trimspec != null) {
			builder.append(this.trimspec.toString().toLowerCase(Locale.ENGLISH)).append(" ");
		}

		if (this.trimChar != null) {
			builder.append(this.trimChar.generateJpqlRestriction(query)).append(" ");
		}

		if ((this.trimspec != null) || (this.trimChar != null)) {
			builder.append("from ");
		}

		return builder//
		.append(this.inner.generateJpqlRestriction(query))//
		.append(")").toString();
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
		final String argument = this.inner.getSqlRestrictionFragments(query)[0];

		return new String[] { query.getJdbcAdaptor().applyTrim(this.trimspec,
			this.trimChar != null ? this.trimChar.getSqlRestrictionFragments(query)[0] : null, argument) };
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

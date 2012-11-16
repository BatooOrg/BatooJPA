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
import java.util.ArrayList;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Expression for string <code>concat</code> function.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ConcatExpression extends AbstractExpression<String> {

	private final ArrayList<Expression<String>> arguments;
	private String alias;

	/**
	 * @param arguments
	 *            the argument expressions
	 * 
	 * @since 2.0.0
	 */
	public ConcatExpression(Expression<String>... arguments) {
		super(String.class);

		this.arguments = Lists.newArrayList(arguments);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		final String expressions = Joiner.on(", ").join(Lists.transform(this.arguments, new Function<Expression<String>, String>() {

			@Override
			public String apply(Expression<String> input) {
				return ((AbstractExpression<String>) input).generateJpqlRestriction(query);
			}
		}));

		return "concat (" + expressions + ")";
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

	private String generateSqlRestriction(final BaseQueryImpl<?> query) {
		return query.getJdbcAdaptor().applyConcat(Lists.transform(this.arguments, new Function<Expression<String>, String>() {

			@Override
			public String apply(Expression<String> input) {
				return ((AbstractExpression<String>) input).getSqlRestrictionFragments(query)[0];
			}
		}));
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

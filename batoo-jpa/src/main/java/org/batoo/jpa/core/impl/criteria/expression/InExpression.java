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
import java.util.Collection;

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
 * Expression for in predicates.
 * 
 * @author hceylan
 * @since $version
 */
public class InExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<?> inner;
	private final ArrayList<AbstractExpression<?>> values = Lists.newArrayList();
	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * @param values
	 *            the values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public InExpression(AbstractExpression<?> inner, Expression<?>[] values) {
		super(Boolean.class);

		this.inner = inner;
		for (final Expression<?> expression : values) {
			this.values.add((AbstractExpression<?>) expression);
		}
	}

	/**
	 * @param inner
	 *            the inner expression
	 * @param values
	 *            the values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public InExpression(Expression<?> inner, Collection<?> values) {
		super(Boolean.class);

		this.inner = (AbstractExpression<?>) inner;
		for (final Object value : values) {
			if (value instanceof AbstractExpression) {
				this.values.add((AbstractExpression<?>) value);
			}
			else {
				this.values.add(new EntityConstantExpression<Object>(null, value));
			}
		}
	}

	/**
	 * Adds the expression to the list of values.
	 * 
	 * @param expression
	 *            the expression to add
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void add(AbstractExpression<?> expression) {
		this.values.add(expression);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		final String values = Joiner.on(", ").join(Lists.transform(this.values, new Function<AbstractExpression<?>, String>() {

			@Override
			public String apply(AbstractExpression<?> input) {
				return input.generateJpqlRestriction(query);
			}
		}));

		return this.inner.generateJpqlRestriction(query) + " in (" + values + ")";
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
	public String[] getSqlRestrictionFragments(final BaseQueryImpl<?> query) {
		final String inner = this.inner.getSqlRestrictionFragments(query)[0];

		final String values = Joiner.on(", ").join(Lists.transform(this.values, new Function<AbstractExpression<?>, String>() {

			@Override
			public String apply(AbstractExpression<?> input) {
				return input.getSqlRestrictionFragments(query)[0];
			}
		}));

		return new String[] { inner + " IN (" + values + ")" };
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

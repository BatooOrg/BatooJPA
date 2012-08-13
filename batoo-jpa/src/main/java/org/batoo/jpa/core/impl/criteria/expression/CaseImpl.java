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
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.Case;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link Case}.
 * 
 * @param <T>
 *            the type of the case expression
 * 
 * @author hceylan
 * @since $version
 */
public class CaseImpl<T> extends AbstractExpression<T> implements Case<T> {

	private final List<Pair<Expression<Boolean>, Expression<? extends T>>> conditions = Lists.newArrayList();
	private Expression<? extends T> otherwise;
	private String alias;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public CaseImpl() {
		super((Class<T>) Object.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<Boolean>, Expression<? extends T>>, String>() {

				@Override
				public String apply(Pair<Expression<Boolean>, Expression<? extends T>> input) {
					final AbstractExpression<Boolean> when = (AbstractExpression<Boolean>) input.getFirst();
					final AbstractExpression<? extends T> then = (AbstractExpression<? extends T>) input.getSecond();

					return "when " + when.generateJpqlRestriction(query) + " then " + then.generateJpqlRestriction(query);
				}
			}));

		final String otherwise = "\n\telse " + ((AbstractExpression<? extends T>) this.otherwise).generateJpqlRestriction(query);

		return "case\n\t" + whens + otherwise + "\nend";
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
	public String[] getSqlRestrictionFragments(final BaseQueryImpl<?> query) {
		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<Boolean>, Expression<? extends T>>, String>() {

				@Override
				public String apply(Pair<Expression<Boolean>, Expression<? extends T>> input) {
					final AbstractExpression<Boolean> when = (AbstractExpression<Boolean>) input.getFirst();
					final AbstractExpression<? extends T> then = (AbstractExpression<? extends T>) input.getSecond();

					return "WHEN " + when.getSqlRestrictionFragments(query)[0] + " THEN " + then.getSqlRestrictionFragments(query)[0];
				}
			}));

		final String otherwise = "\n\tELSE " + ((AbstractExpression<? extends T>) this.otherwise).getSqlRestrictionFragments(query)[0];

		return new String[] { "CASE\n\t" + whens + otherwise + "\nEND" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (T) row.getObject(this.alias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<T> otherwise(Expression<? extends T> result) {
		this.otherwise = result;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<T> otherwise(T result) {
		return this.otherwise(new ConstantExpression<T>(null, result));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Case<T> when(Expression<Boolean> condition, Expression<? extends T> result) {
		this.conditions.add(new Pair<Expression<Boolean>, Expression<? extends T>>(condition, result));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Case<T> when(Expression<Boolean> condition, T result) {
		return this.when(condition, new ConstantExpression<T>(null, result));
	}
}

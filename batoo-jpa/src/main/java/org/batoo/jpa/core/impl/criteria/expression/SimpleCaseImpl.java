/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

import javax.persistence.criteria.CriteriaBuilder.SimpleCase;
import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.common.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link SimpleCase}.
 * 
 * @param <C>
 *            the type of the case expression
 * @param <R>
 *            the type of the result expression
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SimpleCaseImpl<C, R> extends AbstractExpression<R> implements SimpleCase<C, R> {

	private final Expression<? extends C> inner;
	private final List<Pair<Expression<C>, Expression<? extends R>>> conditions = Lists.newArrayList();
	private Expression<? extends R> otherwise;
	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public SimpleCaseImpl(Expression<? extends C> inner) {
		super((Class<R>) Object.class);

		this.inner = inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<C>, Expression<? extends R>>, String>() {

				@Override
				public String apply(Pair<Expression<C>, Expression<? extends R>> input) {
					final AbstractExpression<C> when = (AbstractExpression<C>) input.getFirst();
					final AbstractExpression<? extends R> then = (AbstractExpression<? extends R>) input.getSecond();

					return "when " + when.generateJpqlRestriction(query) + " then " + then.generateJpqlRestriction(query);
				}
			}));

		final String otherwise = "\n\telse " + ((AbstractExpression<? extends R>) this.otherwise).generateJpqlRestriction(query);

		return "case " + ((AbstractExpression<? extends C>) this.inner).generateJpqlRestriction(query) + "\n\t" + whens + otherwise + "\nend";
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
	@SuppressWarnings("unchecked")
	public Expression<C> getExpression() {
		return (Expression<C>) this.inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(final BaseQueryImpl<?> query) {
		final String innerFragment = ((AbstractExpression<? extends C>) this.inner).getSqlRestrictionFragments(query)[0];

		final String whens = Joiner.on("\n\t").join(Lists.transform(this.conditions, //
			new Function<Pair<Expression<C>, Expression<? extends R>>, String>() {

				@Override
				public String apply(Pair<Expression<C>, Expression<? extends R>> input) {
					final String conditionFragment = ((AbstractExpression<C>) input.getFirst()).getSqlRestrictionFragments(query)[0];
					final String resultFragment = ((AbstractExpression<? extends R>) input.getSecond()).getSqlRestrictionFragments(query)[0];

					return "WHEN " + innerFragment + " = " + conditionFragment + " THEN " + resultFragment;
				}
			}));

		final String otherwise = "\n\tELSE " + ((AbstractExpression<? extends R>) this.otherwise).getSqlRestrictionFragments(query)[0];

		return new String[] { "CASE\n\t" + whens + otherwise + "\nEND" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public R handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (R) row.getObject(this.alias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<R> otherwise(Expression<? extends R> result) {
		this.otherwise = result;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<R> otherwise(R result) {
		this.otherwise(new EntityConstantExpression<R>(null, result));

		return this;
	}

	/**
	 * Add a when/then clause to the case expression.
	 * 
	 * @param condition
	 *            "when" condition
	 * @param result
	 *            "then" result expression
	 * @return simple case expression
	 * 
	 * @since 2.0.0
	 */
	public SimpleCaseImpl<C, R> when(AbstractExpression<C> condition, AbstractExpression<R> result) {
		this.conditions.add(new Pair<Expression<C>, Expression<? extends R>>(condition, result));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SimpleCase<C, R> when(C condition, Expression<? extends R> result) {
		this.conditions.add(new Pair<Expression<C>, Expression<? extends R>>(new EntityConstantExpression<C>(null, condition), result));

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SimpleCase<C, R> when(C condition, R result) {
		return this.when(condition, new EntityConstantExpression<R>(null, result));
	}
}

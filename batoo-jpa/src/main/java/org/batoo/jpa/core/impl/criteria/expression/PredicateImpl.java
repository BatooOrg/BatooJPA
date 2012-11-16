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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link Predicate}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PredicateImpl extends BooleanExpression implements Predicate {

	private final BooleanOperator operator;
	private final boolean negated;
	private final List<BooleanExpression> expressions = Lists.newArrayList();
	private String alias;

	/**
	 * @param expression
	 *            the expressions
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public PredicateImpl(AbstractExpression<Boolean> expression) {
		this(false, BooleanOperator.AND, expression);
	}

	/**
	 * @param expressions
	 *            the expressions
	 * 
	 * @since 2.0.0
	 */
	public PredicateImpl(AbstractExpression<Boolean>... expressions) {
		this(false, BooleanOperator.AND, expressions);
	}

	/**
	 * @param negated
	 *            if negated
	 * @param operator
	 *            the operator
	 * @param expressions
	 *            the expressions
	 * 
	 * @since 2.0.0
	 */
	public PredicateImpl(boolean negated, BooleanOperator operator, Expression<Boolean>... expressions) {
		super(null);

		this.negated = negated;
		this.operator = operator;

		for (final Expression<Boolean> expression : expressions) {
			if (expression instanceof BooleanExpression) {
				this.expressions.add((BooleanExpression) expression);
			}
			else {
				this.expressions.add(new BooleanExpression(expression));
			}
		}
	}

	/**
	 * @param negated
	 *            if negated
	 * @param operator
	 *            the operator
	 * @param predicates
	 *            the predicates
	 * 
	 * @since 2.0.0
	 */
	public PredicateImpl(boolean negated, BooleanOperator operator, Predicate... predicates) {
		super(null);

		this.negated = negated;
		this.operator = operator;

		for (final Predicate predicate : predicates) {
			this.expressions.add((PredicateImpl) predicate);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final BaseQueryImpl<?> query) {
		String predicates = Joiner.on(" " + this.operator.name() + " ").join(
			Lists.transform(this.expressions, new Function<AbstractExpression<Boolean>, String>() {

				@Override
				public String apply(AbstractExpression<Boolean> input) {
					return input.generateJpqlRestriction(query);
				}
			}));

		if (StringUtils.isBlank(predicates)) {
			predicates = "true";
		}

		if (this.negated) {
			return "not (" + predicates + ")";
		}

		return predicates;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.alias;
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlRestriction(final BaseQueryImpl<?> query) {
		String predicates = Joiner.on(" " + this.operator.name() + " ").join(Lists.transform(this.expressions, new Function<BooleanExpression, String>() {

			@Override
			public String apply(BooleanExpression input) {
				return input.generateSqlRestriction(query);
			}
		}));

		if (StringUtils.isBlank(predicates)) {
			predicates = "TRUE";
		}

		if (this.negated) {
			return "NOT (" + predicates + ")";
		}

		return predicates;
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
	public List<Expression<Boolean>> getExpressions() {
		final List<Expression<Boolean>> expressions = Lists.newArrayList();
		expressions.addAll(this.expressions);

		return expressions;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BooleanOperator getOperator() {
		return this.operator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return row.getBoolean(this.alias);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isNegated() {
		return this.negated;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PredicateImpl not() {
		return new PredicateImpl(true, this.operator, this.expressions.toArray(new Expression[this.expressions.size()]));
	}
}

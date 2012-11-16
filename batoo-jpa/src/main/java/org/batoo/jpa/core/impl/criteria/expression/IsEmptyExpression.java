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
import javax.persistence.criteria.Root;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for empty
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class IsEmptyExpression extends AbstractExpression<Boolean> {

	private final CollectionExpression<?, ?> inner;
	private final boolean not;

	/**
	 * @param inner
	 *            the inner expression
	 * @param not
	 *            if not empty
	 * 
	 * @since 2.0.0
	 */
	public IsEmptyExpression(Expression<?> inner, boolean not) {
		super(Boolean.class);

		this.inner = (CollectionExpression<?, ?>) inner;
		this.not = not;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.inner.generateJpqlRestriction(query) + (this.not ? " is not empty" : " is empty");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection expressions cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection expressions cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final CriteriaBuilderImpl cb = query.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		final Joinable rp = this.inner.getParentPath().getRootPath();

		final SubqueryImpl<Integer> s = query.subquery(Integer.class);

		final Root<?> r = s.from(rp.getEntity());
		r.join(this.inner.getMapping().getAttribute().getName());

		final PredicateImpl p = this.not ? cb.exists(s).not() : cb.exists(s);
		s.where(cb.equal(r, (AbstractExpression<?>) rp));

		s.select(cb.literal(1));

		return p.getSqlRestrictionFragments(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}
}

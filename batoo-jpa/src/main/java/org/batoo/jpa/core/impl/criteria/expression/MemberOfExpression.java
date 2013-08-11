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
import java.util.Collection;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractPluralJoin;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;

/**
 * Expression for empty
 * 
 * @param <C>
 *            the type of the collection
 * @param <E>
 *            the type of the elemenet
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MemberOfExpression<C extends Collection<E>, E> extends AbstractExpression<Boolean> {

	private final boolean not;
	private final CollectionExpression<?, ?> values;
	private final AbstractExpression<?> value;

	/**
	 * @param not
	 *            if not empty
	 * @param value
	 *            the value expression
	 * @param values
	 *            the values expression
	 * 
	 * @since 2.0.0
	 */
	public MemberOfExpression(boolean not, Expression<E> value, Expression<C> values) {
		super(Boolean.class);

		this.not = not;
		this.values = (CollectionExpression<?, ?>) values;
		this.value = (AbstractExpression<?>) value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.value.generateJpqlRestriction(query) + (this.not ? " not member of " : " member of ") + this.values.generateJpqlRestriction(query);
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
	@SuppressWarnings("unchecked")
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final CriteriaBuilderImpl cb = query.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		final Joinable rp = this.values.getParentPath().getRootPath();

		final SubqueryImpl<Object> s = query.subquery(Object.class);

		final Root<?> r = s.from(rp.getEntity());
		final PluralAttributeImpl<?, C, E> attribute = (PluralAttributeImpl<?, C, E>) this.values.getMapping().getAttribute();
		final AbstractPluralJoin<?, C, E> j = (AbstractPluralJoin<?, C, E>) r.join(attribute.getName());

		if (attribute.getElementType() instanceof EntityTypeImpl) {
			final EntityTypeImpl<E> entity = (EntityTypeImpl<E>) attribute.getElementType();
			final String idName = entity.getIdMapping().getAttribute().getName();
			s.select(j.get(idName));
		}
		else {
			s.select((Expression<Object>) j);
		}

		s.where(cb.equal(r, (AbstractExpression<?>) rp));

		return new String[] { this.value.getSqlRestrictionFragments(query)[0] + " IN " + s.getSqlRestrictionFragments(query)[0] };
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

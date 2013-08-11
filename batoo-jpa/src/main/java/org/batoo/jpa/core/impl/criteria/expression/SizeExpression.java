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
import java.util.LinkedList;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.EntityMapping;

import com.google.common.collect.Lists;

/**
 * Expression for size operations.
 * 
 * @param <C>
 *            the type of the collection.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SizeExpression<C> extends AbstractExpression<Integer> {

	private final CollectionExpression<?, ?> collection;
	private String alias;

	/**
	 * @param collection
	 *            the inner expression
	 * 
	 * @since 2.0.0
	 */
	public SizeExpression(Expression<C> collection) {
		super(Integer.class);

		this.collection = (CollectionExpression<?, ?>) collection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return "size(" + this.collection.generateJpqlRestriction(query) + ")";
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
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final CriteriaBuilderImpl cb = query.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		final Joinable rp = this.collection.getParentPath().getRootPath();

		final LinkedList<AbstractMapping<?, ?, ?>> chain = Lists.newLinkedList();

		AbstractMapping<?, ?, ?> mapping = this.collection.getMapping();
		while (!(mapping instanceof EntityMapping)) {
			chain.addFirst(mapping);

			mapping = mapping.getParent();
		}

		final SubqueryImpl<Long> s = query.subquery(Long.class);
		final RootImpl<?> r = s.from(rp.getEntity());

		AbstractFrom<?, ?> from = r;
		for (final AbstractMapping<?, ?, ?> chainMember : chain) {
			from = from.join(chainMember.getName());
		}

		s.where(cb.equal(r, (AbstractExpression<?>) rp));

		s.select(cb.count(cb.literal(1)));

		return s.getSqlRestrictionFragments(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return Long.valueOf(row.getLong(this.alias)).intValue();
	}
}

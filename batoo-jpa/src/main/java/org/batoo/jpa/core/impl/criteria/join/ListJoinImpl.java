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
package org.batoo.jpa.core.impl.criteria.join;

import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.metamodel.ListAttribute;

import org.batoo.jpa.core.impl.criteria.expression.IndexExpression;
import org.batoo.jpa.core.impl.jdbc.OrderColumn;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

/**
 * Implementation of {@link ListJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ListJoinImpl<Z, E> extends AbstractPluralJoin<Z, List<E>, E> implements ListJoin<Z, E> {

	private final PluralMapping<? super Z, List<E>, E> mapping;

	/**
	 * @param parent
	 *            the parent
	 * @param mapping
	 *            the mapping
	 * @param jointType
	 *            the join type
	 * 
	 * @since 2.0.0
	 */
	public ListJoinImpl(AbstractFrom<?, Z> parent, PluralMapping<? super Z, List<E>, E> mapping, JoinType jointType) {
		super(parent, mapping, jointType);

		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ListAttribute<? super Z, E> getModel() {
		return (ListAttribute<? super Z, E>) this.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> index() {
		final OrderColumn orderColumn = this.mapping.getOrderColumn();

		if (orderColumn == null) {
			throw new IllegalArgumentException("List join does not have an order column");
		}

		return new IndexExpression(this, orderColumn);
	}
}

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

import java.util.Collection;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.CollectionAttribute;

import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

/**
 * Implementation of {@link CollectionJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CollectionJoinImpl<Z, E> extends AbstractPluralJoin<Z, Collection<E>, E> implements CollectionJoin<Z, E> {

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
	public CollectionJoinImpl(AbstractFrom<?, Z> parent, PluralMapping<? super Z, Collection<E>, E> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CollectionAttribute<? super Z, E> getModel() {
		return (CollectionAttribute<? super Z, E>) this.getAttribute();
	}
}

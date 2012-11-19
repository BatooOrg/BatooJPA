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

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.metamodel.Type.PersistenceType;

import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.ElementCollectionMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMappingImpl;
import org.batoo.jpa.jdbc.mapping.MappingType;

/**
 * The implementation of {@link PluralJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <C>
 *            the collection type
 * @param <E>
 *            the element type of the collection
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractPluralJoin<Z, C, E> extends AbstractJoin<Z, E> implements PluralJoin<Z, C, E> {

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
	public AbstractPluralJoin(AbstractFrom<?, Z> parent, JoinedMapping<? super Z, ?, E> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <CC, Y> AbstractMapping<? super E, CC, Y> getMapping(String name) {
		AbstractMapping<? super E, ?, ?> child = null;

		if (this.getMapping().getMappingType() == MappingType.ELEMENT_COLLECTION) {
			final ElementCollectionMappingImpl<? super Z, C, E> elementCollectionMapping = (ElementCollectionMappingImpl<? super Z, C, E>) this.getMapping();
			if (elementCollectionMapping.getType().getPersistenceType() == PersistenceType.EMBEDDABLE) {
				child = (AbstractMapping<? super E, ?, ?>) elementCollectionMapping.getMapping(name);
			}
		}
		else {
			child = ((PluralAssociationMappingImpl<? super Z, C, E>) this.getMapping()).getType().getRootMapping().getChild(name);
		}

		if (child == null) {
			throw this.cannotDereference(name);
		}

		return (AbstractMapping<? super E, CC, Y>) child;
	}
}

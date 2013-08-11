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

package org.batoo.jpa.core.impl.criteria.join;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMappingImpl;
import org.batoo.jpa.jdbc.mapping.MappingType;

/**
 * Joins for singular attributes.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SingularJoin<Z, X> extends AbstractJoin<Z, X> {

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
	public SingularJoin(AbstractFrom<?, Z> parent, JoinedMapping<? super Z, ?, X> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <C, Y> AbstractMapping<? super X, C, Y> getMapping(String name) {
		AbstractMapping<? super X, ?, ?> child = null;

		if (this.getMapping().getMappingType() == MappingType.EMBEDDABLE) {
			child = ((EmbeddedMappingImpl<? super Z, X>) this.getMapping()).getChild(name);
		}
		else {
			child = ((SingularAssociationMappingImpl<? super Z, X>) this.getMapping()).getType().getRootMapping().getChild(name);
		}

		if (child == null) {
			throw this.cannotDereference(name);
		}

		return (AbstractMapping<? super X, C, Y>) child;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SingularAttribute<? super Z, X> getModel() {
		return (SingularAttributeImpl<? super Z, X>) super.getAttribute();
	}
}

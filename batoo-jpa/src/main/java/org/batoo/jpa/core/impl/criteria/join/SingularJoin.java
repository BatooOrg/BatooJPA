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
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping.MappingType;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;

/**
 * Joins for singular attributes.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
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
	 * @since $version
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
	protected <C, Y> Mapping<? super X, C, Y> getMapping(String name) {
		Mapping<? super X, ?, ?> child = null;

		if (this.getMapping().getMappingType() == MappingType.EMBEDDABLE) {
			child = ((EmbeddedMapping<? super Z, X>) this.getMapping()).getChild(name);
		}
		else {
			child = ((SingularAssociationMapping<? super Z, X>) this.getMapping()).getType().getRootMapping().getChild(name);
		}

		if (child == null) {
			throw this.cannotDereference(name);
		}

		return (Mapping<? super X, C, Y>) child;
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

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

package org.batoo.jpa.core.impl.model.attribute;

import java.util.Collection;
import java.util.List;

import javax.persistence.metamodel.ListAttribute;

import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

/**
 * Implementation of {@link ListAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ListAttributeImpl<X, E> extends PluralAttributeImpl<X, List<E>, E> implements ListAttribute<X, E> {

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            attribute type
	 * @since 2.0.0
	 */
	public ListAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		super(declaringType, metadata, attributeType, 0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Z extends X> ListAttributeImpl<Z, E> clone(EntityTypeImpl<Z> type) {
		return new ListAttributeImpl<Z, E>(type, this.getMetadata(), this.getPersistentAttributeType());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.LIST;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<E> newCollection(PluralMappingEx<?, List<E>, E> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		return new ManagedList<X, E>(mapping, managedInstance, lazy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<E> newCollection(PluralMappingEx<?, List<E>, E> mapping, ManagedInstance<?> managedInstance, Object values) {
		return new ManagedList<X, E>(mapping, managedInstance, (Collection<? extends E>) values);
	}
}

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

package org.batoo.jpa.core.impl.model;

import javax.persistence.metamodel.MappedSuperclassType;

import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

/**
 * Implementation of {@link MappedSuperclassType}.
 * 
 * @param <X>
 *            The represented entity type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MappedSuperclassTypeImpl<X> extends IdentifiableTypeImpl<X> implements MappedSuperclassType<X> {

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public MappedSuperclassTypeImpl(MetamodelImpl metamodel, MappedSuperclassTypeImpl<? super X> parent, Class<X> javaType, MappedSuperclassMetadata metadata) {
		super(metamodel, parent, javaType, metadata);

		this.addAttributes(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.MAPPED_SUPERCLASS;
	}
}

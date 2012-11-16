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
package org.batoo.jpa.core.impl.model.type;

import javax.persistence.metamodel.BasicType;

import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * Implementation of {@link BasicType}.
 * 
 * @param <X>
 *            The type of the represented basic type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public final class BasicTypeImpl<X> extends TypeImpl<X> implements BasicType<X> {

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param javaType
	 *            the java type of the type
	 * 
	 * @since 2.0.0
	 */
	public BasicTypeImpl(MetamodelImpl metamodel, Class<X> javaType) {
		super(metamodel, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.BASIC;
	}
}

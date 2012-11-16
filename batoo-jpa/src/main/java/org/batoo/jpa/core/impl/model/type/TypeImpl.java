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

import javax.persistence.metamodel.Type;

import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * Implementation of {@link Type}.
 * 
 * @param <X>
 *            The type of the represented object or attribute
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class TypeImpl<X> implements Type<X> {

	private final MetamodelImpl metamodel;
	private final Class<X> javaType;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param javaType
	 *            the java type of the type
	 * 
	 * @since 2.0.0
	 */
	public TypeImpl(MetamodelImpl metamodel, Class<X> javaType) {
		super();

		this.metamodel = metamodel;
		this.javaType = javaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<X> getJavaType() {
		return this.javaType;
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * 
	 * @since 2.0.0
	 */
	public final MetamodelImpl getMetamodel() {
		return this.metamodel;
	}
}

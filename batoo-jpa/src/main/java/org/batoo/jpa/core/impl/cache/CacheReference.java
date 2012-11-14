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
package org.batoo.jpa.core.impl.cache;

import java.io.Serializable;

import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Reference to another cachable entity
 * 
 * @author hceylan
 * @since $version
 */
public class CacheReference implements Serializable {

	private final String type;
	private final Object id;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param reference
	 *            the child
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CacheReference(MetamodelImpl metamodel, Object reference) {
		super();

		final EntityTypeImpl<?> entityType = metamodel.getEntity(reference.getClass());

		this.type = entityType.getJavaType().getName();
		this.id = entityType.getId(reference).getId();
	}

	/**
	 * Returns the id of the reference.
	 * 
	 * @return the id of the reference
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getId() {
		return this.id;
	}

	/**
	 * Returns the type of the reference.
	 * 
	 * @return the type of the reference
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "CacheReference [type=" + this.type + ", id=" + this.id + "]";
	}
}

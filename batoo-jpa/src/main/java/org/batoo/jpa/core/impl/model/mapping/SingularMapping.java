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
package org.batoo.jpa.core.impl.model.mapping;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The base class for {@link EmbeddedMapping} and {@link BasicMapping}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface SingularMapping<Z, X> {

	/**
	 * Fills the sequence / table generated value.
	 * <p>
	 * The operation returns false if at least one entity needs to obtain identity from the database.
	 * 
	 * @param type
	 *            the entity type
	 * @param managedInstance
	 *            the managed instance
	 * @param instance
	 *            the instance to fill ids.
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since 2.0.0
	 */
	boolean fillValue(EntityTypeImpl<?> type, ManagedInstance<?> managedInstance, Object instance);

	/**
	 * Returns the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the mapping value of instance
	 * 
	 * @since 2.0.0
	 */
	X get(Object instance);

	/**
	 * Returns the attribute of the mapping.
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since 2.0.0
	 */
	SingularAttributeImpl<? super Z, X> getAttribute();

	/**
	 * Sets the mapping value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to set
	 * @param value
	 *            the value to set
	 * 
	 * @since 2.0.0
	 */
	void set(Object instance, Object value);
}

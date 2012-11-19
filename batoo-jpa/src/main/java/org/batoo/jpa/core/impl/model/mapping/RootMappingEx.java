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

import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.jdbc.mapping.RootMapping;

/**
 * @param <X>
 *            the type of the mapping
 * 
 * @author hceylan
 * @since $version
 */
public interface RootMappingEx<X> extends RootMapping<X> {

	/**
	 * Returns the root type of the mapping.
	 * 
	 * @return the root type of the mapping
	 * 
	 * @since $version
	 */
	ManagedTypeImpl<? super X> getType();
}

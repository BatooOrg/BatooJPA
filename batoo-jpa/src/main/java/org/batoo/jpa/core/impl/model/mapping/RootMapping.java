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

import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;

/**
 * The interface for root mappings.
 * 
 * @param <X>
 *            the type of the mapping
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface RootMapping<X> {

	/**
	 * Returns the mapping corresponding to the name.
	 * 
	 * @param name
	 *            the name of the child mapping
	 * @return the mapping or null
	 * 
	 * @since 2.0.0
	 */
	Mapping<? super X, ?, ?> getChild(String name);

	/**
	 * Returns the mapping corresponding to the path.
	 * 
	 * @param path
	 *            the path of the mapping
	 * @return the mapping
	 * 
	 * @since 2.0.0
	 */
	Mapping<?, ?, ?> getMapping(String path);

	/**
	 * Returns the type of the root mapping.
	 * 
	 * @return the type of the root mapping
	 * 
	 * @since 2.0.0
	 */
	ManagedTypeImpl<?> getType();

	/**
	 * Returns if the root is an entity.
	 * 
	 * @return true if the root is an entity, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isEntity();
}

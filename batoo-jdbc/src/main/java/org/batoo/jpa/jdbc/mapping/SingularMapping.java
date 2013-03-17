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
package org.batoo.jpa.jdbc.mapping;

import org.batoo.jpa.jdbc.IdType;

/**
 * Mappings that corresponds to singular attributes.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since 2.0.1
 */
public interface SingularMapping<Z, X> extends Mapping<Z, X, X> {

	/**
	 * Returns the id type of the mapping.
	 * 
	 * @return the id type of the mapping or <code>null</code>
	 * 
	 * @since 2.0.0
	 */
	IdType getIdType();

	/**
	 * Returns if the mapping corresponds to an id field.
	 * 
	 * @return <code>true</code> if the mapping corresponds to an id field, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	boolean isId();
}

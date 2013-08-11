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
package org.batoo.jpa.parser.metadata.attribute;

/**
 * The commons definitions for association attributes that can be optional.
 * <p>
 * Typically these are OneToOne and ManyToOne attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface OptionalAssociationAttributeMetadata {

	/**
	 * Returns the maps id.
	 * 
	 * @return the maps id
	 * 
	 * @since 2.0.0
	 */
	String getMapsId();

	/**
	 * Returns if the attribute is an id attribute.
	 * 
	 * @return true if the attribute is an id attribute, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isId();

	/**
	 * Returns if the association attribute is optional.
	 * 
	 * @return true if the association attribute is optional.
	 * 
	 * @since 2.0.0
	 */
	boolean isOptional();
}

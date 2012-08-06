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
package org.batoo.jpa.parser.metadata.attribute;

import javax.persistence.EnumType;
import javax.persistence.FetchType;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public interface BasicAttributeMetadata extends PhysicalAttributeMetadata {

	/**
	 * Returns the enumType of the basic attribute.
	 * 
	 * @return the enumType of the basic attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	EnumType getEnumType();

	/**
	 * Returns the fetch type of the attribute.
	 * 
	 * @return the fetch type of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	FetchType getFetchType();

	/**
	 * Returns if the basic attribute is lob.
	 * 
	 * @return true if the basic attribute is lob
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isLob();

	/**
	 * Returns if the basic attribute is optional.
	 * 
	 * @return true if the basic attribute is optional, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isOptional();
}

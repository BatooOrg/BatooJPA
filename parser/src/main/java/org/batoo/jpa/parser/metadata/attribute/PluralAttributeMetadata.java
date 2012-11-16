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

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Definitions for plural attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface PluralAttributeMetadata {

	/**
	 * Returns the map key.
	 * 
	 * @return the map key
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	String getMapKey();

	/**
	 * Returns the list of map key attribute overrides.
	 * 
	 * @return the list of map key attribute overrides
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	List<AttributeOverrideMetadata> getMapKeyAttributeOverrides();

	/**
	 * Returns the class name of the map key.
	 * 
	 * @return the class name of the map key
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	String getMapKeyClassName();

	/**
	 * Returns the map key column definition.
	 * 
	 * @return the map key column definition
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	ColumnMetadata getMapKeyColumn();

	/**
	 * Returns the enum type of the map key.
	 * 
	 * @return the enum type of the map key
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	EnumType getMapKeyEnumType();

	/**
	 * Returns the temporal type of the map key.
	 * 
	 * @return the temporal type of the map key
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	TemporalType getMapKeyTemporalType();

	/**
	 * Returns the order by.
	 * 
	 * @return the order by
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	String getOrderBy();

	/**
	 * Returns the order column definition.
	 * 
	 * @return the order column definition
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	ColumnMetadata getOrderColumn();
}

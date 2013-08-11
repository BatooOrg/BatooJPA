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

import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Definition for element collection attributes.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface ElementCollectionAttributeMetadata extends PluralAttributeMetadata, EmbeddedAttributeMetadata {

	/**
	 * Returns the collection table definition.
	 * 
	 * @return the collection table definition
	 * 
	 * @since 2.0.0
	 */
	CollectionTableMetadata getCollectionTable();

	/**
	 * Returns the column definition.
	 * 
	 * @return the column definition
	 * 
	 * @since 2.0.0
	 */
	ColumnMetadata getColumn();

	/**
	 * Returns the enum type.
	 * 
	 * @return the enum type
	 * 
	 * @since 2.0.0
	 */
	EnumType getEnumType();

	/**
	 * Returns the fetch type.
	 * 
	 * @return the fetch type
	 * 
	 * @since 2.0.0
	 */
	FetchType getFetchType();

	/**
	 * Returns the name of the target class.
	 * 
	 * @return the name of the target class
	 * 
	 * @since 2.0.0
	 */
	String getTargetClass();

	/**
	 * Returns the temporal type.
	 * 
	 * @return the enum type
	 * 
	 * @since 2.0.0
	 */
	TemporalType getTemporalType();

	/**
	 * Returns if the attribute is lob type.
	 * 
	 * @return true if the attribute is lob type, false othwerwise
	 * 
	 * @since 2.0.0
	 */
	boolean isLob();
}

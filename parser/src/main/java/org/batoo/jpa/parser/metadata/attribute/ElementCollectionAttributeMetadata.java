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
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Definition for element collection attributes.
 * 
 * @author hceylan
 * @since $version
 */
public interface ElementCollectionAttributeMetadata extends PluralAttributeMetadata, EmbeddedAttributeMetadata {

	/**
	 * Returns the collection table definition.
	 * 
	 * @return the collection table definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	CollectionTableMetadata getCollectionTable();

	/**
	 * Returns the column definition.
	 * 
	 * @return the column definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ColumnMetadata getColumn();

	/**
	 * Returns the enum type.
	 * 
	 * @return the enum type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	EnumType getEnumType();

	/**
	 * Returns the fetch type.
	 * 
	 * @return the fetch type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	FetchType getFetchType();

	/**
	 * Returns the name of the target class.
	 * 
	 * @return the name of the target class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTargetClass();

	/**
	 * Returns the temporal type.
	 * 
	 * @return the enum type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TemporalType getTemporalType();

	/**
	 * Returns if the attribute is lob type.
	 * 
	 * @return true if the attribute is lob type, false othwerwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isLob();
}

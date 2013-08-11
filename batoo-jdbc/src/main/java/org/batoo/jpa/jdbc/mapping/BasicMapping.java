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
package org.batoo.jpa.jdbc.mapping;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.jdbc.BasicColumn;
import org.batoo.jpa.parser.metadata.ColumnTransformerMetadata;

/**
 * The interface for basic mappings.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since 2.0.1
 */
public interface BasicMapping<Z, X> extends SingularMapping<Z, X> {

	/**
	 * Returns the column of the mapping.
	 * 
	 * @return the column of the mapping
	 * 
	 * @since 2.0.1
	 */
	BasicColumn getColumn();

	/**
	 * Returns the column transformer of the mapping.
	 * 
	 * @return the column transformer of the mapping
	 * 
	 * @since 2.0.1
	 */
	ColumnTransformerMetadata getColumnTransformer();

	/**
	 * Returns the enum type of the mapping.
	 * 
	 * @return the enum type of the mapping or <code>null</code>
	 * 
	 * @since 2.0.1
	 */
	EnumType getEnumType();

	/**
	 * Returns the temporal type of the mapping.
	 * 
	 * @return the temporal type of the mapping, <code>null</code>
	 * 
	 * @since 2.0.1
	 */
	TemporalType getTemporalType();

	/**
	 * Returns if the mapping is lob type.
	 * 
	 * @return <code>true</code> if the mapping is lob type, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	boolean isLob();

	/**
	 * Returns if the column is a version column.
	 * 
	 * @return <code>true</code> if the column is a version column, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	boolean isVersion();
}

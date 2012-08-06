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
package org.batoo.jpa.parser.metadata;

/**
 * The common definition of the columns.
 * 
 * @author hceylan
 * @since $version
 */
public interface BaseColumnMetadata extends BindableMetadata {

	/**
	 * Returns the raw column definition of the column.
	 * 
	 * @return the raw column definition of the column.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getColumnDefinition();

	/**
	 * Returns the table of the column.
	 * 
	 * @return the table of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTable();

	/**
	 * Returns if the column is insertable.
	 * 
	 * @return true if the column is insertable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isInsertable();

	/**
	 * Returns if the column is nullable.
	 * 
	 * @return true if the column is nullable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isNullable();

	/**
	 * Returns if the column is unique.
	 * 
	 * @return true if the column is unique
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isUnique();

	/**
	 * Returns if the column is updatable.
	 * 
	 * @return true if the column is updatable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isUpdatable();
}

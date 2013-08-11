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
package org.batoo.jpa.parser.metadata;

/**
 * The common definition of the columns.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface BaseColumnMetadata extends BindableMetadata {

	/**
	 * Returns the raw column definition of the column.
	 * 
	 * @return the raw column definition of the column.
	 * 
	 * @since 2.0.0
	 */
	String getColumnDefinition();

	/**
	 * Returns the table of the column.
	 * 
	 * @return the table of the column
	 * 
	 * @since 2.0.0
	 */
	String getTable();

	/**
	 * Returns if the column is insertable.
	 * 
	 * @return true if the column is insertable
	 * 
	 * @since 2.0.0
	 */
	boolean isInsertable();

	/**
	 * Returns if the column is nullable.
	 * 
	 * @return true if the column is nullable
	 * 
	 * @since 2.0.0
	 */
	boolean isNullable();

	/**
	 * Returns if the column is unique.
	 * 
	 * @return true if the column is unique
	 * 
	 * @since 2.0.0
	 */
	boolean isUnique();

	/**
	 * Returns if the column is updatable.
	 * 
	 * @return true if the column is updatable
	 * 
	 * @since 2.0.0
	 */
	boolean isUpdatable();
}

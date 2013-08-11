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
 * The definition for unique constraints.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface IndexMetadata extends BindableMetadata {

	/**
	 * Returns the list column names that make up the unique constraint.
	 * 
	 * @return the list of columns names that make up the unique constraint
	 * 
	 * @since 2.0.0
	 */
	String[] getColumnNames();

	/**
	 * Returns the name of the table.
	 * 
	 * @return the name of the table
	 * 
	 * @since 2.0.0
	 */
	String getTable();
}

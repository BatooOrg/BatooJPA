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
 * The definition of primary key join columns.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface PrimaryKeyJoinColumnMetadata extends BindableMetadata {

	/**
	 * Returns the raw column definition of the primary key join column.
	 * 
	 * @return the raw column definition of the the primary key join column
	 * 
	 * @since 2.0.0
	 */
	String getColumnDefinition();

	/**
	 * Returns the name of the referenced column of the the primary key join column.
	 * 
	 * @return the name of the referenced column of the the primary key join column
	 * 
	 * @since 2.0.0
	 */
	String getReferencedColumnName();
}

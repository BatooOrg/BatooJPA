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
package org.batoo.jpa.jdbc;

/**
 * The valid modes for DDL
 * 
 * @author hceylan
 * @since 2.0.0
 */
public enum DDLMode {
	/**
	 * Drop and recreate the schemas and tables
	 */
	DROP,

	/**
	 * Only create missing schemas and tables
	 */
	CREATE,

	/**
	 * Only update the existing tables
	 */
	UPDATE,

	/**
	 * None
	 */
	NONE;
}

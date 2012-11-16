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

import java.util.List;

/**
 * The definition of the table generators.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface TableGeneratorMetadata extends GeneratorMetadata {

	/**
	 * Returns the name of the primary key column of the table generator.
	 * 
	 * @return the name of the primary key column of the table generator
	 * 
	 * @since 2.0.0
	 */
	String getPkColumnName();

	/**
	 * Returns the primary key value of the table generator.
	 * 
	 * @return the primary key value of the table generator
	 * 
	 * @since 2.0.0
	 */
	String getPkColumnValue();

	/**
	 * Returns the table name of the table generator.
	 * 
	 * @return the table name of the table generator
	 * 
	 * @since 2.0.0
	 */
	String getTable();

	/**
	 * Returns the list of unique constraints of the table generator.
	 * 
	 * @return the list of unique constraints of the table generator
	 * 
	 * @since 2.0.0
	 */
	List<UniqueConstraintMetadata> getUniqueConstraints();

	/**
	 * Returns the name of the value column of the table generator.
	 * 
	 * @return the name of the value column of the table generator
	 * 
	 * @since 2.0.0
	 */
	String getValueColumnName();
}

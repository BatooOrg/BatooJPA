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
 * The common definition of the generators.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface GeneratorMetadata extends BindableMetadata {

	/**
	 * Returns the allocation size of the generator.
	 * 
	 * @return the allocation size of the generator
	 * 
	 * @since 2.0.0
	 */
	int getAllocationSize();

	/**
	 * Returns the catalog of the generator.
	 * 
	 * @return the catalog of the generator
	 * 
	 * @since 2.0.0
	 */
	String getCatalog();

	/**
	 * Returns the initial value of the generator.
	 * 
	 * @return the initial Value of the generator
	 * 
	 * @since 2.0.0
	 */
	int getInitialValue();

	/**
	 * Returns the name of the schema of the generator.
	 * 
	 * @return the name of the schema of the generator
	 * 
	 * @since 2.0.0
	 */
	String getSchema();

}

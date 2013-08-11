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

import java.util.List;


/**
 * 
 * @author asimarslan
 * @since 2.0.1
 */
public interface EntityResultMetadata extends LocatableMatadata {

	/**
	 * Specifies the column name (or alias) of the column in the SELECT list that is used to determine the type of the entity instance.
	 * 
	 * @return DiscriminatorColumn
	 * @since 2.0.1
	 */
	String getDiscriminatorColumn();

	/**
	 * The class of the result.
	 * 
	 * @return The class of the result
	 * @since 2.0.1
	 */
	String getEntityClass();

	/**
	 * Maps the columns specified in the SELECT list of the query to the properties or fields of the entity class.
	 * 
	 * @return FieldResultMetadata 's
	 * @since 2.0.1
	 */
	List<FieldResultMetadata> getFields();

}

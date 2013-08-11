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

import org.batoo.jpa.jdbc.ForeignKey;
import org.batoo.jpa.jdbc.JoinableTable;

/**
 * The interface for association mappings.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since 2.0.1
 */
public interface AssociationMapping<Z, X, Y> extends Mapping<Z, X, Y> {

	/**
	 * Returns the foreign key of the mapping.
	 * 
	 * @return the foreign key of the mapping
	 * 
	 * @since 2.0.0
	 */
	public abstract ForeignKey getForeignKey();

	/**
	 * Returns the join table for the relation.
	 * <p>
	 * This is, for associations the JoinTable, for element collections the CollectionTable.
	 * 
	 * @return the table
	 * 
	 * @since 2.0.0
	 */
	JoinableTable getJoinTable();
}

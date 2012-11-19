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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.TypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.jdbc.JoinableTable;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.jdbc.mapping.MappingType;

/**
 * Mapping that have a join or collection table
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface JoinedMapping<Z, X, Y> extends Mapping<Z, X, Y> {

	/**
	 * Extracts key from the value.
	 * 
	 * @param value
	 *            the value
	 * @return the key of the value
	 * 
	 * @since 2.0.0
	 */
	public Object extractKey(Object value);

	/**
	 * Flushes the associates.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance
	 * @param removals
	 *            true if the removals should be flushed and false for the additions
	 * @param force
	 *            true to force, effective only for insertions and for new entities.
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	void flush(Connection connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException;

	/**
	 * Returns the attribute of the mapping
	 * 
	 * @return the attribute of the mapping
	 * 
	 * @since 2.0.0
	 */
	AttributeImpl<? super Z, X> getAttribute();

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

	/**
	 * Returns the type of the mapping.
	 * 
	 * @return the type of the mapping
	 * 
	 * @since 2.0.0
	 */
	MappingType getMappingType();

	/**
	 * Returns the bindable entity type.
	 * 
	 * @return the bindable entity type
	 * 
	 * @since 2.0.0
	 */
	TypeImpl<Y> getType();

	/**
	 * Initializes the mapping.
	 * 
	 * @param instance
	 *            the instance
	 * 
	 * @since 2.0.0
	 */
	void initialize(ManagedInstance<?> instance);

	/**
	 * Returns if the mapping is an association.
	 * 
	 * @return true if the mapping is an association, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isAssociation();

	/**
	 * Returns if the association should be eagerly fetched.
	 * 
	 * @return true if the association should be eagerly fetched
	 * 
	 * @since 2.0.0
	 */
	boolean isEager();

	/**
	 * Returns if the mapping is a joined mapping.
	 * 
	 * @return true if the mapping is a joined mapping, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isJoined();

	/**
	 * Returns if the mapping is a map collection.
	 * 
	 * @return true if the mapping is a map collection, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isMap();

	/**
	 * Returns the join SQL for the mapping.
	 * 
	 * @return the join SQL for the mapping
	 * 
	 * @param parentAlias
	 *            the parent table alias
	 * @param alias
	 *            the primary table alias
	 * @param joinType
	 *            the join type
	 * 
	 * @since 2.0.0
	 */
	String join(String parentAlias, String alias, JoinType joinType);
}

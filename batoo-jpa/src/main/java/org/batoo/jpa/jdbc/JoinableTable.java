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
package org.batoo.jpa.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface JoinableTable {

	/**
	 * Performs the insert for the join.
	 * 
	 * @param connection
	 *            the connection
	 * @param source
	 *            the source instance
	 * @param batch
	 *            the array of joinable batch
	 * @param size
	 *            the size of the batch
	 * 
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	void performInsert(Connection connection, Object source, Joinable[] batch, int size) throws SQLException;

	/**
	 * Performs the remove for the join.
	 * 
	 * @param connection
	 *            the connection
	 * @param source
	 *            the source instance
	 * @param key
	 *            the key object
	 * @param destination
	 *            the destination instance
	 * 
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	void performRemove(Connection connection, Object source, Object key, Object destination) throws SQLException;

	/**
	 * Performs the remove for the join.
	 * 
	 * @param connection
	 *            the connection
	 * @param source
	 *            the source instance
	 * 
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since 2.0.0
	 */
	void performRemoveAll(Connection connection, Object source) throws SQLException;
}

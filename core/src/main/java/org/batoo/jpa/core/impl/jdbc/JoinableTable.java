/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.jdbc;

import java.sql.SQLException;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public interface JoinableTable {

	/**
	 * Performs the insert for the join.
	 * 
	 * @param connection
	 *            the connection
	 * @param source
	 *            the source instance
	 * @param key
	 *            the key object
	 * @param destination
	 *            the destination instance
	 * @param order
	 *            the order of the column
	 * 
	 * @throws SQLException
	 *             thrown if there is an underlying SQL Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void performInsert(ConnectionImpl connection, Object source, Object key, Object destination, int order) throws SQLException;

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
	 * @since $version
	 * @author hceylan
	 */
	void performRemove(ConnectionImpl connection, Object source, Object key, Object destination) throws SQLException;

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
	 * @since $version
	 * @author hceylan
	 */
	void performRemoveAll(ConnectionImpl connection, Object source) throws SQLException;
}

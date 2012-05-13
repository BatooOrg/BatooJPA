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
package org.batoo.jpa.core.impl.mapping;

import java.sql.Connection;
import java.sql.SQLException;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

/**
 * An association that can persist the relation with a join table
 * 
 * @author hceylan
 * @since $version
 */
public interface PersistableAssociation<X, T> extends Mapping<X, T> {

	/**
	 * Returns the joinTable.
	 * 
	 * @return the joinTable
	 * @since $version
	 */
	JoinTable getJoinTable();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getType();

	/**
	 * Returns if the association has join table.
	 * 
	 * @return true if the association has join table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean hasJoin();

	/**
	 * Performs insertions for the joins.
	 * 
	 * @param connection
	 *            the connection
	 * @param managedInstance
	 *            the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void performInsert(Connection connection, ManagedInstance<X> managedInstance) throws SQLException;
}

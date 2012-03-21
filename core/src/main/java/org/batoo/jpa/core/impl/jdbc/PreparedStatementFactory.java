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

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

import com.google.common.annotations.Beta;

/**
 * A Factory to implement preparation of {@link PreparedStatementImpl}s
 * 
 * @author hceylan
 * @since $version
 */
@Beta
public class PreparedStatementFactory extends BaseKeyedPoolableObjectFactory<String, PreparedStatementImpl> {

	private final ConnectionImpl connection;

	/**
	 * @param connection
	 *            the connection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PreparedStatementFactory(ConnectionImpl connection) {
		super();

		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void destroyObject(String key, PreparedStatementImpl obj) throws SQLException {
		obj.close0();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatementImpl makeObject(String key) throws SQLException {
		return this.connection.prepareStatement0(key);
	}

}

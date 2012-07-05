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

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;

import com.google.common.annotations.Beta;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Beta
public class ConnectionFactory extends BasePoolableObjectFactory<ConnectionImpl> {

	private static final BLogger LOG = BLoggerFactory.getLogger(ConnectionFactory.class);

	private final DataSourceImpl dataSource;

	/**
	 * @param dataSource
	 *            the datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ConnectionFactory(DataSourceImpl dataSource) {
		super();

		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void activateObject(ConnectionImpl obj) throws Exception {
		obj.reset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void destroyObject(ConnectionImpl obj) throws Exception {
		obj.close0();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ConnectionImpl makeObject() throws SQLException {
		return this.dataSource.getConnection0();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void passivateObject(ConnectionImpl obj) throws Exception {
		try {
			obj.setAutoCommit(true);
		}
		catch (final Exception e) {
			ConnectionFactory.LOG.error(e, "Error while returning connection");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean validateObject(ConnectionImpl obj) {
		try {
			return obj.isValid(DataSourceImpl.MAX_WAIT);
		}
		catch (final SQLException e) {
			return false;
		}
	}

}

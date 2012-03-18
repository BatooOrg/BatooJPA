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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.OperationTookLongTimeWarning;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ConnectionFactory extends BasePoolableObjectFactory<Connection> {

	private static final BLogger LOG = BLogger.getLogger(ConnectionFactory.class);

	private final String jdbcUrl;
	private final String jdbcUser;
	private final String jdbcPassword;

	/**
	 * @param jdbcUrl
	 *            the JDBC URL
	 * @param jdbcUser
	 *            the JDBC user
	 * @param jdbcPassword
	 *            the JDBC password
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ConnectionFactory(String jdbcUrl, String jdbcUser, String jdbcPassword) {
		super();

		this.jdbcUrl = jdbcUrl;
		this.jdbcUser = jdbcUser;
		this.jdbcPassword = jdbcPassword;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void destroyObject(Connection obj) throws Exception {
		DbUtils.closeQuietly(obj);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection makeObject() throws Exception {
		LOG.trace("getConnection()");

		final long start = System.currentTimeMillis();
		try {
			return DriverManager.getConnection(this.jdbcUrl, this.jdbcUser, this.jdbcPassword);
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				LOG.warn(new OperationTookLongTimeWarning(), "{1} msecs, getConnection()", time);
			}
			else {
				LOG.trace("{0} msecs, getConnection()", time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void passivateObject(Connection obj) throws Exception {
		try {
			obj.setAutoCommit(true);
		}
		catch (final Exception e) {
			LOG.error(e, "Error while returning connection");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean validateObject(Connection obj) {
		try {
			return obj.isValid(DataSourceImpl.MAX_WAIT);
		}
		catch (final SQLException e) {
			return false;
		}
	}

}

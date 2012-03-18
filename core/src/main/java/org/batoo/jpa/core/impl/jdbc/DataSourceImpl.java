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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.batoo.jpa.core.BLogger;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class DataSourceImpl implements DataSource {

	private static final BLogger LOG = BLogger.getLogger(DataSourceImpl.class);

	static final int MAX_WAIT = 1000;

	private final String jdbcUrl;

	private PrintWriter printer;
	private int loginTimeout;

	private boolean active;
	private final Connection keepaliveConnection; // Kept to keep in memory databases open

	private final ConnectionFactory connectionFactory;

	private final GenericObjectPool<Connection> pool;

	/**
	 * @param jdbcUrl
	 *            the JDBC UTL
	 * @param jdbcUser
	 *            the user
	 * @param jdbcPassword
	 *            the password
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DataSourceImpl(String jdbcUrl, String jdbcUser, String jdbcPassword) throws SQLException {
		super();

		this.jdbcUrl = jdbcUrl;

		this.active = true;
		this.connectionFactory = new ConnectionFactory(this.jdbcUrl, jdbcUser, jdbcPassword);
		this.pool = new GenericObjectPool<Connection>(this.connectionFactory);
		this.pool.setMaxWait(MAX_WAIT);

		this.keepaliveConnection = this.getConnection();

		LOG.info("Datasource active: {0}", this.jdbcUrl);
	}

	private void assertActive() {
		if (!this.active) {
			throw new IllegalStateException("Datasource closed!");
		}
	}

	/**
	 * Closes the connection provider
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void close() {
		this.assertActive();

		LOG.info("Datasource closed: {0}", this.jdbcUrl);

		this.active = false;
		try {
			this.pool.close();
		}
		catch (final Exception e) {
			LOG.warn(e, "Error while closing connection cache");
		}

		DbUtils.closeQuietly(this.keepaliveConnection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection getConnection() throws SQLException {
		try {
			return new ConnectionImpl(this.pool);
		}
		catch (final Exception e) {
			if (e instanceof SQLException) {
				throw (SQLException) e;
			}

			throw new SQLException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return this.loginTimeout;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.printer;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeout = seconds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.printer = out;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
}

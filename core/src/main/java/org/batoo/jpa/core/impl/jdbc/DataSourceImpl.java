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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.pool.ObjectPool;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.impl.manager.OperationTookLongTimeWarning;
import org.batoo.jpa.core.pool.GenericPool;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class DataSourceImpl implements DataSource {

	private static final BLogger LOG = BLoggerFactory.getLogger(DataSourceImpl.class);

	static final int MAX_WAIT = 1000;

	private final String jdbcUrl;
	private final String jdbcUser;
	private final String jdbcPassword;

	private final ObjectPool<ConnectionImpl> pool;

	private PrintWriter printer;
	private int loginTimeout;

	private final boolean open;

	/**
	 * @param jdbcUrl
	 *            the JDBC UTL
	 * @param jdbcUser
	 *            the user
	 * @param jdbcPassword
	 *            the password
	 * @throws SQLException
	 *             thrown in case of an underlying SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DataSourceImpl(String jdbcUrl, String jdbcUser, String jdbcPassword) throws SQLException {
		super();

		this.jdbcUrl = jdbcUrl;
		this.jdbcUser = jdbcUser;
		this.jdbcPassword = jdbcPassword;

		this.pool = new GenericPool<ConnectionImpl>(new ConnectionFactory(this));

		DataSourceImpl.LOG.info("Datasource active: {0}", this.jdbcUrl);

		this.open = true;
	}

	/**
	 * Closes the connection provider
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void close() {
		if (!this.open) {
			DataSourceImpl.LOG.info("Datasource closed: {0}", this.jdbcUrl);

			try {
				this.pool.close();
			}
			catch (final Exception e) {
				DataSourceImpl.LOG.warn(e, "Error while closing connection cache");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ConnectionImpl getConnection() throws SQLException {
		if (!this.open) {
			throw new IllegalStateException("Datasource closed");
		}

		try {
			if (this.pool == null) {
				return this.getConnection0();
			}

			return this.pool.borrowObject();
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
	 * 
	 * @see #getConnection()
	 * 
	 * @since $version
	 * @author hceylan
	 */
	/* package */ConnectionImpl getConnection0() throws SQLException {
		DataSourceImpl.LOG.trace("getConnection()");

		final long start = System.currentTimeMillis();
		try {
			final Connection connection = DriverManager.getConnection(this.jdbcUrl, this.jdbcUser, this.jdbcPassword);

			return new ConnectionImpl(connection, this.pool);
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				DataSourceImpl.LOG.warn(new OperationTookLongTimeWarning(), "{0} msecs, getConnection()", time);
			}
			else {
				DataSourceImpl.LOG.trace("{0} msecs, getConnection()", time);
			}
		}

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

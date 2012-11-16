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
package org.batoo.jpa.core.impl.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.batoo.jpa.core.impl.jdbc.PreparedStatementProxy.SqlLoggingType;

/**
 * Proxy class to proxy datasources. Main purpose is to cache and wrap the prepared statements.
 * 
 * @author hceylan
 * @since $version
 */
public class DataSourceProxy implements DataSource {

	private final SqlLoggingType sqlLogging;
	private final long slowSqlThreshold;
	private final int jdbcFetchSize;
	private final DataSource datasource;

	/**
	 * @param datasource
	 *            the original datasource
	 * @param slowSqlThreshold
	 *            the time to decide if SQL is deemed as slow
	 * @param sqlLogging
	 *            the sql logging type
	 * @param jdbcFetchSize
	 *            the size of the jdbc fetch
	 * 
	 * @since $version
	 */
	public DataSourceProxy(DataSource datasource, SqlLoggingType sqlLogging, long slowSqlThreshold, int jdbcFetchSize) {
		super();

		this.datasource = datasource;
		this.sqlLogging = sqlLogging;
		this.slowSqlThreshold = slowSqlThreshold;
		this.jdbcFetchSize = jdbcFetchSize;
	}

	/**
	 * Closes the resource local datasource.
	 */
	public void close() {
		if (this.datasource instanceof BoneCPDataSource) {
			((BoneCPDataSource) this.datasource).close();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return new ConnectionProxy(this.datasource.getConnection(), this.slowSqlThreshold, this.sqlLogging, this.jdbcFetchSize);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return new ConnectionProxy(this.datasource.getConnection(username, password), this.slowSqlThreshold, this.sqlLogging, this.jdbcFetchSize);
	}

	/**
	 * Returns the delegate datasource.
	 * 
	 * @return the delegate datasource
	 * 
	 * @since $version
	 */
	public DataSource getDelegate() {
		return this.datasource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return this.datasource.getLoginTimeout();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.datasource.getLogWriter();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.datasource.getParentLogger();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.datasource.isWrapperFor(iface);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.datasource.setLoginTimeout(seconds);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.datasource.setLogWriter(out);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.datasource.unwrap(iface);
	}
}

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

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.jdbc.PreparedStatementProxy.SqlLoggingType;
import org.batoo.jpa.jdbc.datasource.AbstractDataSource;

/**
 * Proxy class to proxy datasources. Main purpose is to cache and wrap the prepared statements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class DataSourceProxy implements DataSource {

	private static final BLogger LOG = BLoggerFactory.getLogger(DataSourceProxy.class);

	private final DataSource datasource;
	private final boolean external;
	private final SqlLoggingType sqlLogging;
	private final long slowSqlThreshold;
	private final int jdbcFetchSize;
	private final boolean externalPoolDS;

	/**
	 * @param datasource
	 *            the custom datasource
	 * @param external
	 *            if the original datasource is external
	 * @param slowSqlThreshold
	 *            the time to decide if SQL is deemed as slow
	 * @param sqlLogging
	 *            the sql logging type
	 * @param jdbcFetchSize
	 *            the size of the jdbc fetch
	 * 
	 * @since 2.0.0
	 */
	public DataSourceProxy(AbstractDataSource datasource, boolean external, SqlLoggingType sqlLogging, long slowSqlThreshold, int jdbcFetchSize) {
		super();

		this.datasource = datasource;
		this.external = external;
		this.sqlLogging = sqlLogging;
		this.slowSqlThreshold = slowSqlThreshold;
		this.jdbcFetchSize = jdbcFetchSize;
		this.externalPoolDS = true;
	}

	/**
	 * @param datasource
	 *            the original datasource
	 * @param external
	 *            if the original datasource is external
	 * @param slowSqlThreshold
	 *            the time to decide if SQL is deemed as slow
	 * @param sqlLogging
	 *            the sql logging type
	 * @param jdbcFetchSize
	 *            the size of the jdbc fetch
	 * 
	 * @since 2.0.0
	 */
	public DataSourceProxy(DataSource datasource, boolean external, SqlLoggingType sqlLogging, long slowSqlThreshold, int jdbcFetchSize) {
		super();

		this.datasource = datasource;
		this.external = external;
		this.sqlLogging = sqlLogging;
		this.slowSqlThreshold = slowSqlThreshold;
		this.jdbcFetchSize = jdbcFetchSize;
		this.externalPoolDS = false;
	}

	/**
	 * Closes the resource local datasource.
	 * 
	 */
	public void close() {
		if (!this.external && !this.externalPoolDS) {
			try {
				// close the datasource via reflection
				final Method closeMethod = this.datasource.getClass().getMethod("close");
				if (closeMethod != null) {
					closeMethod.invoke(this.datasource);
				}
			}
			catch (final Exception e) {
				DataSourceProxy.LOG.error(e, "Cannot close() the internal datasource");
			}
		}
		else if (this.externalPoolDS) {
			((AbstractDataSource) this.datasource).close();
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
		throw new UnsupportedOperationException("not supported");
	}

	/**
	 * Returns the delegate datasource.
	 * 
	 * @return the delegate datasource
	 * 
	 * @since 2.0.0
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

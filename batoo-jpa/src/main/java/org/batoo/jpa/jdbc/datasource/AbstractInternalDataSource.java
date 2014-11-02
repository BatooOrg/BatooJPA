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

package org.batoo.jpa.jdbc.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;

/**
 * Internal abstract datasource
 * @author lburgazzoli
 * @author asimarslan
 */
abstract class AbstractInternalDataSource extends AbstractDataSource {

	private static final BLogger LOGGER = BLoggerFactory.getLogger(HikariCPDataSource.class);

	private DataSource dataSource;

	protected AbstractInternalDataSource() {
		this.dataSource = null;
	}

	@Override
	public void releaseConnection(Connection connection) {
		try {
			connection.close();
		}
		catch (Exception e) {
			LOGGER.warn("Exception", e);
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return this.dataSource.getConnection(username, password);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.dataSource.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.dataSource.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.dataSource.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return this.dataSource.getLoginTimeout();
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.dataSource.getParentLogger();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.equals(iface)
			|| AbstractDataSource.class.isAssignableFrom(iface)
			|| AbstractInternalDataSource.class.isAssignableFrom(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) {
		try {
			if (isWrapperFor(iface)) {
				return iface.cast(this);
			}
			else {
				throw new RuntimeException("Cannot unwrap to requested type [" + iface.getName() + "]");
			}
		}
		catch (SQLException e) {
			throw new RuntimeException("Cannot unwrap to requested type [" + iface.getName() + "]", e);
		}
	}

	protected void setWrappedDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected DataSource getWrappedDataSource() {
		return this.dataSource;
	}

	/**
	 * remove the prefixed keys prefix and return them as pro
	 * @param mapProps
	 * @param prefix
	 * @return
	 * @throws Exception
	 */
	static Properties cropPrefixFromProperties(Map<String, Object> mapProps, String prefix) throws Exception {
		Properties cpProps = new Properties();
		for (String key : mapProps.keySet()) {
			if (key.startsWith(prefix)) {
				cpProps.setProperty(key.substring(prefix.length()), (String) mapProps.get(key));
			}
		}
		return cpProps;
	}
}

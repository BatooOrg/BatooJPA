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
package org.batoo.jpa.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.batoo.jpa.jdbc.PreparedStatementProxy.SqlLoggingType;

/**
 * Proxy class to proxy connections. Main purpose is to cache and wrap the prepared statements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ConnectionProxy implements Connection {

	private final Connection connection;

	private final long slowSqlThreshold;
	private final SqlLoggingType sqlLogging;
	private final int jdbcFetchSize;

	/**
	 * @param connection
	 *            the connection
	 * @param slowSqlThreshold
	 *            the time to decide if SQL is deemed as slow
	 * @param sqlLogging
	 *            the sql logging type
	 * @param jdbcFetchSize
	 *            the size of the jdbc fetch
	 * 
	 * @since 2.0.0
	 */
	public ConnectionProxy(Connection connection, long slowSqlThreshold, SqlLoggingType sqlLogging, int jdbcFetchSize) {
		super();

		this.connection = connection;
		this.slowSqlThreshold = slowSqlThreshold;
		this.sqlLogging = sqlLogging;
		this.jdbcFetchSize = jdbcFetchSize;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void abort(final Executor executor) throws SQLException {
		this.connection.abort(executor);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clearWarnings() throws SQLException {
		this.connection.clearWarnings();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() throws SQLException {
		this.connection.close();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void commit() throws SQLException {
		this.connection.commit();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return this.connection.createArrayOf(typeName, elements);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Blob createBlob() throws SQLException {
		return this.connection.createBlob();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Clob createClob() throws SQLException {
		return this.connection.createClob();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NClob createNClob() throws SQLException {
		return this.connection.createNClob();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return this.connection.createSQLXML();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Statement createStatement() throws SQLException {
		final Statement statement = this.connection.createStatement();

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.connection.createStatement(resultSetType, resultSetConcurrency);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return this.connection.createStruct(typeName, attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		return this.connection.getAutoCommit();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() throws SQLException {
		return this.connection.getCatalog();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Properties getClientInfo() throws SQLException {
		return this.connection.getClientInfo();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClientInfo(String name) throws SQLException {
		return this.connection.getClientInfo(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getHoldability() throws SQLException {
		return this.connection.getHoldability();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.connection.getMetaData();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNetworkTimeout() throws SQLException {
		return this.connection.getNetworkTimeout();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() throws SQLException {
		return this.connection.getSchema();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		return this.connection.getTransactionIsolation();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.connection.getTypeMap();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.connection.getWarnings();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return this.connection.isClosed();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		return this.connection.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		return this.connection.isValid(timeout);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.connection.isWrapperFor(iface);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		return this.connection.nativeSQL(sql);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.connection.prepareCall(sql);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		final PreparedStatementProxy statement = new PreparedStatementProxy(sql, this.connection.prepareStatement(sql), this.slowSqlThreshold, this.sqlLogging);

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		final PreparedStatementProxy statement = new PreparedStatementProxy(sql, this.connection.prepareStatement(sql, autoGeneratedKeys),
			this.slowSqlThreshold, this.sqlLogging);

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		final PreparedStatementProxy statement = new PreparedStatementProxy(sql, this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency),
			this.slowSqlThreshold, this.sqlLogging);

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		final PreparedStatementProxy statement = new PreparedStatementProxy(sql, this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency,
			resultSetHoldability), this.slowSqlThreshold, this.sqlLogging);

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		final PreparedStatementProxy statement = new PreparedStatementProxy(sql, this.connection.prepareStatement(sql, columnIndexes), this.slowSqlThreshold,
			this.sqlLogging);

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		final PreparedStatementProxy statement = new PreparedStatementProxy(sql, this.connection.prepareStatement(sql, columnNames), this.slowSqlThreshold,
			this.sqlLogging);

		statement.setFetchSize(this.jdbcFetchSize);

		return statement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.connection.releaseSavepoint(savepoint);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void rollback() throws SQLException {
		this.connection.rollback();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		this.connection.rollback(savepoint);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.connection.setAutoCommit(autoCommit);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCatalog(String catalog) throws SQLException {
		this.connection.setCatalog(catalog);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		this.connection.setClientInfo(properties);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		this.connection.setClientInfo(name, value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setHoldability(int holdability) throws SQLException {
		this.connection.setHoldability(holdability);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		this.connection.setNetworkTimeout(executor, milliseconds);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		this.connection.setReadOnly(readOnly);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Savepoint setSavepoint() throws SQLException {
		return this.connection.setSavepoint();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return this.connection.setSavepoint(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setSchema(String schema) throws SQLException {
		this.connection.setSchema(schema);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		this.connection.setTransactionIsolation(level);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.connection.setTypeMap(map);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.connection.unwrap(iface);
	}

}

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
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.pool.ObjectPool;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.impl.manager.OperationTookLongTimeWarning;
import org.batoo.jpa.core.pool.GenericKeyedPool;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ConnectionImpl implements Connection {

	private static final BLogger LOG = BLoggerFactory.getLogger(ConnectionImpl.class);

	private static AtomicLong no = new AtomicLong(0);

	private Connection connection;
	private final ObjectPool<ConnectionImpl> pool;

	final long connNo;
	private final long opened;
	private boolean closed;

	private volatile long callNo = 0;
	private volatile long statements = 0;
	volatile long executes = 0;
	private volatile long transactions = 0;

	private final GenericKeyedPool<String, PreparedStatementImpl> preparedStatementPool;

	/**
	 * @param connection
	 *            the connection
	 * @param pool
	 *            the datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ConnectionImpl(Connection connection, ObjectPool<ConnectionImpl> pool) {
		super();
		this.connection = connection;
		this.pool = pool;

		this.connNo = ConnectionImpl.no.incrementAndGet();
		this.opened = System.currentTimeMillis();
		this.preparedStatementPool = new GenericKeyedPool<String, PreparedStatementImpl>(new PreparedStatementFactory(this));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void abort(Executor executor) throws SQLException {
		this.throwNotImplemented();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clearWarnings() throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() throws SQLException {
		if (this.pool != null) {
			try {
				this.pool.returnObject(this);

				this.closed = true;
			}
			catch (final Exception e) {
				if (e instanceof SQLException) {
					throw (SQLException) e;
				}

				throw new SQLException(e);
			}
		}
	}

	/* package */void close0() throws SQLException {
		final long callNo = ++this.callNo;

		ConnectionImpl.LOG.trace("{0}:{1} close()", this.connNo, callNo);

		final long start = System.currentTimeMillis();
		try {
			try {
				if (this.connection != null) {
					this.connection.close();
				}
			}
			catch (final Exception e) {
				this.handlePoolException(e);
			}

			ConnectionImpl.LOG.trace("Connection {0}, used for {1} msecs, transactions= {2}, statements= {3}, executes= {4}", this.connNo,
				System.currentTimeMillis() - this.opened, this.transactions, this.statements, this.executes);
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				ConnectionImpl.LOG.warn(new OperationTookLongTimeWarning(), "{0}:{1} {2} msecs, close()", this.connNo, callNo, time);
			}
			else {
				ConnectionImpl.LOG.trace("{0}:{1} {2} msecs, close()", this.connNo, callNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void commit() throws SQLException {
		if (!ConnectionImpl.LOG.isDebugEnabled()) {
			this.connection.commit();

			return;
		}

		final long callNo = ++this.callNo;

		ConnectionImpl.LOG.trace("{0}:{1} commit()", this.connNo, callNo);

		final long start = System.currentTimeMillis();
		try {
			this.connection.commit();
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				ConnectionImpl.LOG.warn(new OperationTookLongTimeWarning(), "{0}:{1} {2} msecs, commit()", this.connNo, callNo, time);
			}
			else {
				ConnectionImpl.LOG.trace("{0}:{1} {2} msecs, commit()", this.connNo, callNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Blob createBlob() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Clob createClob() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public NClob createNClob() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Statement createStatement() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		this.throwNotImplemented();
		return false;
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

	private void handlePoolException(Exception e) throws SQLException {
		if (e instanceof SQLException) {
			throw (SQLException) e;
		}

		throw new SQLException(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return this.closed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		return this.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		return this.isValid(timeout);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.isWrapperFor(iface);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		return this.nativeSQL(sql);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if (this.preparedStatementPool == null) {
			return this.prepareStatement0(sql);
		}
		try {
			return this.preparedStatementPool.borrowObject(sql);
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
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/* package */PreparedStatementImpl prepareStatement0(String sql) throws SQLException {
		if (!ConnectionImpl.LOG.isDebugEnabled()) {
			return new PreparedStatementImpl(this, sql, this.connection.prepareStatement(sql), this.preparedStatementPool);
		}

		final long callNo = ++this.callNo;

		ConnectionImpl.LOG.trace("{0}:{1} prepareStatement(String): {2}", this.connNo, callNo, ConnectionImpl.LOG.lazyBoxed(sql));

		final long start = System.currentTimeMillis();
		try {
			return new PreparedStatementImpl(this, sql, this.connection.prepareStatement(sql), this.preparedStatementPool);
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				ConnectionImpl.LOG.warn(new OperationTookLongTimeWarning(), "{0}:{1} {2} msecs, prepareStatement(String): {3}", this.connNo, callNo, time,
					ConnectionImpl.LOG.lazyBoxed(sql));
			}
			else {
				ConnectionImpl.LOG.debug("{0}:{1} {2} msecs, prepareStatement(String)", this.connNo, callNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void rollback() throws SQLException {
		if (!ConnectionImpl.LOG.isDebugEnabled()) {
			if (this.connection != null) {
				this.connection.rollback();
			}
		}

		final long callNo = ++this.callNo;

		ConnectionImpl.LOG.trace("{0}:{1} rollback()", this.connNo, callNo);

		final long start = System.currentTimeMillis();
		try {
			if (this.connection != null) {
				this.connection.rollback();
			}
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				ConnectionImpl.LOG.warn(new OperationTookLongTimeWarning(), "{0}:{1} {2} msecs, rollback()", this.connNo, callNo, time);
			}
			else {
				ConnectionImpl.LOG.trace("{0}:{1} {2} msecs, rollback()", this.connNo, callNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (!ConnectionImpl.LOG.isDebugEnabled()) {
			this.connection.setAutoCommit(autoCommit);

			return;
		}

		final long callNo = ++this.callNo;

		ConnectionImpl.LOG.trace("{0}:{1} setAutoCommit(boolean): {2}", this.connNo, callNo, autoCommit);

		final long start = System.currentTimeMillis();
		try {
			this.connection.setAutoCommit(autoCommit);
		}
		finally {
			ConnectionImpl.LOG.trace("{0}:{1} {2} msecs, setAutoCommit(boolean): {3}", this.connNo, callNo, System.currentTimeMillis() - start, autoCommit);
		}
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
		this.setClientInfo(properties);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		this.setClientInfo(name, value);
	}

	/**
	 * Sets the connection.
	 * 
	 * @param connection
	 *            the connection to set
	 * @since $version
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
		this.closed = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setHoldability(int holdability) throws SQLException {
		this.setHoldability(holdability);
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
		return this.setSavepoint(name);
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
		this.setTransactionIsolation(level);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.setTypeMap(map);
	}

	private void throwNotImplemented() {
		throw new NotImplementedException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ConnectionImpl [connNo=" + this.connNo + ", connected=" + (this.connection != null) + ", opened=" + this.opened + ", callNo=" + this.callNo
			+ ", statements=" + this.statements + ", executes=" + this.executes + ", transactions=" + this.transactions + "]";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

}

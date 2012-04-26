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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.OperationTookLongTimeWarning;
import org.batoo.jpa.core.pool.GenericKeyedPool;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class PreparedStatementImpl implements PreparedStatement {

	private static final BLogger LOG = BLogger.getLogger(PreparedStatementImpl.class);

	private static AtomicLong no = new AtomicLong(0);

	private final ConnectionImpl connection;
	private final PreparedStatement statement;
	private final long statementNo;

	private final long opened;

	private volatile long executeNo;
	private volatile long executes;
	private volatile long selects;

	private final String sql;

	private Object[] parameters;

	private final GenericKeyedPool<String, PreparedStatementImpl> pool;

	private ParameterMetaData parameterMetaData;

	/**
	 * @param connection
	 *            the connection
	 * @param sql
	 *            the sql
	 * @param statement
	 *            the delegate statement
	 * @param connNo
	 *            the connection no
	 * 
	 * @since $version
	 * @author hceylan
	 * @param pool
	 */
	public PreparedStatementImpl(ConnectionImpl connection, String sql, PreparedStatement statement,
		GenericKeyedPool<String, PreparedStatementImpl> pool) {
		super();

		this.connection = connection;
		this.sql = sql;
		this.statement = statement;
		this.pool = pool;

		this.statementNo = no.incrementAndGet();
		this.opened = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addBatch() throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addBatch(String sql) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void cancel() throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clearBatch() throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clearParameters() throws SQLException {
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
		if (this.pool == null) { // we are pooled
			this.close0();
		}
		else {
			try {
				this.pool.returnObject(this.sql, this);
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
		final long executeNo = ++this.executeNo;

		LOG.trace("{0}:{1}:{2} close()", this.connection.connNo, this.statementNo, executeNo);

		final long start = System.currentTimeMillis();
		try {
			this.statement.close();

			LOG.trace("{0}:{1}, used for {2} msecs, executes= {3}, selects= {4}", this.connection.connNo, this.statementNo,
				System.currentTimeMillis() - this.opened, this.executes, this.selects);
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				LOG.trace(new OperationTookLongTimeWarning(), "{0}:{1}:{2} {3} msecs, close()", this.connection.connNo, this.statementNo,
					executeNo, time);
			}
			else {
				LOG.trace("{0}:{1}:{2} {3} msecs, close()", this.connection.connNo, this.statementNo, executeNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void closeOnCompletion() throws SQLException {
		this.throwNotImplemented();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean execute() throws SQLException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean execute(String sql) throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int[] executeBatch() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ResultSet executeQuery() throws SQLException {
		final long executeNo = ++this.executeNo;
		this.executes++;
		this.selects++;

		LOG.debug("{0}:{1}:{2} executeQuery(){3}", this.connection.connNo, this.statementNo, executeNo,
			BLogger.lazyBoxed(this.sql, this.parameters));

		final long start = System.currentTimeMillis();
		try {
			this.connection.executes++;

			return this.statement.executeQuery();
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				LOG.warn(new OperationTookLongTimeWarning(), "{0}:{1}:{2} {3} msecs, executeQuery()", this.connection.connNo,
					this.statementNo, executeNo, time);
			}
			else {
				LOG.trace("{0}:{1}:{2} {3} msecs, executeQuery()", this.connection.connNo, this.statementNo, executeNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate() throws SQLException {
		final long executeNo = ++this.executeNo;
		this.executes++;

		LOG.debug("{0}:{1}:{2} executeUpdate(){3}", this.connection.connNo, this.statementNo, executeNo,
			BLogger.lazyBoxed(this.sql, this.parameters));

		final long start = System.currentTimeMillis();
		try {
			this.connection.executes++;

			return this.statement.executeUpdate();
		}
		finally {
			final long time = System.currentTimeMillis() - start;

			if (time > BJPASettings.WARN_TIME) {
				LOG.warn(new OperationTookLongTimeWarning(), "{0}:{1}:{2} {3} msecs, executeUpdate()", this.connection.connNo,
					this.statementNo, executeNo, time);
			}
			else {
				LOG.debug("{0}:{1}:{2} {3} msecs, executeUpdate()", this.connection.connNo, this.statementNo, executeNo, time);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate(String sql) throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Connection getConnection() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getFetchDirection() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getFetchSize() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getMaxFieldSize() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getMaxRows() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean getMoreResults() throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean getMoreResults(int current) throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		if (this.parameterMetaData != null) {
			return this.parameterMetaData;
		}

		this.parameterMetaData = this.statement.getParameterMetaData();

		if (this.parameters == null) {
			this.parameters = new Object[this.parameterMetaData.getParameterCount()];
		}

		return this.parameterMetaData;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getQueryTimeout() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getResultSetConcurrency() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getResultSetHoldability() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getResultSetType() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getUpdateCount() throws SQLException {
		this.throwNotImplemented();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isClosed() throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isPoolable() throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		this.throwNotImplemented();
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCursorName(String name) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFetchDirection(int direction) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFetchSize(int rows) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setMaxRows(int max) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.parameters[parameterIndex - 1] = null;

		this.statement.setNull(parameterIndex, sqlType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.throwNotImplemented();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.parameters[parameterIndex - 1] = x;

		this.statement.setObject(parameterIndex, x);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.throwNotImplemented();

	}

	private void throwNotImplemented() {
		throw new NotImplementedException();
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

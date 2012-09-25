/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.batoo.jpa.core.impl.jdbc.dbutils;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

/**
 * The base class for QueryRunner & AsyncQueryRunner. This class is thread safe.
 * 
 * @since 1.4 (mostly extracted from QueryRunner)
 */
public abstract class AbstractQueryRunner {
	/**
	 * Is {@link ParameterMetaData#getParameterType(int)} broken (have we tried it yet)?
	 */
	private volatile boolean pmdKnownBroken = false;

	/**
	 * The DataSource to retrieve connections from.
	 */
	protected final DataSource ds;

	/**
	 * Default constructor, sets pmdKnownBroken to false and ds to null.
	 */
	public AbstractQueryRunner() {
		this.ds = null;
	}

	/**
	 * Constructor to allow workaround for Oracle drivers
	 * 
	 * @param pmdKnownBroken
	 *            Oracle drivers don't support {@link ParameterMetaData#getParameterType(int) }; if <code>pmdKnownBroken</code> is set to
	 *            true, we won't even try it; if false, we'll try it, and if it breaks, we'll remember not to use it again.
	 */
	public AbstractQueryRunner(boolean pmdKnownBroken) {
		this.pmdKnownBroken = pmdKnownBroken;
		this.ds = null;
	}

	/**
	 * Constructor to provide a <code>DataSource</code>. Methods that do not take a <code>Connection</code> parameter will retrieve
	 * connections from this <code>DataSource</code>.
	 * 
	 * @param ds
	 *            The <code>DataSource</code> to retrieve connections from.
	 */
	public AbstractQueryRunner(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * Constructor to allow workaround for Oracle drivers. Methods that do not take a <code>Connection</code> parameter will retrieve
	 * connections from this <code>DataSource</code>.
	 * 
	 * @param ds
	 *            The <code>DataSource</code> to retrieve connections from.
	 * @param pmdKnownBroken
	 *            Oracle drivers don't support {@link ParameterMetaData#getParameterType(int) }; if <code>pmdKnownBroken</code> is set to
	 *            true, we won't even try it; if false, we'll try it, and if it breaks, we'll remember not to use it again.
	 */
	public AbstractQueryRunner(DataSource ds, boolean pmdKnownBroken) {
		this.pmdKnownBroken = pmdKnownBroken;
		this.ds = ds;
	}

	/**
	 * Close a <code>Connection</code>. This implementation avoids closing if null and does <strong>not</strong> suppress any exceptions.
	 * Subclasses can override to provide special handling like logging.
	 * 
	 * @param conn
	 *            Connection to close
	 * @throws SQLException
	 *             if a database access error occurs
	 * @since DbUtils 1.1
	 */
	protected void close(Connection conn) throws SQLException {
		DbUtils.close(conn);
	}

	/**
	 * Close a <code>ResultSet</code>. This implementation avoids closing if null and does <strong>not</strong> suppress any exceptions.
	 * Subclasses can override to provide special handling like logging.
	 * 
	 * @param rs
	 *            ResultSet to close
	 * @throws SQLException
	 *             if a database access error occurs
	 * @since DbUtils 1.1
	 */
	protected void close(ResultSet rs) throws SQLException {
		DbUtils.close(rs);
	}

	/**
	 * Close a <code>Statement</code>. This implementation avoids closing if null and does <strong>not</strong> suppress any exceptions.
	 * Subclasses can override to provide special handling like logging.
	 * 
	 * @param stmt
	 *            Statement to close
	 * @throws SQLException
	 *             if a database access error occurs
	 * @since DbUtils 1.1
	 */
	protected void close(Statement stmt) throws SQLException {
		DbUtils.close(stmt);
	}

	/**
	 * Fill the <code>PreparedStatement</code> replacement parameters with the given objects.
	 * 
	 * @param stmt
	 *            PreparedStatement to fill
	 * @param params
	 *            Query replacement parameters; <code>null</code> is a valid value to pass in.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {

		// check the parameter count, if we can
		ParameterMetaData pmd = null;
		if (!this.pmdKnownBroken) {
			pmd = stmt.getParameterMetaData();
			final int stmtCount = pmd.getParameterCount();
			final int paramsCount = params == null ? 0 : params.length;

			if (stmtCount != paramsCount) {
				throw new SQLException("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
			}
		}

		// nothing to do here
		if (params == null) {
			return;
		}

		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			}
			else {
				// VARCHAR works with many drivers regardless
				// of the actual column type. Oddly, NULL and
				// OTHER don't work with Oracle's drivers.
				int sqlType = Types.VARCHAR;
				if (!this.pmdKnownBroken) {
					try {
						sqlType = pmd.getParameterType(i + 1);
					}
					catch (final SQLException e) {
						this.pmdKnownBroken = true;
					}
				}
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	/**
	 * Returns the <code>DataSource</code> this runner is using. <code>QueryRunner</code> methods always call this method to get the
	 * <code>DataSource</code> so subclasses can provide specialized behavior.
	 * 
	 * @return DataSource the runner is using
	 */
	public DataSource getDataSource() {
		return this.ds;
	}

	/**
	 * Oracle drivers don't support {@link ParameterMetaData#getParameterType(int) }; if <code>pmdKnownBroken</code> is set to true, we won't
	 * even try it; if false, we'll try it, and if it breaks, we'll remember not to use it again.
	 * 
	 * @return the flag to skip (or not) {@link ParameterMetaData#getParameterType(int) }
	 * @since 1.4
	 */
	public boolean isPmdKnownBroken() {
		return this.pmdKnownBroken;
	}

	/**
	 * Factory method that creates and initializes a <code>Connection</code> object. <code>QueryRunner</code> methods always call this
	 * method to retrieve connections from its DataSource. Subclasses can override this method to provide special <code>Connection</code>
	 * configuration if needed. This implementation simply calls <code>ds.getConnection()</code>.
	 * 
	 * @return An initialized <code>Connection</code>.
	 * @throws SQLException
	 *             if a database access error occurs
	 * @since DbUtils 1.1
	 */
	protected Connection prepareConnection() throws SQLException {
		if (this.getDataSource() == null) {
			throw new SQLException("QueryRunner requires a DataSource to be " + "invoked in this way, or a Connection should be passed in");
		}
		return this.getDataSource().getConnection();
	}

	/**
	 * Factory method that creates and initializes a <code>PreparedStatement</code> object for the given SQL. <code>QueryRunner</code>
	 * methods always call this method to prepare statements for them. Subclasses can override this method to provide special
	 * PreparedStatement configuration if needed. This implementation simply calls <code>conn.prepareStatement(sql)</code>.
	 * 
	 * @param conn
	 *            The <code>Connection</code> used to create the <code>PreparedStatement</code>
	 * @param sql
	 *            The SQL statement to prepare.
	 * @return An initialized <code>PreparedStatement</code>.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {

		return conn.prepareStatement(sql);
	}

	/**
	 * Throws a new exception with a more informative error message.
	 * 
	 * @param cause
	 *            The original exception that will be chained to the new exception when it's rethrown.
	 * 
	 * @param sql
	 *            The query that was executing when the exception happened.
	 * 
	 * @param params
	 *            The query replacement parameters; <code>null</code> is a valid value to pass in.
	 * 
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	protected void rethrow(SQLException cause, String sql, Object... params) throws SQLException {

		String causeMessage = cause.getMessage();
		if (causeMessage == null) {
			causeMessage = "";
		}
		final StringBuffer msg = new StringBuffer(causeMessage);

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (params == null) {
			msg.append("[]");
		}
		else {
			msg.append(Arrays.deepToString(params));
		}

		final SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
		e.setNextException(cause);

		throw e;
	}

	/**
	 * Wrap the <code>ResultSet</code> in a decorator before processing it. This implementation returns the <code>ResultSet</code> it is
	 * given without any decoration.
	 * 
	 * <p>
	 * Often, the implementation of this method can be done in an anonymous inner class like this:
	 * </p>
	 * 
	 * <pre>
	 * QueryRunner run = new QueryRunner() {
	 * 	protected ResultSet wrap(ResultSet rs) {
	 * 		return StringTrimmedResultSet.wrap(rs);
	 * 	}
	 * };
	 * </pre>
	 * 
	 * @param rs
	 *            The <code>ResultSet</code> to decorate; never <code>null</code>.
	 * @return The <code>ResultSet</code> wrapped in some decorator.
	 */
	protected ResultSet wrap(ResultSet rs) {
		return rs;
	}

}

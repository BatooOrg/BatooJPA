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
package org.batoo.jpa.jdbc.dbutils;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.batoo.jpa.jdbc.PreparedStatementProxy;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;

/**
 * Executes SQL queries with pluggable strategies for handling <code>ResultSet</code>s. This class is thread safe.
 * <p>
 * This class's code is based on original Apache DBUtils {@link org.apache.commons.dbutils.QueryRunner} class.
 * 
 * @see ResultSetHandler
 */
public class QueryRunner {

	private final boolean hasLob;
	private final DataSource datasource;

	private JdbcAdaptor jdbcAdaptor;
	private boolean pmdKnownBroken = false;
	private ParameterMetaData pmd;

	/**
	 * Constructor for QueryRunner.
	 */
	public QueryRunner() {
		super();

		this.hasLob = false;
		this.datasource = null;
	}

	/**
	 * Constructor for QueryRunner, allows workaround for Oracle drivers
	 * 
	 * @param pmdKnownBroken
	 *            Oracle drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) }; if <code>pmdKnownBroken</code> is
	 *            set to true, we won't even try it; if false, we'll try it, and if it breaks, we'll remember not to use it again.
	 * @param hasLob
	 *            if the query has lob parameters
	 */
	public QueryRunner(boolean pmdKnownBroken, boolean hasLob) {
		super();

		this.pmdKnownBroken = pmdKnownBroken;
		this.hasLob = hasLob;
		this.datasource = null;
	}

	/**
	 * Constructor for QueryRunner which takes a <code>DataSource</code>. Methods that do not take a <code>Connection</code> parameter will
	 * retrieve connections from this <code>DataSource</code>.
	 * 
	 * @param datasource
	 *            The <code>DataSource</code> to retrieve connections from.
	 */
	public QueryRunner(DataSource datasource) {
		super();

		this.hasLob = false;
		this.datasource = datasource;
	}

	/**
	 * Constructor for QueryRunner, allows workaround for Oracle drivers. Methods that do not take a <code>Connection</code> parameter will
	 * retrieve connections from this <code>DataSource</code>.
	 * 
	 * @param datasource
	 *            The <code>DataSource</code> to retrieve connections from.
	 * @param pmdKnownBroken
	 *            Oracle drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) }; if <code>pmdKnownBroken</code> is
	 *            set to true, we won't even try it; if false, we'll try it, and if it breaks, we'll remember not to use it again.
	 */
	public QueryRunner(DataSource datasource, boolean pmdKnownBroken) {
		super();

		this.hasLob = false;
		this.datasource = datasource;
	}

	/**
	 * Constructor for QueryRunner, allows workaround for Oracle drivers
	 * 
	 * @param jdbcAdaptor
	 *            the JDBC adaptor
	 * @param hasLob
	 *            if the query has lob parameters
	 */
	public QueryRunner(JdbcAdaptor jdbcAdaptor, boolean hasLob) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;
		this.pmdKnownBroken = jdbcAdaptor.isPmdBroken();
		this.hasLob = hasLob;
		this.datasource = null;
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
	 * @return SQLException if a database access error occurs
	 */
	private SQLException convertSqlException(SQLException cause, String sql, Object... params) {
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

		return e;
	}

	/**
	 * Fill the <code>PreparedStatement</code> replacement parameters with the given objects.
	 * 
	 * @param statement
	 *            PreparedStatement to fill
	 * @param params
	 *            Query replacement parameters; <code>null</code> is a valid value to pass in.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	private void fillStatement(PreparedStatement statement, Object... params) throws SQLException {
		// use local variable for performance
		boolean pmdKnownBroken = this.pmdKnownBroken;
		ParameterMetaData pmd = this.pmd;
		final boolean hasLob = this.hasLob;

		if (pmdKnownBroken) {
			((PreparedStatementProxy) statement).setParamCount(params.length);
		}

		// if the jdbc adaptor wants to modify the parameters we let it do it its own way
		final JdbcAdaptor jdbcAdaptor = this.jdbcAdaptor;
		if ((jdbcAdaptor != null) && jdbcAdaptor.modifiesParameters()) {
			pmd = this.pmd = statement.getParameterMetaData();

			jdbcAdaptor.modifyParameters(pmd, params);
		}

		for (int i = 0; i < params.length; i++) {
			final Object param = params[i];
			if (param != null) {
				if (hasLob && (param instanceof Clob)) {
					statement.setClob(i + 1, (Clob) param);
				}
				else if (hasLob && (param instanceof Blob)) {
					statement.setBlob(i + 1, (Blob) param);
				}
				else {
					statement.setObject(i + 1, param);
				}
			}
			else {
				if (!pmdKnownBroken && (pmd == null)) {
					pmd = this.pmd = statement.getParameterMetaData();
				}

				// VARCHAR works with many drivers regardless of the actual column type.
				// Oddly, NULL and OTHER don't work with Oracle's drivers.
				int sqlType = Types.VARCHAR;
				if (!pmdKnownBroken) {
					try {
						sqlType = pmd.getParameterType(i + 1);
					}
					catch (final SQLException e) {
						pmdKnownBroken = this.pmdKnownBroken = true;
					}
				}

				statement.setNull(i + 1, sqlType);
			}
		}
	}

	/**
	 * Calls query after checking the parameters to ensure nothing is null.
	 * 
	 * @param conn
	 *            The connection to use for the query call.
	 * @param closeConn
	 *            True if the connection should be closed, false otherwise.
	 * @param sql
	 *            The SQL statement to execute.
	 * @param params
	 *            An array of query replacement parameters. Each row in this array is one set of batch replacement values.
	 * @return The results of the query.
	 * @throws SQLException
	 *             If there are database or parameter errors.
	 */
	private <T> T query(Connection conn, boolean closeConn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = conn.prepareStatement(sql);
			if (params != null) {
				this.fillStatement(statement, params);
			}

			resultSet = statement.executeQuery();

			return rsh.handle(resultSet);
		}
		catch (final SQLException e) {
			throw this.convertSqlException(e, sql, params);
		}
		finally {
			try {
				DbUtils.close(resultSet);
			}
			finally {
				DbUtils.close(statement);

				if (closeConn) {
					DbUtils.close(conn);
				}
			}
		}
	}

	/**
	 * Execute an SQL SELECT query without any replacement parameters. The caller is responsible for closing the connection.
	 * 
	 * @param <T>
	 *            The type of object that the handler returns
	 * @param connection
	 *            The connection to execute the query in.
	 * @param sql
	 *            The query to execute.
	 * @param rsh
	 *            The handler that converts the results into an object.
	 * @return The object returned by the handler.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public <T> T query(Connection connection, String sql, ResultSetHandler<T> rsh) throws SQLException {
		return this.query(connection, false, sql, rsh, (Object[]) null);
	}

	/**
	 * Execute an SQL SELECT query with replacement parameters. The caller is responsible for closing the connection.
	 * 
	 * @param <T>
	 *            The type of object that the handler returns
	 * @param connection
	 *            The connection to execute the query in.
	 * @param sql
	 *            The query to execute.
	 * @param rsh
	 *            The handler that converts the results into an object.
	 * @param params
	 *            The replacement parameters.
	 * @return The object returned by the handler.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public <T> T query(Connection connection, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
		return this.query(connection, false, sql, rsh, params);
	}

	/**
	 * Executes the given SELECT SQL without any replacement parameters. The <code>Connection</code> is retrieved from the
	 * <code>DataSource</code> set in the constructor.
	 * 
	 * @param <T>
	 *            The type of object that the handler returns
	 * @param sql
	 *            The SQL statement to execute.
	 * @param rsh
	 *            The handler used to create the result object from the <code>ResultSet</code>.
	 * 
	 * @return An object generated by the handler.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
		return this.query(this.datasource.getConnection(), true, sql, rsh, (Object[]) null);
	}

	/**
	 * Executes the given SELECT SQL query and returns a result object. The <code>Connection</code> is retrieved from the
	 * <code>DataSource</code> set in the constructor.
	 * 
	 * @param <T>
	 *            The type of object that the handler returns
	 * @param sql
	 *            The SQL statement to execute.
	 * @param rsh
	 *            The handler used to create the result object from the <code>ResultSet</code>.
	 * @param params
	 *            Initialize the PreparedStatement's IN parameters with this array.
	 * @return An object generated by the handler.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
		return this.query(this.datasource.getConnection(), true, sql, rsh, params);
	}

	/**
	 * Calls update after checking the parameters to ensure nothing is null.
	 * 
	 * @param connection
	 *            The connection to use for the update call.
	 * @param closeConn
	 *            True if the connection should be closed, false otherwise.
	 * @param sql
	 *            The SQL statement to execute.
	 * @param params
	 *            An array of update replacement parameters. Each row in this array is one set of update replacement values.
	 * @return The number of rows updated.
	 * @throws SQLException
	 *             If there are database or parameter errors.
	 */
	private int update(Connection connection, boolean closeConn, String sql, Object... params) throws SQLException {
		if (connection == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			if (closeConn) {
				DbUtils.close(connection);
			}
			throw new SQLException("Null SQL statement");
		}

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			if (params != null) {
				this.fillStatement(statement, params);
			}

			return statement.executeUpdate();
		}
		catch (final SQLException e) {
			throw this.convertSqlException(e, sql, params);
		}
		finally {
			DbUtils.close(statement);

			if (closeConn) {
				DbUtils.close(connection);
			}
		}
	}

	/**
	 * Execute an SQL INSERT, UPDATE, or DELETE query without replacement parameters.
	 * 
	 * @param connection
	 *            The connection to use to run the query.
	 * @param sql
	 *            The SQL to execute.
	 * @return The number of rows updated.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public int update(Connection connection, String sql) throws SQLException {
		return this.update(connection, false, sql, (Object[]) null);
	}

	/**
	 * Execute an SQL INSERT, UPDATE, or DELETE query.
	 * 
	 * @param connection
	 *            The connection to use to run the query.
	 * @param sql
	 *            The SQL to execute.
	 * @param params
	 *            The query replacement parameters.
	 * @return The number of rows updated.
	 * @throws SQLException
	 *             if a database access error occurs
	 */
	public int update(Connection connection, String sql, Object... params) throws SQLException {
		return this.update(connection, false, sql, params);
	}

	/**
	 * Executes the given INSERT, UPDATE, or DELETE SQL statement without any replacement parameters. The <code>Connection</code> is
	 * retrieved from the <code>DataSource</code> set in the constructor. This <code>Connection</code> must be in auto-commit mode or the
	 * update will not be saved.
	 * 
	 * @param sql
	 *            The SQL statement to execute.
	 * @throws SQLException
	 *             if a database access error occurs
	 * @return The number of rows updated.
	 */
	public int update(String sql) throws SQLException {
		return this.update(this.datasource.getConnection(), true, sql, (Object[]) null);
	}

	/**
	 * Executes the given INSERT, UPDATE, or DELETE SQL statement. The <code>Connection</code> is retrieved from the <code>DataSource</code>
	 * set in the constructor. This <code>Connection</code> must be in auto-commit mode or the update will not be saved.
	 * 
	 * @param sql
	 *            The SQL statement to execute.
	 * @param params
	 *            Initializes the PreparedStatement's IN (i.e. '?') parameters.
	 * @throws SQLException
	 *             if a database access error occurs
	 * @return The number of rows updated.
	 */
	public int update(String sql, Object... params) throws SQLException {
		return this.update(this.datasource.getConnection(), true, sql, params);
	}
}

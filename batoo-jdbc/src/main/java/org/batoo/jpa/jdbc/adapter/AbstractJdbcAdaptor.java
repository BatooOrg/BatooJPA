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
package org.batoo.jpa.jdbc.adapter;

import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder.Trimspec;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractJdbcAdaptor {

	private static final Map<String, JdbcAdaptor> ADAPTERS = AbstractJdbcAdaptor.prepareAdaptors();

	/**
	 * Returns the JDBC Adapter.
	 * 
	 * @param classloader
	 *            the class loader
	 * @param databaseProductName
	 *            the name of the database product
	 * 
	 * @return the adapters
	 * @since 2.0.0
	 */
	public static JdbcAdaptor getAdapter(ClassLoader classloader, String databaseProductName) {
		final JdbcAdaptor jdbcAdaptor = AbstractJdbcAdaptor.ADAPTERS.get(databaseProductName);

		if (jdbcAdaptor == null) {
			throw new PersistenceException("Cannot locate JDBC Adaptor for '" + databaseProductName + "'");
		}

		try {
			return jdbcAdaptor.getClass().newInstance();
		}
		catch (final Exception e) {}

		return null;
	}

	private static Map<String, JdbcAdaptor> prepareAdaptors() {
		final Map<String, JdbcAdaptor> adaptors = Maps.newHashMap();

		AbstractJdbcAdaptor.putAdaptor(adaptors, new DerbyAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new MySqlAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new MsSqlAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new OracleAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new H2Adaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new HsqlAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new PostgreSqlAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new SawSqlAdaptor());

		return adaptors;
	}

	private static void putAdaptor(Map<String, JdbcAdaptor> adaptors, JdbcAdaptor adaptor) {
		for (final String productName : adaptor.getProductNames()) {
			adaptors.put(productName, adaptor);
		}
	}

	/**
	 * 
	 * @since 2.0.0
	 */
	public AbstractJdbcAdaptor() {
		super();
	}

	/**
	 * Applies cast operation to the expression.
	 * 
	 * @param argument
	 *            the SQL argument
	 * @param clazz
	 *            the class to cast
	 * @return the casted expression
	 * 
	 * @since 2.0.0
	 */
	public String applyCast(String argument, Class<?> clazz) {
		final String className = this.getCastClassName(clazz);

		return "CAST(" + argument + " AS " + className + ")";
	}

	/**
	 * Applies the concat operation to the arguments.
	 * 
	 * @param arguments
	 *            the arguments
	 * @return the concat SQL fragment
	 * 
	 * @since 2.0.0
	 */
	public abstract String applyConcat(List<String> arguments);

	/**
	 * Appends the escape pattern.
	 * 
	 * @param escapePattern
	 *            the escape pattern
	 * @return the sql fragment to escape
	 * 
	 * @since 2.0.0
	 */
	public abstract String applyLikeEscape(String escapePattern);

	/**
	 * Applies the lock to SQL string.
	 * 
	 * @param sql
	 *            the SQL to apply
	 * @param lockMode
	 *            the lock mode
	 * @return the modified SQL
	 * 
	 * @since 2.0.0
	 */
	public abstract String applyLock(String sql, LockModeType lockMode);

	/**
	 * Applies the pagination to SQL string.
	 * 
	 * @param sql
	 *            the SQL to apply
	 * @param startPosition
	 *            the start position
	 * @param maxResult
	 *            the max number of results
	 * @return the modified SQL
	 * 
	 * @since 2.0.0
	 */
	public abstract String applyPagination(String sql, int startPosition, int maxResult);

	/**
	 * Returns the sub string function.
	 * 
	 * @param innerFragment
	 *            the inner fragment
	 * @param startFragment
	 *            the start fragment
	 * @param endFragment
	 *            the end fragment
	 * @return the sub string function
	 * 
	 * @since 2.0.0
	 */
	public String applySubStr(String innerFragment, String startFragment, String endFragment) {
		return "SUBSTR(" + Joiner.on(", ").skipNulls().join(new Object[] { innerFragment, startFragment, endFragment }) + ")";
	}

	/**
	 * Applies the trim to argument
	 * 
	 * @param trimspec
	 *            the trim spec
	 * @param trimChar
	 *            the trim character
	 * @param argument
	 *            the argument
	 * @return the trim SQL fragment
	 * 
	 * @since 2.0.0
	 */
	public String applyTrim(Trimspec trimspec, String trimChar, String argument) {
		final StringBuilder builder = new StringBuilder("TRIM(");

		if (trimspec != null) {
			builder.append(trimspec.toString()).append(" ");
		}

		if (trimChar != null) {
			builder.append(trimChar).append(" ");
		}

		if ((trimspec != null) || (trimChar != null)) {
			builder.append("FROM ");
		}

		return builder.append(argument).append(")").toString();
	}

	/**
	 * Casts the expression to boolean if necessary.
	 * 
	 * @param sqlFragment
	 *            the SQL Fragment
	 * @return the cast SQL fragment
	 */
	public String castBoolean(String sqlFragment) {
		return sqlFragment;
	}

	/**
	 * Returns the clazz to DB type data type.
	 * 
	 * @param clazz
	 *            the original cast class
	 * @return the converted DB type name
	 * 
	 * @since 2.0.0
	 */
	protected String getCastClassName(Class<?> clazz) {
		if (clazz == String.class) {
			return "VARCHAR";
		}

		return clazz.getSimpleName();
	}

	/**
	 * @return the JDBC Driver this adapter works with
	 * 
	 * @since 2.0.0
	 */
	protected abstract String[] getProductNames();
}

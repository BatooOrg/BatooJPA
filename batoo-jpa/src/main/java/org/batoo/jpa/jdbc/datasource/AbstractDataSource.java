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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Abstract class for implementing pluggable data source
 *
 * @author asimarslan
 * @since 2.0.1
 */
public abstract class AbstractDataSource implements DataSource {

	/**
	 * finalize the underlining implementation
	 *
	 * @since 2.0.1
	 */
	public abstract void close();

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public abstract Connection getConnection() throws SQLException;

	/**
	 * initialize the underlining implementation
	 *
	 * @param persistenceUnitName
	 *            the name of the persistence unit
	 * @param properties
	 *            the datasource properties
	 *
	 * @since 2.0.1
	 */

	public abstract void open(String persistenceUnitName, Map<String, Object> properties);

	/**
	 * release the connection
	 *
	 * @param connection
	 *            the connection
	 *
	 * @since 2.0.1
	 */
	public abstract void releaseConnection(Connection connection);

}

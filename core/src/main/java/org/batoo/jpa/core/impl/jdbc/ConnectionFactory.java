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

import java.sql.SQLException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;

import com.google.common.annotations.Beta;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Beta
public class ConnectionFactory extends BasePoolableObjectFactory<ConnectionImpl> {

	private static final BLogger LOG = BLoggerFactory.getLogger(ConnectionFactory.class);

	private final DataSourceImpl dataSource;

	/**
	 * @param dataSource
	 *            the datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ConnectionFactory(DataSourceImpl dataSource) {
		super();

		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void activateObject(ConnectionImpl obj) throws Exception {
		obj.reset();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void destroyObject(ConnectionImpl obj) throws Exception {
		obj.close0();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ConnectionImpl makeObject() throws SQLException {
		return this.dataSource.getConnection0();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void passivateObject(ConnectionImpl obj) throws Exception {
		try {
			obj.setAutoCommit(true);
		}
		catch (final Exception e) {
			ConnectionFactory.LOG.error(e, "Error while returning connection");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean validateObject(ConnectionImpl obj) {
		try {
			return obj.isValid(DataSourceImpl.MAX_WAIT);
		}
		catch (final SQLException e) {
			return false;
		}
	}

}

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

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;

import com.google.common.annotations.Beta;

/**
 * A Factory to implement preparation of {@link PreparedStatementImpl}s
 * 
 * @author hceylan
 * @since $version
 */
@Beta
public class PreparedStatementFactory extends BaseKeyedPoolableObjectFactory<String, PreparedStatementImpl> {

	private final ConnectionImpl connection;

	/**
	 * @param connection
	 *            the connection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PreparedStatementFactory(ConnectionImpl connection) {
		super();

		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void destroyObject(String key, PreparedStatementImpl obj) throws SQLException {
		obj.close0();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PreparedStatementImpl makeObject(String key) throws SQLException {
		return this.connection.prepareStatement0(key);
	}

}

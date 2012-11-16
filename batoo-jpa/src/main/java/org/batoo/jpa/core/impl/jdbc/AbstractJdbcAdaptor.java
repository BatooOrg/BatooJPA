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

import java.util.Map;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.jdbc.adapter.DerbyAdaptor;
import org.batoo.jpa.core.jdbc.adapter.H2Adaptor;
import org.batoo.jpa.core.jdbc.adapter.HsqlAdaptor;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.core.jdbc.adapter.MsSqlAdaptor;
import org.batoo.jpa.core.jdbc.adapter.MySqlAdaptor;
import org.batoo.jpa.core.jdbc.adapter.OracleAdaptor;
import org.batoo.jpa.core.jdbc.adapter.PostgreSqlAdaptor;
import org.batoo.jpa.core.jdbc.adapter.SawSqlAdaptor;

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
	 * @return the JDBC Driver this adapter works with
	 * 
	 * @since 2.0.0
	 */
	protected abstract String[] getProductNames();
}

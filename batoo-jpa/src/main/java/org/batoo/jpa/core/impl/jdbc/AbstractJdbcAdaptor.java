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

import org.batoo.jpa.core.jdbc.adapter.DerbyAdaptor;
import org.batoo.jpa.core.jdbc.adapter.HsqlAdaptor;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.core.jdbc.adapter.MySqlAdaptor;
import org.batoo.jpa.core.jdbc.adapter.PostgreSqlAdaptor;

import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since $version
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
	 * @since $version
	 */
	public static JdbcAdaptor getAdapter(ClassLoader classloader, String databaseProductName) {
		return AbstractJdbcAdaptor.ADAPTERS.get(databaseProductName);
	}

	private static Map<String, JdbcAdaptor> prepareAdaptors() {
		final Map<String, JdbcAdaptor> adaptors = Maps.newHashMap();

		AbstractJdbcAdaptor.putAdaptor(adaptors, new DerbyAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new MySqlAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new HsqlAdaptor());
		AbstractJdbcAdaptor.putAdaptor(adaptors, new PostgreSqlAdaptor());

		return adaptors;
	}

	private static void putAdaptor(Map<String, JdbcAdaptor> adaptors, JdbcAdaptor adaptor) {
		for (final String productName : adaptor.getProductNames()) {
			adaptors.put(productName, adaptor);
		}
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractJdbcAdaptor() {
		super();
	}

	/**
	 * @return the JDBC Driver this adapter works with
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract String[] getProductNames();
}

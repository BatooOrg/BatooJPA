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

import java.util.Map;
import java.util.Properties;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.JPASettings;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * HikariCP datasource wrapper
 *
 * @author lburgazzoli
 * @author asimarslan
 */
public class HikariCPDataSource extends AbstractInternalDataSource {

	private static final BLogger LOGGER = BLoggerFactory.getLogger(HikariCPDataSource.class);

	public static final String CONFIG_PREFIX = "org.batoo.hikaricp.";

	public HikariCPDataSource() {
	}

	@Override
	public void open(String persistenceUnitName, Map<String, Object> mapProperties) {
		try {
			LOGGER.debug("Configuring HikariCP");
			setWrappedDataSource(new HikariDataSource(loadConfiguration(mapProperties)));
		} catch(Exception e) {
			LOGGER.warn("Cannot configure hikariCP cause: ",e);
		}
		LOGGER.debug("HikariCP Configured");
	}

	@Override
	public void close() {
		((HikariDataSource)getWrappedDataSource()).shutdown();
		setWrappedDataSource(null);
	}

	private HikariConfig loadConfiguration(Map<String, Object> props) throws Exception {
		Properties cpProps = cropPrefixFromProperties(props, CONFIG_PREFIX);

		if (props.containsKey(JPASettings.JDBC_DRIVER)) {
			cpProps.setProperty("driverClassName", (String) props.get(JPASettings.JDBC_DRIVER));
		}
		if (props.containsKey(JPASettings.JDBC_URL)) {
			cpProps.setProperty("jdbcUrl", (String) props.get(JPASettings.JDBC_URL));
		}
		if (props.containsKey(JPASettings.JDBC_USER)) {
			cpProps.setProperty("username", (String) props.get(JPASettings.JDBC_USER));
		}
		if (props.containsKey(JPASettings.JDBC_PASSWORD)) {
			cpProps.setProperty("password", (String) props.get(JPASettings.JDBC_PASSWORD));
		}
		return new HikariConfig(cpProps);
	}
}
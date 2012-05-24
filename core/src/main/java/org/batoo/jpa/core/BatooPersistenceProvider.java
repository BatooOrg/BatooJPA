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
package org.batoo.jpa.core;

import java.sql.SQLException;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.core.impl.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.MetamodelImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.impl.PersistenceParser;

/**
 * Implementation of {@link PersistenceProvider}.
 * 
 * @author hceylan
 * @since $version
 */
public class BatooPersistenceProvider implements PersistenceProvider {

	private DataSourceImpl datasource;
	private JdbcAdaptor jdbcAdapter;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactoryImpl createContainerEntityManagerFactory(PersistenceUnitInfo info, @SuppressWarnings("rawtypes") Map map) {
		return null;
	}

	private void createDatasource(PersistenceParser parser) throws SQLException {
		final boolean scanExternal = Boolean.valueOf(parser.getProperty(BJPASettings.SCAN_EXTERNAL_JDBC_DRIVERS));
		final String jdbcDriver = parser.getProperty(JPASettings.JDBC_DRIVER);

		this.jdbcAdapter = AbstractJdbcAdaptor.getAdapter(scanExternal, jdbcDriver);

		final String jdbcUrl = parser.getProperty(JPASettings.JDBC_URL);
		final String jdbcUser = parser.getProperty(JPASettings.JDBC_USER);
		final String jdbcPassword = parser.getProperty(JPASettings.JDBC_PASSWORD);

		this.datasource = new DataSourceImpl(jdbcUrl, jdbcUser, jdbcPassword);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactory createEntityManagerFactory(String emName, @SuppressWarnings("rawtypes") Map map) {
		// create the persistence parser
		final PersistenceParser parser = new PersistenceParser(emName);

		// create the datasource
		try {
			this.createDatasource(parser);
		}
		catch (final SQLException e) {
			throw new BatooException("Cannot create the datasource", e);
		}

		// create the metamodel
		final MetamodelImpl metamodel = new MetamodelImpl(parser.getMetadata());

		// finally, create the entity manager factory
		return new EntityManagerFactoryImpl(emName, metamodel, this.datasource);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ProviderUtil getProviderUtil() {
		// TODO Auto-generated method stub
		return null;
	}

}

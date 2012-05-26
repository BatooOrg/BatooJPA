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
package org.batoo.jpa.core.impl.manager;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.JPASettings;
import org.batoo.jpa.core.impl.manager.deployment.DdlManager;
import org.batoo.jpa.core.impl.manager.jdbc.AbstractJdbcAdaptor;
import org.batoo.jpa.core.impl.manager.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.manager.model.MetamodelImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.PersistenceParser;

import com.google.common.collect.Sets;

/**
 * Implementation of {@link EntityManagerFactory}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

	private final String name;
	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;
	private final JdbcAdaptor jdbcAdaptor;
	private final Map<String, Object> properties;
	private boolean open;

	/**
	 * @param name
	 *            the name of the entity manager factory
	 * @param parser
	 *            the persistence parser
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerFactoryImpl(String name, PersistenceParser parser) {
		super();

		this.name = name;
		this.jdbcAdaptor = this.createJdbcAdaptor(parser);
		this.datasource = this.createDatasource(parser);
		this.metamodel = new MetamodelImpl(this.jdbcAdaptor, parser.getMetadata());
		this.properties = parser.getProperties();

		final Set<String> schemas = Sets.newHashSet();
		DdlManager.perform(this.datasource, this.metamodel, schemas, DDLMode.DROP);

		this.open = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() {
		this.open = false;
	}

	private DataSourceImpl createDatasource(PersistenceParser parser) {
		final String jdbcUrl = (String) parser.getProperty(JPASettings.JDBC_URL);
		final String jdbcUser = (String) parser.getProperty(JPASettings.JDBC_USER);
		final String jdbcPassword = (String) parser.getProperty(JPASettings.JDBC_PASSWORD);

		try {
			return new DataSourceImpl(jdbcUrl, jdbcUser, jdbcPassword);
		}
		catch (final SQLException e) {
			throw new BatooException("Datasource cannot be created", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManager createEntityManager() {
		return new EntityManagerImpl(this, this.metamodel, this.datasource);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManager createEntityManager(Map map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates the JDBC adaptor
	 * 
	 * @param parser
	 *            the persistence parser
	 * @return the JDBC Adaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private JdbcAdaptor createJdbcAdaptor(PersistenceParser parser) {
		final boolean scanExternal = Boolean.valueOf((String) parser.getProperty(BJPASettings.SCAN_EXTERNAL_JDBC_DRIVERS));
		final String jdbcDriver = (String) parser.getProperty(JPASettings.JDBC_DRIVER);

		return AbstractJdbcAdaptor.getAdapter(scanExternal, jdbcDriver);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Cache getCache() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Metamodel getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return this.open;
	}
}

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
package org.batoo.jpa.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.JPASettings;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.manager.DdlManager;
import org.batoo.jpa.core.impl.manager.HLinkerManager;
import org.batoo.jpa.core.impl.manager.ParserManager;
import org.batoo.jpa.core.impl.manager.VLinkerManager;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class EntityManagerFactoryImpl implements EntityManagerFactory {

	private class PersistenceWorkerFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "Batoo Worker [" + nextThreadNo.incrementAndGet() + "]");
		}

	}

	private static final BLogger LOG = BLogger.getLogger(EntityManagerFactoryImpl.class);

	private static final AtomicInteger nextThreadNo = new AtomicInteger(0);

	private final ExecutorService executor;
	private final Configuration configuration;
	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;

	private boolean open;

	/**
	 * 
	 * 
	 * @param embeddables
	 * @param mappedSuperTypes
	 * @param entities
	 * @param properties
	 * @throws Throwable
	 *             in case of a deployment error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerFactoryImpl(Configuration configuration, MetamodelImpl metamodel) throws Throwable {
		super();

		this.configuration = configuration;
		this.metamodel = metamodel;

		LOG.info("Entity Manager Factory for {0} is being initializing...", configuration.getPersistenceUnitName());

		final long start = System.currentTimeMillis();
		final String jdbcDriver = this.configuration.getProperty(JPASettings.JDBC_DRIVER);
		final String jdbcUrl = this.configuration.getProperty(JPASettings.JDBC_URL);
		final String jdbcUser = this.configuration.getProperty(JPASettings.JDBC_USER);
		final String jdbcPassword = this.configuration.getProperty(JPASettings.JDBC_PASSWORD);

		final DDLMode ddlMode = this.configuration.getProperty(JPASettings.DDL, DDLMode.DROP);

		final boolean scanExternal = Boolean.valueOf((String) this.configuration.getProperty(JPASettings.SCAN_EXTERNAL_JDBC_DRIVERS));

		this.metamodel.setJdbcAdapter(JDBCAdapter.getAdapter(scanExternal, jdbcDriver));
		this.datasource = new DataSourceImpl(jdbcUrl, jdbcUser, jdbcPassword);

		ParserManager.parse(this.metamodel); // parse
		VLinkerManager.link(this.metamodel); // vlink
		HLinkerManager.link(this.metamodel, this.datasource); // hlink
		this.metamodel.linkAssociations();

		// ddl
		if (ddlMode == DDLMode.NONE) {
			LOG.info("Configured DDL mode is {0}. Skiping DDL operations...", ddlMode);
		}
		else {
			LOG.info("Configured DDL mode is {0}", ddlMode);

			final Set<String> schemas = Sets.newHashSet();
			this.metamodel.performSequencesDdl(schemas, this.datasource, ddlMode);
			this.metamodel.performGeneratorTablesDdl(schemas, this.datasource, ddlMode);

			DdlManager.perform(this.datasource, this.metamodel, schemas, ddlMode);
		}

		this.open = true;

		LOG.info("Entity Manager Factory for {0} is has been initialized successfully in {1} msecs",
			configuration.getPersistenceUnitName(), System.currentTimeMillis() - start);

		final Set<EntityType<?>> entities = this.metamodel.getEntities();
		LOG.info("List of {0} entities avaliable:\n{1}", entities.size(), this.reportEntity(entities));

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executor = Executors.newFixedThreadPool(nThreads, new PersistenceWorkerFactory());

		LOG.info("Number of worker threads is {0}", nThreads);

		this.metamodel.preFillGenerators(this.datasource);

		this.open = true;
		this.metamodel.markDeployed();
	}

	private void assertOpen() {
		if (!this.open) {
			throw new IllegalStateException("EntityManagerFactory has been previously closed");
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() {
		this.assertOpen();

		this.datasource.close();
		this.metamodel.getIdGeneratorExecutor().shutdown();

		this.open = false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManager createEntityManager() {
		this.assertOpen();

		return this.createEntityManager(Collections.emptyMap());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManager createEntityManager(@SuppressWarnings("rawtypes") Map map) {
		this.assertOpen();

		return new EntityManagerImpl(this, map, this.datasource);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Cache getCache() {
		this.assertOpen();

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		this.assertOpen();

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the executor.
	 * 
	 * @return the executor
	 * @since $version
	 */
	public ExecutorService getExecutor() {
		return this.executor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetamodelImpl getMetamodel() {
		this.assertOpen();

		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		this.assertOpen();

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getProperties() {
		this.assertOpen();

		return Maps.newHashMap(this.configuration.getProperties());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOpen() {
		return this.open;
	}

	protected String reportAttributes(Set<?> attributes) {
		final List<AttributeImpl<?, ?>> list = Lists.newArrayList();
		for (final Object attribute : attributes) {
			list.add((AttributeImpl<?, ?>) attribute);
		}
		Collections.sort(list);

		final Collection<String> transformed = Collections2.transform(list, new Function<Object, String>() {

			@Override
			public String apply(Object input) {
				final StringBuffer out = new StringBuffer();
				final Attribute<?, ?> attribute = (Attribute<?, ?>) input;

				if (attribute instanceof SingularAttribute) {
					if (((SingularAttribute<?, ?>) attribute).isId()) {
						out.append("*");
					}
				}

				switch (attribute.getPersistentAttributeType()) {
					case EMBEDDED:
					case MANY_TO_MANY:
					case MANY_TO_ONE:
						out.append("#");
				}

				out.append(attribute.getName());

				switch (attribute.getPersistentAttributeType()) {
					case ELEMENT_COLLECTION:
					case MANY_TO_MANY:
					case ONE_TO_MANY:
						out.append("[]");
				}

				out.append(": ");
				out.append(attribute.getJavaType().getSimpleName());

				return out.toString();
			}
		});

		return Joiner.on(", ").join(transformed);
	}

	private String reportEntity(Set<EntityType<?>> entities) {
		final ArrayList<EntityTypeImpl<?>> list = Lists.newArrayList();
		for (final EntityType<?> entity : entities) {
			list.add((EntityTypeImpl<?>) entity);
		}
		Collections.sort(list);

		final Collection<String> transformed = Collections2.transform(list, new Function<EntityType<?>, String>() {

			@Override
			public String apply(EntityType<?> input) {
				return "\t" + input.getName() + ": [" + EntityManagerFactoryImpl.this.reportAttributes(input.getAttributes()) + "]";
			}
		});

		return Joiner.on(",\n").join(transformed);
	}

}

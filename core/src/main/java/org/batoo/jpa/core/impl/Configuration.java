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

import java.net.URL;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.JPASettings;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.PersistenceProvider;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.mapping.TypeFactory;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class Configuration {

	private static final BLogger LOG = BLogger.getLogger(Configuration.class);

	private final PersistenceUnitInfo info;
	private final Map<String, Object> properties;

	/**
	 * @param properties
	 * @param info
	 * 
	 * @since $version
	 * 
	 * @author hceylan
	 */
	public Configuration(PersistenceUnitInfo info, Map<String, Object> properties) {
		super();

		this.info = info;
		this.properties = properties;
	}

	/**
	 * @return the created entity manager factory
	 * @throws MappingException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerFactory buildEntityManagerFactory() throws MappingException {
		if (!this.checkProvider()) {
			return null;
		}

		if (this.info.getClassLoader() == null) {
			throw new IllegalStateException("ClassLoader is null: " + this.getPersistenceUnitName());
		}

		final MetamodelImpl metaModel = new MetamodelImpl();

		// if managed classes specified then build on them
		if (this.info.getManagedClassNames().size() > 0) {
			TypeFactory.scanForArtifacts(metaModel, this.info.getManagedClassNames());
		}
		else {
			// build on class loader artifacts
			final String rootPackage = (String) this.info.getProperties().get(JPASettings.ROOT_PACKAGE);

			for (final URL jarUrl : this.info.getJarFileUrls()) {
				TypeFactory.scanForArtifacts(metaModel, jarUrl, rootPackage);
			}

			TypeFactory.scanForArtifacts(metaModel, this.info.getPersistenceUnitRootUrl(), rootPackage);
		}

		LOG.debug("Collected {0} Entities, {1} MappedSuperClass, {2} Embeddable", metaModel.getEntities().size(),
			metaModel.getMappedSuperclasses().size(), metaModel.getEmbeddables().size());

		try {
			return new EntityManagerFactoryImpl(this, metaModel);
		}
		catch (final Throwable e) {
			LOG.error(e, "Cannot create EntityManagerFactory: {0}", e.getMessage());

			return null;
		}
	}

	private boolean checkProvider() {
		String provider = (String) this.properties.get(JPASettings.PROVIDER);

		if (provider == null) {
			provider = this.info.getPersistenceProviderClassName();
		}

		// Check if we are called right
		if ((provider != null) && !provider.trim().startsWith(PersistenceProvider.class.getName())) {
			Configuration.LOG.error("Invalid provider {0}", provider);

			return false;
		}

		return true;
	}

	/**
	 * @return the persistence unit name
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public String getPersistenceUnitName() {
		return this.info.getPersistenceUnitName();
	}

	/**
	 * Returns the properties.
	 * 
	 * @return the properties
	 * @since $version
	 */
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * @param key
	 *            the property key
	 * @return the value or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> X getProperty(String key) {
		return (X) this.properties.get(key);
	}

	/**
	 * @param key
	 *            the property key
	 * @return the value or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> X getProperty(String key, X def) {
		final Object value = this.properties.get(key);
		return value != null ? (X) value : def;
	}

	@SuppressWarnings("unchecked")
	public <X extends Enum<X>> X getProperty(String key, X def) {
		final Object value = this.properties.get(key);

		if (value == null) {
			return null;
		}

		return (X) Enum.valueOf(def.getClass(), (String) value);
	}
}

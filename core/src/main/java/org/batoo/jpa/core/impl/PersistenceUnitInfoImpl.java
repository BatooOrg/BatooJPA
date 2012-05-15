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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BJPASettings;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.xml.Persistence;
import org.batoo.jpa.xml.Persistence.PersistenceUnit;
import org.batoo.jpa.xml.Persistence.PersistenceUnit.Properties.Property;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {

	//
	private static final String PERSISTENCE_XML = "META-INF/persistence.xml";

	private static final String SCHEMA_VERSION = "2.0";

	private ClassLoader classLoader;

	private final String emName;

	private PersistenceUnit persistenceUnit;

	private final Map<String, String> properties;

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 * @param emName
	 * @param map
	 * @throws BatooException
	 */
	public PersistenceUnitInfoImpl(String emName, Map<String, String> properties) throws BatooException {
		super();

		if (StringUtils.isEmpty(emName)) {
			throw new IllegalArgumentException("Entity Manager Name is null");
		}

		this.emName = emName;

		this.init();

		this.properties = Maps.newHashMap();
		for (final Property property : this.persistenceUnit.getProperties().getProperty()) {
			this.properties.put(property.getName(), property.getValue());
		}

		this.properties.putAll(properties);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addTransformer(ClassTransformer transformer) {
		throw new NotImplementedException();
	}

	private ClassLoader createClassLoader() {
		return this.createClassLoaderImpl();
	}

	private ClassLoader createClassLoaderImpl() {
		final String className = this.getProperty(BJPASettings.CLASS_LOADER_CLASS);

		if (className == null) {
			return Thread.currentThread().getContextClassLoader();
		}

		try {
			final Class<?> clazz = Class.forName(className);

			try {
				final Constructor<?> constructor = clazz.getConstructor(ClassLoader.class);
				if (constructor != null) {
					return (ClassLoader) constructor.newInstance(Thread.currentThread().getContextClassLoader());
				}
			}
			catch (final NoSuchMethodException e) {
				// noop
			}

			return (ClassLoader) clazz.newInstance();
		}
		catch (final ClassNotFoundException e) {
			throw new IllegalArgumentException("Classloader not found: " + className);
		}
		catch (final InstantiationException e) {
			throw new IllegalArgumentException("Classloader " + className + " does not have correct constructors");
		}
		catch (final IllegalAccessException e) {
			throw new RuntimeException("Error instantiating the classloader: " + className, e);
		}
		catch (final SecurityException e) {
			throw new RuntimeException("Error instantiating the classloader: " + className, e);
		}
		catch (final InvocationTargetException e) {
			throw new RuntimeException("Error instantiating the classloader: " + className, e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean excludeUnlistedClasses() {
		return this.persistenceUnit.getExcludeUnlistedClasses();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ClassLoader getClassLoader() {
		if (this.classLoader != null) {
			return this.classLoader;
		}

		this.classLoader = this.createClassLoader();

		return this.classLoader;
	}

	private DataSource getDatasource(String datasource) {
		InitialContext context;
		try {
			context = new InitialContext();
			return (DataSource) context.lookup(datasource);
		}
		catch (final NamingException e) {
			throw new IllegalArgumentException("Unable to find Datasource: " + datasource, e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<URL> getJarFileUrls() {
		final List<String> jarFiles = this.persistenceUnit.getJarFile();

		return Lists.transform(jarFiles, new Function<String, URL>() {

			@Override
			public URL apply(String input) {
				try {
					return new URL(input);
				}
				catch (final MalformedURLException e) {
					throw new IllegalArgumentException("Invalid jar file: " + input, e);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getJtaDataSource() {
		return this.getDatasource(this.persistenceUnit.getJtaDataSource());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<String> getManagedClassNames() {
		return this.persistenceUnit.getClazz();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<String> getMappingFileNames() {
		return Collections.unmodifiableList(this.persistenceUnit.getMappingFile());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ClassLoader getNewTempClassLoader() {
		return this.createClassLoader();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getNonJtaDataSource() {
		return this.getDatasource(this.persistenceUnit.getNonJtaDataSource());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPersistenceProviderClassName() {
		return this.persistenceUnit.getProvider();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPersistenceUnitName() {
		return this.emName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public URL getPersistenceUnitRootUrl() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPersistenceXMLSchemaVersion() {
		return PersistenceUnitInfoImpl.SCHEMA_VERSION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Properties getProperties() {
		final Properties properties = new Properties();

		properties.putAll(this.properties);

		return properties;
	}

	private String getProperty(String key) {
		final List<Property> properties = this.persistenceUnit.getProperties().getProperty();

		if (this.properties.containsKey(key)) {
			return this.properties.get(key);
		}

		for (final Property property : properties) {
			if (key.equals(property.getName())) {
				return property.getValue();
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SharedCacheMode getSharedCacheMode() {
		if (this.persistenceUnit.getSharedCacheMode() == null) {
			return null;
		}

		return SharedCacheMode.valueOf(this.upper(this.persistenceUnit.getSharedCacheMode().name()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		if (this.persistenceUnit.getTransactionType() == null) {
			return null;
		}

		return PersistenceUnitTransactionType.valueOf(this.upper(this.persistenceUnit.getTransactionType().name()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ValidationMode getValidationMode() {
		if (this.persistenceUnit.getValidationMode() == null) {
			return null;
		}

		return ValidationMode.valueOf(this.upper(this.persistenceUnit.getValidationMode().name()));
	}

	/**
	 * Initializes the persistence unit
	 * 
	 * @since $version
	 * @author hceylan
	 * @throws BatooException
	 */
	private void init() throws BatooException {
		try {
			Enumeration<URL> xmls;

			// Try to load the persistence xml from the thread's class loader
			xmls = Thread.currentThread().getContextClassLoader().getResources(PERSISTENCE_XML);

			if (!xmls.hasMoreElements()) {
				// Fall back to load the persistence xml from the class's class loader
				xmls = this.getClass().getClassLoader().getResources(PERSISTENCE_XML);
			}

			if (!xmls.hasMoreElements()) {
				throw new BatooException("persistence.xml not found in the classpath");
			}

			final JAXBContext context = JAXBContext.newInstance(Persistence.class);
			final Unmarshaller unmarshaller = context.createUnmarshaller();

			while (xmls.hasMoreElements()) {
				final URL xmlUrl = xmls.nextElement();

				final Persistence persistence = (Persistence) unmarshaller.unmarshal(xmlUrl);
				for (final PersistenceUnit persistenceUnit : persistence.getPersistenceUnit()) {
					if (this.emName.equals(persistenceUnit.getName())) {
						this.persistenceUnit = persistenceUnit;

						return;
					}
				}
			}

		}
		catch (final JAXBException e) {
			throw new BatooException("Unable to create JAXBContext", e);
		}
		catch (final IOException e) {
			throw new BatooException("Unable to load persistence.xml", e);
		}

		throw new BatooException("Unable to find persistence.xml");
	}

	private String upper(String input) {
		return input.toUpperCase(Locale.ENGLISH);
	}
}

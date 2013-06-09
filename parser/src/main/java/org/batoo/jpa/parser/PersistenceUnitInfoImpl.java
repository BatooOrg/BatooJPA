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
package org.batoo.jpa.parser;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.batoo.common.BatooException;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.parser.persistence.Persistence;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit.Properties.Property;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {

	private static final BLogger LOG = BLoggerFactory.getLogger(PersistenceUnitInfoImpl.class);

	private final ClassLoader classLoader;
	private final boolean excludeUnlistedClasses;
	private final List<URL> jarFiles;
	private final DataSource jtaDataSource;
	private final List<String> managedClassNames;
	private final List<String> mappingFileNames;
	private final DataSource nonJtaDataSource;
	private final String persistenceProviderClassName;
	private final String persistenceUnitName;
	private final URL persistenceUnitRootUrl;
	private final Properties properties;
	private final SharedCacheMode sharedCacheMode;
	private final PersistenceUnitTransactionType transactionType;
	private final ValidationMode validationMode;

	/**
	 * @param puName
	 *            the name of the persistence unit
	 * 
	 * @since 2.0.0
	 */
	public PersistenceUnitInfoImpl(String puName) {
		super();

		this.classLoader = Thread.currentThread().getContextClassLoader();

		final Object[] pair = this.createPersistenceUnit(puName);
		this.persistenceUnitRootUrl = (URL) pair[0];
		final PersistenceUnit pu = (PersistenceUnit) pair[1];

		this.jarFiles = this.createJarFiles(pu);
		this.jtaDataSource = this.createDatasource(pu.getJtaDataSource());
		this.managedClassNames = pu.getClazzs();
		this.excludeUnlistedClasses = this.createExcludeUnlistedProperties(pu);
		this.mappingFileNames = pu.getMappingFiles();
		this.nonJtaDataSource = this.createDatasource(pu.getNonJtaDataSource());
		this.persistenceProviderClassName = pu.getProvider();
		this.persistenceUnitName = puName;
		this.sharedCacheMode = this.createSharedCacheMode(pu);
		this.transactionType = this.createTransactionType(pu);
		this.validationMode = this.createValidationMode(pu);
		this.properties = this.createProperties(pu);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addTransformer(ClassTransformer transformer) {
		// noop
	}

	private DataSource createDatasource(String jndiName) {
		if (jndiName == null) {
			return null;
		}

		try {
			return (DataSource) new InitialContext().lookup(jndiName);
		}
		catch (final NamingException e) {
			throw new PersistenceException("Cannot lookup datasource: " + jndiName);
		}
	}

	private boolean createExcludeUnlistedProperties(final PersistenceUnit pu) {
		return pu.isExcludeUnlistedClasses() != null ? pu.isExcludeUnlistedClasses() : !this.managedClassNames.isEmpty();
	}

	private List<URL> createJarFiles(PersistenceUnit pu) {
		return Lists.transform(pu.getJarFiles(), new Function<String, URL>() {

			@Override
			public URL apply(String input) {
				try {
					return new URL(input);
				}
				catch (final MalformedURLException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	private Object[] createPersistenceUnit(String puName) {
		try {
			PersistenceUnitInfoImpl.LOG.info("Loading persistence.xml");

			final Enumeration<URL> resources = this.classLoader.getResources(PersistenceParserImpl.PERSISTENCE_XML);
			while (resources.hasMoreElements()) {
				final URL url = resources.nextElement();

				final PersistenceUnit unit = this.findPersistence(puName, url);
				if (unit != null) {
					return new Object[] {
						new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(0,
							url.getFile().length() - PersistenceParserImpl.PERSISTENCE_XML.length() - 1)), unit };
				}
			}
		}
		catch (final Exception e) {
			throw new BatooException("Unable to parse persistence.xml", e);
		}

		throw new BatooException("Persistence unit " + puName + " not found.");
	}

	private Properties createProperties(PersistenceUnit pu) {
		final Properties properties = new Properties();

		if (pu.getProperties() != null) {
			for (final Property property : pu.getProperties().getProperties()) {
				properties.put(property.getName(), property.getValue());
			}
		}

		return properties;
	}

	private SharedCacheMode createSharedCacheMode(PersistenceUnit pu) {
		if (pu.getSharedCacheMode() == null) {
			return SharedCacheMode.NONE;
		}

		switch (pu.getSharedCacheMode()) {
			case ALL:
				return SharedCacheMode.ALL;

			case DISABLE_SELECTIVE:
				return SharedCacheMode.DISABLE_SELECTIVE;

			case ENABLE_SELECTIVE:
				return SharedCacheMode.ENABLE_SELECTIVE;

			case UNSPECIFIED:
				return SharedCacheMode.UNSPECIFIED;

			default:
				return SharedCacheMode.NONE;
		}
	}

	private PersistenceUnitTransactionType createTransactionType(PersistenceUnit pu) {
		if (pu.getTransactionType() == null) {
			return PersistenceUnitTransactionType.RESOURCE_LOCAL;
		}

		switch (pu.getTransactionType()) {
			case JTA:
				return PersistenceUnitTransactionType.JTA;

			default:
				return PersistenceUnitTransactionType.RESOURCE_LOCAL;
		}
	}

	private ValidationMode createValidationMode(PersistenceUnit pu) {
		if (pu.getValidationMode() == null) {
			return ValidationMode.NONE;
		}

		switch (pu.getValidationMode()) {
			case AUTO:
				return ValidationMode.AUTO;

			case CALLBACK:
				return ValidationMode.CALLBACK;

			default:
				return ValidationMode.NONE;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean excludeUnlistedClasses() {
		return this.excludeUnlistedClasses;
	}

	/**
	 * Finds and returns the persistence unit.
	 * 
	 * @param puName
	 * @param persistenceXmlUrl
	 * @return the persistence unit
	 * 
	 * @since 2.0.0
	 */
	private PersistenceUnit findPersistence(String puName, URL url) {
		InputStream is;
		try {
			is = url.openStream();

			final javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Persistence.class);
			final javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();

			final Persistence persistence = (Persistence) unmarshaller.unmarshal(is);
			for (final PersistenceUnit persistenceUnit : persistence.getPersistenceUnits()) {
				if (puName.equals(persistenceUnit.getName())) {
					return persistenceUnit;
				}
			}

			return null;
		}
		catch (final Exception e) {
			PersistenceUnitInfoImpl.LOG.error(e, "Cannot read persistence.xml at location {0}", url);

			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<URL> getJarFileUrls() {
		return Collections.unmodifiableList(this.jarFiles);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getJtaDataSource() {
		return this.jtaDataSource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<String> getManagedClassNames() {
		return Collections.unmodifiableList(this.managedClassNames);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<String> getMappingFileNames() {
		return Collections.unmodifiableList(this.mappingFileNames);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ClassLoader getNewTempClassLoader() {
		return this.classLoader;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getNonJtaDataSource() {
		return this.nonJtaDataSource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPersistenceProviderClassName() {
		return this.persistenceProviderClassName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPersistenceUnitName() {
		return this.persistenceUnitName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public URL getPersistenceUnitRootUrl() {
		return this.persistenceUnitRootUrl;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPersistenceXMLSchemaVersion() {
		return "2.0";
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SharedCacheMode getSharedCacheMode() {
		return this.sharedCacheMode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceUnitTransactionType getTransactionType() {
		return this.transactionType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ValidationMode getValidationMode() {
		return this.validationMode;
	}
}

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.impl.OrmParser;
import org.batoo.jpa.parser.impl.metadata.MetadataImpl;
import org.batoo.jpa.parser.persistence.Persistence;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit.Properties.Property;
import org.batoo.jpa.parser.persistence.PersistenceUnitCachingType;
import org.batoo.jpa.parser.persistence.PersistenceUnitValidationModeType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The main entry point to parse the persistence units.
 * 
 * @author hceylan
 * @since $version
 */
public class PersistenceParserImpl implements PersistenceParser {

	/**
	 * The name of the Persistence XML file.
	 */
	public static final String PERSISTENCE_XML = "META-INF/persistence.xml";

	/**
	 * The default name of the ORM XML File.
	 */
	private static final String ORM_XML = "META-INF/orm.xml";

	private static final BLogger LOG = BLoggerFactory.getLogger(PersistenceParserImpl.class);

	private final String puName;
	private final ClassLoader classloader;
	private final MetadataImpl metadata;
	private final Map<String, Object> properties = Maps.newHashMap();
	private final List<String> ormMappingFiles;

	private final boolean hasValidators;
	private final SharedCacheMode sharedCacheMode;
	private final DataSource jtaDataSource;
	private final DataSource nonJtaDataSource;

	/**
	 * @param puInfo
	 *            the persistence unit info
	 * @param properties
	 *            the properties of the persistence unit
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceParserImpl(PersistenceUnitInfo puInfo, Map<String, Object> properties) {
		super();

		this.puName = puInfo.getPersistenceUnitName();
		this.classloader = puInfo.getClassLoader();

		for (final Entry<Object, Object> entry : puInfo.getProperties().entrySet()) {
			this.properties.put((String) entry.getKey(), entry.getValue());
		}

		for (final Entry<String, Object> entry : properties.entrySet()) {
			this.properties.put(entry.getKey(), entry.getValue());
		}

		this.hasValidators = (puInfo.getValidationMode() == ValidationMode.AUTO) || (puInfo.getValidationMode() == ValidationMode.CALLBACK);
		this.metadata = new MetadataImpl();
		this.ormMappingFiles = puInfo.getMappingFileNames();

		this.parseOrmXmls();

		this.metadata.parse(puInfo.getJarFileUrls(), this.classloader);
		this.sharedCacheMode = puInfo.getSharedCacheMode();
		this.jtaDataSource = puInfo.getJtaDataSource();
		this.nonJtaDataSource = puInfo.getNonJtaDataSource();
	}

	/**
	 * @param puName
	 *            the name of the persistence unit.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceParserImpl(String puName) {
		super();

		this.puName = puName;
		this.classloader = Thread.currentThread().getContextClassLoader();

		final PersistenceUnit puInfo = this.createPersistenceUnit();

		for (final Property entry : puInfo.getProperties().getProperties()) {
			this.properties.put(entry.getName(), entry.getValue());
		}

		this.hasValidators = (puInfo.getValidationMode() == PersistenceUnitValidationModeType.AUTO)
			|| (puInfo.getValidationMode() == PersistenceUnitValidationModeType.CALLBACK);
		this.metadata = new MetadataImpl(puInfo.getClazzs());
		this.ormMappingFiles = puInfo.getMappingFiles();

		this.parseOrmXmls();

		this.metadata.parse(this.classloader);
		switch (puInfo.getSharedCacheMode() != null ? puInfo.getSharedCacheMode() : PersistenceUnitCachingType.NONE) {
			case ALL:
				this.sharedCacheMode = SharedCacheMode.ALL;
				break;
			case DISABLE_SELECTIVE:
				this.sharedCacheMode = SharedCacheMode.DISABLE_SELECTIVE;
				break;
			case ENABLE_SELECTIVE:
				this.sharedCacheMode = SharedCacheMode.ENABLE_SELECTIVE;
				break;
			case NONE:
				this.sharedCacheMode = SharedCacheMode.NONE;
				break;
			default:
				this.sharedCacheMode = SharedCacheMode.NONE;
				break;
		}

		this.jtaDataSource = null;
		this.nonJtaDataSource = null;
	}

	/**
	 * @param emName
	 *            the name of the entity manager
	 * @param properties
	 *            the list of properties
	 * @param classes
	 *            the array of classes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceParserImpl(String emName, Map<String, String> properties, String[] classes) {
		super();

		this.puName = emName;
		this.classloader = Thread.currentThread().getContextClassLoader();

		// initialize the persistence unit
		final PersistenceUnit pu = new PersistenceUnit();
		for (final String clazz : classes) {
			pu.getClazzs().add(clazz);
		}

		for (final Entry<String, String> property : properties.entrySet()) {
			this.properties.put(property.getKey(), property.getValue());
		}

		this.properties.putAll(properties);
		this.hasValidators = true;

		this.metadata = new MetadataImpl(pu.getClazzs());
		this.metadata.parse(this.classloader);

		this.ormMappingFiles = Lists.newArrayList();
		this.jtaDataSource = null;
		this.nonJtaDataSource = null;
		this.sharedCacheMode = null;
	}

	/**
	 * Initializes the persistence unit by parsing the Persistence XML File.
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	private PersistenceUnit createPersistenceUnit() {
		try {
			PersistenceParserImpl.LOG.info("Loading persistence.xml");

			final InputStream is = this.classloader.getResourceAsStream(PersistenceParserImpl.PERSISTENCE_XML);
			// Try to load the Persistence XML
			if (is == null) {
				throw new BatooException("persistence.xml not found in the classpath");
			}

			final javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Persistence.class);
			final javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();

			final Persistence persistence = (Persistence) unmarshaller.unmarshal(is);
			for (final PersistenceUnit persistenceUnit : persistence.getPersistenceUnits()) {
				if (this.puName.equals(persistenceUnit.getName())) {
					return persistenceUnit;
				}
			}
		}
		catch (final Exception e) {
			throw new BatooException("Unable to parse persistence.xml", e);
		}

		throw new BatooException("Persistence unit " + this.puName + " not found.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ClassLoader getClassloader() {
		return this.classloader;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getJtaDatasource() {
		return this.jtaDataSource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetadataImpl getMetadata() {
		return this.metadata;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getNonJtaDatasource() {
		return this.nonJtaDataSource;
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
	public SharedCacheMode getSharedCacheMode() {
		return this.sharedCacheMode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasValidators() {
		return this.hasValidators;
	}

	/**
	 * Parses a single ORM XML File.
	 * 
	 * @param mappingFile
	 *            the name of the mapping file
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void parseOrmXml(final String mappingFile) {
		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingFile);

		if (is != null) {
			final OrmParser ormParser = new OrmParser(mappingFile);
			ormParser.consume(is);

			this.metadata.merge(ormParser.getMetadata());

			PersistenceParserImpl.LOG.trace("Merged metamodel {0}", this.metadata);
		}
	}

	/**
	 * Parses the ORM XML Files.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void parseOrmXmls() {
		if (this.ormMappingFiles.size() > 0) {
			for (final String mappingFile : this.ormMappingFiles) {
				this.parseOrmXml(mappingFile);
			}
		}
		else {
			this.parseOrmXml(PersistenceParserImpl.ORM_XML);
		}
	}
}

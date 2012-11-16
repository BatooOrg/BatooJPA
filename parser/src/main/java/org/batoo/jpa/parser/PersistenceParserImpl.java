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
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.parser.impl.OrmParser;
import org.batoo.jpa.parser.impl.metadata.MetadataImpl;

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

	private final PersistenceUnitInfo puInfo;
	private final Map<String, Object> properties = Maps.newHashMap();
	private final String provider;

	private final MetadataImpl metadata;
	private final List<String> ormMappingFiles;

	private final boolean hasValidators;

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

		this.puInfo = puInfo;
		this.provider = null;

		if (puInfo.getProperties() != null) {
			for (final Entry<Object, Object> entry : puInfo.getProperties().entrySet()) {
				this.properties.put((String) entry.getKey(), entry.getValue());
			}
		}

		if (properties != null) {
			for (final Entry<String, Object> entry : properties.entrySet()) {
				this.properties.put(entry.getKey(), entry.getValue());
			}
		}

		this.hasValidators = this.createHasValidators(puInfo);
		this.metadata = new MetadataImpl();
		this.ormMappingFiles = puInfo.getMappingFileNames();

		this.parseOrmXmls();

		this.metadata.parse(puInfo);
	}

	/**
	 * @param puName
	 *            the name of the persistence unit.
	 * @param properties
	 *            the properties for the entity manager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceParserImpl(String puName, Map<String, Object> properties) {
		this(new PersistenceUnitInfoImpl(puName), properties);
	}

	private boolean createHasValidators(PersistenceUnitInfo puInfo) {
		switch (puInfo.getValidationMode()) {
			case CALLBACK:
				return true;
			case NONE:
				return false;
			default:
				try {
					javax.validation.Validation.buildDefaultValidatorFactory();

					return true;
				}
				catch (final Exception e) {
					PersistenceParserImpl.LOG.debug(e, "Validation cannot be enabled");

					PersistenceParserImpl.LOG.warn("Validation mode is set to AUTO, yet no validation implementation seems to be available!" + //
						"\n\tValidation is being turned off." + //
						"\n\tTo avoid this warning set validation-mode in your persistence.xml to NONE." + //
						"\n\tExtra information can be obtained by turning debug log on...");

					return false;
				}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ClassLoader getClassloader() {
		return this.puInfo.getClassLoader();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DataSource getJtaDataSource() {
		return this.puInfo.getJtaDataSource();
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
	public DataSource getNonJtaDataSource() {
		return this.puInfo.getNonJtaDataSource();
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
	public String getProvider() {
		return this.provider;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SharedCacheMode getSharedCacheMode() {
		return this.puInfo.getSharedCacheMode();
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
	 * @param optional
	 *            if the file is optional
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void parseOrmXml(final String mappingFile, boolean optional) {
		final InputStream is = this.puInfo.getClassLoader().getResourceAsStream(mappingFile);

		if (is != null) {
			final OrmParser ormParser = new OrmParser(mappingFile);
			ormParser.consume(is);

			this.metadata.merge(ormParser.getMetadata());

			PersistenceParserImpl.LOG.debug("Merged ORM Metamodel {0}", this.metadata);
		}
		else if (!optional) {
			PersistenceParserImpl.LOG.error("ORM Mapping file {0} could not be found!", mappingFile);
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
				this.parseOrmXml(mappingFile, false);
			}
		}
		else {
			this.parseOrmXml(PersistenceParserImpl.ORM_XML, true);
		}
	}
}

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
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.impl.OrmParser;
import org.batoo.jpa.parser.impl.metadata.MetadataImpl;
import org.batoo.jpa.parser.persistence.Persistence;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit.Properties.Property;

import com.google.common.collect.Maps;

/**
 * The main entry point to parse the persistence units.
 * 
 * @author hceylan
 * @since $version
 */
public class PersistenceParser {

	/**
	 * The name of the Persistence XML file.
	 */
	public static final String PERSISTENCE_XML = "META-INF/persistence.xml";

	/**
	 * The default name of the ORM XML File.
	 */
	private static final String ORM_XML = "META-INF/orm.xml";

	private static final BLogger LOG = BLoggerFactory.getLogger(PersistenceParser.class);

	private final String puName;
	private final MetadataImpl metadata;
	private PersistenceUnit persistenceUnit;
	private final Map<String, Object> properties = Maps.newHashMap();

	/**
	 * @param persistenceUnitName
	 *            the name of the persistence unit
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistenceParser(String persistenceUnitName) {
		super();

		this.puName = persistenceUnitName;

		// initialize the persistence unit
		this.init();
		this.readProperties();

		this.metadata = new MetadataImpl(this.persistenceUnit.getClazzs());

		this.parseOrmXmls();
		this.metadata.parse();
	}

	/**
	 * Returns the metadata of the parser.
	 * 
	 * @return the metadata of the parser
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MetadataImpl getMetadata() {
		return this.metadata;
	}

	/**
	 * Returns the properties of the persistence unit.
	 * 
	 * @return the properties of the persistence unit
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	/**
	 * Initializes the persistence unit by parsing the Persistence XML File.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void init() {
		try {
			PersistenceParser.LOG.info("Loading persistence.xml");

			InputStream is;

			// Try to load the Persistence XML from the thread's class loader
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(PersistenceParser.PERSISTENCE_XML);

			if (is == null) {
				// Fall back to load the Persistence XML from the class's class loader
				is = this.getClass().getClassLoader().getResourceAsStream(PersistenceParser.PERSISTENCE_XML);
			}

			if (is == null) {
				throw new BatooException("persistence.xml not found in the classpath");
			}

			final JAXBContext context = JAXBContext.newInstance(Persistence.class);
			final Unmarshaller unmarshaller = context.createUnmarshaller();

			final Persistence persistence = (Persistence) unmarshaller.unmarshal(is);
			for (final PersistenceUnit persistenceUnit : persistence.getPersistenceUnits()) {
				if (this.puName.equals(persistenceUnit.getName())) {
					this.persistenceUnit = persistenceUnit;

					return;
				}
			}

			throw new BatooException("Persistence unit " + this.puName + " not found in the persistence.xml");
		}
		catch (final JAXBException e) {
			throw new BatooException("Unable to create JAXBContext", e);
		}
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

			PersistenceParser.LOG.trace("Merged metamodel {0}", this.metadata);
		}
	}

	/**
	 * Parses the ORM XML Files.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void parseOrmXmls() {
		if (this.persistenceUnit.getMappingFiles().size() > 0) {
			for (final String mappingFile : this.persistenceUnit.getMappingFiles()) {
				this.parseOrmXml(mappingFile);
			}
		}
		else {
			this.parseOrmXml(PersistenceParser.ORM_XML);
		}
	}

	/**
	 * Reads the properties from the persistence unit.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void readProperties() {
		for (final Property property : this.persistenceUnit.getProperties().getProperties()) {
			this.properties.put(property.getName(), property.getValue());
		}
	}
}

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
package org.batoo.jpa.parser.impl;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.impl.annotated.MetadataImpl;
import org.batoo.jpa.parser.persistence.Persistence;
import org.batoo.jpa.parser.persistence.Persistence.PersistenceUnit;

/**
 * 
 * The main entry point to parse the persistence units.
 * 
 * @author hceylan
 * @since $version
 */
public class PersistenceParser {
	private static final BLogger LOG = BLoggerFactory.getLogger(PersistenceParser.class);

	private static final String PERSISTENCE_XML = "META-INF/persistence.xml";
	private static final String ORM_XML = "META-INF/orm.xml";

	private final String puName;
	private final MetadataImpl metadata;

	private PersistenceUnit persistenceUnit;

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
		this.metadata = new MetadataImpl();

		// parse the Persistence XML
		this.init();

		// parse ORM XML Files phase
		this.parseOrmXmls();

		// parse annotated model
		this.parse();
	}

	/**
	 * Returns the metadata.
	 * 
	 * @return the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MetadataImpl getMetadata() {
		return this.metadata;
	}

	/**
	 * Initializes the persistence unit.
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
	 * Parses the annotated java model.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void parse() {
		new AnnotationParser(this.metadata).parse();

		PersistenceParser.LOG.debug("Final metamodel {0}", this.metadata);
	}

	private void parseOrmXml(final String mappingFile) {
		final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingFile);

		if (is != null) {
			final OrmParser ormParser = new OrmParser(mappingFile);
			ormParser.consume(is);

			this.metadata.merge(ormParser.getMetadata());
		}

		PersistenceParser.LOG.trace("Merged metamodel {0}", this.metadata);
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
}

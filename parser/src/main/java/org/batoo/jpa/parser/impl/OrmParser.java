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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.parser.impl.metadata.Metadata;
import org.batoo.jpa.parser.impl.orm.OrmContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * PersistenceParser for ORM Files.
 * 
 * @author hceylan
 * @since $version
 */
public class OrmParser {

	private final XMLReader reader;
	private final String fileName;
	private final OrmContentHandler handler;

	/**
	 * @param fileName
	 *            the name of the ORM XML file.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OrmParser(String fileName) {
		super();

		this.fileName = fileName;
		this.handler = new OrmContentHandler(this.fileName);

		try {
			this.reader = this.createReader();
		}
		catch (final Exception e) {
			throw new BatooException("Unable to initialize ORM Parser", e);
		}

		this.reader.setContentHandler(this.handler);
	}

	/**
	 * Consumes the ORM file with the given input stream.
	 * 
	 * @param is
	 *            the input stream
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void consume(InputStream is) {
		try {
			this.reader.parse(new InputSource(is));
		}
		catch (final Exception e) {
			throw new BatooException("Unable to parse ORM XML File " + this.fileName, e);
		}
	}

	private XMLReader createReader() throws SAXException, ParserConfigurationException {
		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		parserFactory.setNamespaceAware(true);
		parserFactory.setValidating(false);

		return parserFactory.newSAXParser().getXMLReader();
	}

	/**
	 * Returns the metadata.
	 * 
	 * @return the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Metadata getMetadata() {
		return this.handler.getMetadata();
	}
}

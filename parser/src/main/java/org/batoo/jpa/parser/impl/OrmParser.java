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
package org.batoo.jpa.parser.impl;

import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.batoo.common.BatooException;
import org.batoo.jpa.parser.impl.orm.OrmContentHandler;
import org.batoo.jpa.parser.metadata.Metadata;
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

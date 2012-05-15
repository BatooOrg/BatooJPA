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
package org.batoo.jpa.spec.impl.converter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class OrmReader implements EntityResolver, ContentHandler {

	private final XMLReader reader;
	private Locator locator;

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OrmReader() throws SAXException, ParserConfigurationException {
		super();

		this.reader = this.createReader();

		this.initialize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * @param resource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void consume(InputStream is) throws IOException, SAXException {
		this.reader.parse(new InputSource(is));
	}

	private XMLReader createReader() throws SAXException, ParserConfigurationException {
		SAXParserFactory parserFactory;
		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		// there is no point in asking a validation because
		// there is no guarantee that the document will come with
		// a proper schemaLocation.
		parserFactory.setValidating(false);

		return parserFactory.newSAXParser().getXMLReader();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initialize() {
		this.reader.setEntityResolver(this);
		this.reader.setContentHandler(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		// TODO Auto-generated method stub

	}
}

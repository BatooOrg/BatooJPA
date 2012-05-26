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
package org.batoo.jpa.parser.impl.orm;

import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.Metadata;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

/**
 * The content handler for ORM XML Files.
 * 
 * @author hceylan
 * @since $version
 */
public class OrmContentHandler implements ContentHandler {

	private static final BLogger LOG = BLoggerFactory.getLogger(OrmContentHandler.class);

	private final String fileName;
	private Locator locator;
	private final Stack<Element> elementStack = new Stack<Element>();
	private EntityMappings entityMappings;

	/**
	 * @param fileName
	 *            the name of the ORM XML file
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OrmContentHandler(String fileName) {
		super();

		this.fileName = fileName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String cdata = new String(ch, start, length);
		if (cdata.startsWith("![CDATA[")) {
			cdata = cdata.substring(8, cdata.length() - 2);
		}

		if (StringUtils.isNotBlank(cdata)) {
			OrmContentHandler.LOG.trace("Element body {0}", cdata);

			this.elementStack.peek().cdata(cdata);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void endDocument() throws SAXException {
		OrmContentHandler.LOG.debug("ORM Parsing is over for {0}", this.fileName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		OrmContentHandler.LOG.trace("End of element url: {0}, localName: {1}, qName: {2}", uri, localName, qName);

		final Element element = this.elementStack.pop();

		element.end();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		OrmContentHandler.LOG.trace("End of prefix mapping {0}", prefix);
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
		return this.entityMappings;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		OrmContentHandler.LOG.trace("Ignorable whitespace {0}", new String(ch, start, length));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		OrmContentHandler.LOG.trace("Processing instructions {0}, {1}", target, data);
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
	 * {@inheritedDoc}
	 * 
	 */
	@Override
	public void skippedEntity(String name) throws SAXException {
		OrmContentHandler.LOG.warn("Skipped entity {0}", name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void startDocument() throws SAXException {
		OrmContentHandler.LOG.debug("ORM Parsing started for {0}", this.fileName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		final Map<String, String> attributes = Maps.newHashMap();

		for (int i = 0; i < atts.getLength(); i++) {
			final String name = atts.getLocalName(i);
			final String value = atts.getValue(i);

			attributes.put(name, value);
		}

		OrmContentHandler.LOG.trace("Start of element url: {0}, localName: {1}, qName: {2}, attributes: {3}", uri, localName, qName,
			attributes);

		// if stack is empty we are at the root of the document
		if (this.elementStack.size() == 0) {
			if (!ElementConstants.ELEMENT_ENTITY_MAPPINGS.equals(localName)) {
				throw new MappingException("Unexpected element '" + ElementConstants.ELEMENT_ENTITY_MAPPINGS + "' encountered.",
					new XmlLocator(this.fileName, localName, this.locator));
			}

			this.entityMappings = new EntityMappings(attributes);
			this.elementStack.push(this.entityMappings);
		}
		// check if the element is expected
		else {
			this.elementStack.peek().expected(localName, this.locator);

			// push the new Element to the stack
			final XmlLocator xmlLocator = new XmlLocator(this.fileName, localName, this.locator);
			this.elementStack.push(Element.forElement(this.elementStack.peek(), attributes, xmlLocator));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		OrmContentHandler.LOG.trace("Start of prefix mapping url: {0}, prefix: {1}", uri, prefix);
	}

}

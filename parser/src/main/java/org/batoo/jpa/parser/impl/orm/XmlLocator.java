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

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.xml.sax.Locator;

/**
 * The placemark to remember where the ORM Metadata is obtained from.
 * 
 * @author hceylan
 * @since $version
 */
public class XmlLocator extends AbstractLocator {

	private final int lineNumber;
	private final int columnNumber;
	private final String localName;
	private final String fileName;

	/**
	 * @param fileName
	 *            the name of the ORM XML file
	 * @param localName
	 *            the local name of the tag
	 * @param locator
	 *            the locator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public XmlLocator(String fileName, String localName, Locator locator) {
		super();

		this.fileName = fileName;
		this.localName = localName;

		this.lineNumber = locator.getLineNumber();
		this.columnNumber = locator.getColumnNumber();
	}

	/**
	 * Returns the column number of the location.
	 * 
	 * @return the column number of the location
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getColumnNumber() {
		return this.columnNumber;
	}

	/**
	 * Returns the name of the ORM XML File.
	 * 
	 * @return the name of the ORM XML File
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Returns the line number of the location.
	 * 
	 * @return the line number of the location
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * Returns the local name of the location.
	 * 
	 * @return the local name of the location
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getLocalName() {
		return this.localName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.fileName + "@" + this.lineNumber + ":" + this.columnNumber;
	}
}

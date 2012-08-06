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

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

import java.util.Map;

import org.batoo.jpa.parser.metadata.GeneratorMetadata;

/**
 * Element for <code>sequence-generator</code> and <code>table-generator</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class GeneratorElement extends ParentElement implements GeneratorMetadata {

	private String name;
	private String catalog;
	private String schema;
	private int allocationSize;
	private int initialValue;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public GeneratorElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param childElement
	 *            the name of the element expected
	 * 
	 * @since 2.0.0
	 */
	public GeneratorElement(ParentElement parent, Map<String, String> attributes, String childElement) {
		super(parent, attributes, childElement);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ATTR_NAME, EMPTY);
		this.catalog = this.getAttribute(ATTR_CATALOG, EMPTY);
		this.schema = this.getAttribute(ATTR_SCHEMA, EMPTY);
		this.allocationSize = this.getAttribute(ATTR_ALLOCATION_SIZE, 50);
		this.initialValue = this.getAttribute(ATTR_INITIAL_VALUE, 1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getAllocationSize() {
		return this.allocationSize;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Returns the initialValue of the GeneratorElement.
	 * 
	 * @return the initialValue of the GeneratorElement
	 * 
	 * @since 2.0.0
	 */
	@Override
	public int getInitialValue() {
		return this.initialValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		// noop
	}

}

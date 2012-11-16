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

import javax.persistence.DiscriminatorType;

import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;

/**
 * Element for <code>discriminator-column</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class DiscriminatorColumnElement extends ChildElement implements DiscriminatorColumnMetadata {

	private String name;
	private DiscriminatorType discriminatorType;
	private String columnDefinition;
	private int length;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public DiscriminatorColumnElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, "DTYPE");
		this.columnDefinition = this.getAttribute(ElementConstants.ATTR_COLUMN_DEFINITION);
		this.discriminatorType = DiscriminatorType.valueOf(this.getAttribute(ElementConstants.ATTR_DISCRIMINATOR_TYPE,
			DiscriminatorType.STRING.name()));
		this.length = this.getAttribute(ElementConstants.ATTR_LENGTH, 31);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DiscriminatorType getDiscriminatorType() {
		return this.discriminatorType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}
}

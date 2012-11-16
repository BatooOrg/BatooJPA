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

import javax.persistence.InheritanceType;

import org.batoo.jpa.parser.metadata.InheritanceMetadata;

/**
 * Element for <code>inheritance</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class InheritanceElement extends ChildElement implements InheritanceMetadata {

	private InheritanceType inheritance;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public InheritanceElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.inheritance = InheritanceType.valueOf(this.getAttribute(ElementConstants.ATTR_STRATEGY, InheritanceType.SINGLE_TABLE.name()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public InheritanceType getInheritanceType() {
		return this.inheritance;
	}
}

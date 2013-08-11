/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

import org.batoo.common.log.ToStringBuilder;
import org.batoo.common.log.ToStringBuilder.DetailLevel;

/**
 * Element factories that have no children elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class ChildElement extends Element {

	private static final String[] NO_CHILDREN = new String[] {};

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public ChildElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ChildElement.NO_CHILDREN);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, DetailLevel.SHORT)//
		.excludeFieldNames("parent", "attributes", "expectedChildElements") //
		.toString();
	}

}

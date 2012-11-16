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

import org.batoo.common.log.ToStringBuilder;

/**
 * Element factories that may parent child elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class ParentElement extends Element {

	/**
	 * Returns the joint array of <code>elements1</code> and <code>elements2</code>.
	 * 
	 * @param elements1
	 *            the first array of elements
	 * @param elements2
	 *            the second array of elements
	 * @return the joint array of <code>elements1</code> and <code>elements2</code>
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public static String[] join(String[] elements1, String... elements2) {
		if ((elements1 == null) || (elements1.length == 0)) {
			return elements2;
		}

		if ((elements2 == null) || (elements2.length == 0)) {
			return elements1;
		}

		final String[] joined = new String[elements1.length + elements2.length];

		for (int i = 0; i < elements1.length; i++) {
			joined[i] = elements1[i];
		}

		for (int i = 0; i < elements2.length; i++) {
			joined[i + elements1.length] = elements2[i];
		}

		return joined;
	}

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param expectedChildElements
	 *            the name of the elements expected
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public ParentElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, expectedChildElements);
	}

	/**
	 * Handles the <code>generated</code> artifact of the child element.
	 * <p>
	 * Element factories implement the method to generate their artifacts.
	 * 
	 * @param child
	 *            the generated child artifact
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	protected abstract void handleChild(Element child);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)//
		.excludeFieldNames("parent", "attributes", "expectedChildElements") //
		.toString();
	}
}

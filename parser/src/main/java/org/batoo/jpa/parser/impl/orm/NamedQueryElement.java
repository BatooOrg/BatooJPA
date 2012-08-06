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

import javax.persistence.LockModeType;

import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.collect.Maps;

/**
 * Element for <code>named-native-query</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class NamedQueryElement extends ParentElement implements NamedQueryMetadata {

	private String name;
	private String query;
	private LockModeType lockMode;
	private final Map<String, Object> hints = Maps.newHashMap();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public NamedQueryElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_QUERY, //
			ElementConstants.ELEMENT_HINT, //
			ElementConstants.ELEMENT_LOCK_MODE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getHints() {
		return this.hints;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode() {
		return this.lockMode;
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
	public String getQuery() {
		return this.query;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof HintElement) {
			this.hints.put(((HintElement) child).getName(), ((HintElement) child).getValue());
		}

		if (child instanceof QueryElement) {
			this.query = ((QueryElement) child).getQuery();
		}

		if (child instanceof LockModeElement) {
			this.lockMode = ((LockModeElement) child).getLockMode();
		}
	}
}

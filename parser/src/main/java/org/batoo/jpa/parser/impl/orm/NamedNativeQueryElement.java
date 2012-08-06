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

import org.batoo.jpa.parser.metadata.NamedNativeQueryMetadata;

import com.google.common.collect.Maps;

/**
 * Element for <code>named-query</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class NamedNativeQueryElement extends ParentElement implements NamedNativeQueryMetadata {

	private String name;
	private String query;
	private final Map<String, Object> hints = Maps.newHashMap();
	private String resultClass;
	private String resultSetMapping;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public NamedNativeQueryElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_QUERY, //
			ElementConstants.ELEMENT_HINT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
		this.resultClass = this.getAttribute(ElementConstants.ATTR_RESULT_CLASS, ElementConstants.EMPTY);
		this.resultSetMapping = this.getAttribute(ElementConstants.ATTR_RESULT_SET_MAPPING, ElementConstants.EMPTY);
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
	public String getResultClass() {
		return this.resultClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getResultSetMapping() {
		return this.resultSetMapping;
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
	}
}

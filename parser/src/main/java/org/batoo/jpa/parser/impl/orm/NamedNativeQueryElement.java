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

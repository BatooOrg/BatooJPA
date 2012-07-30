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

import javax.persistence.LockModeType;

import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.collect.Maps;

/**
 * Element for <code>named-query</code> elements.
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

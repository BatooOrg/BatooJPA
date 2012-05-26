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

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>join-table</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinTableElement extends TableElement implements JoinTableMetadata {

	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();
	private final List<JoinColumnMetadata> inverseJoinColumns = Lists.newArrayList();

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinTableElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_UNIQUE_CONSTRAINT, //
			ElementConstants.ELEMENT_JOIN_COLUMN,//
			ElementConstants.ELEMENT_INVERSE_JOIN_COLUMN);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<JoinColumnMetadata> getInverseJoinColumns() {
		return this.inverseJoinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<JoinColumnMetadata> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		super.handleChild(child);

		if (child instanceof JoinTableElement) {
			this.joinColumns.add((JoinColumnMetadata) child);
		}

		if (child instanceof InverseJoinColumnElement) {
			this.inverseJoinColumns.add((JoinColumnMetadata) child);
		}
	}
}

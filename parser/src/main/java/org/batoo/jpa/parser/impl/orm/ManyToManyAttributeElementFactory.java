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

import javax.persistence.FetchType;

import org.batoo.jpa.parser.metadata.ManyToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

import com.google.common.collect.Lists;

/**
 * Element factory for <code>many-to-many</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToManyAttributeElementFactory extends AssociationElementFactory implements ManyToManyAttributeMetadata {

	private String mappedBy;

	private final List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param expectedChildElements
	 *            the name of the elements expected
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToManyAttributeElementFactory(ParentElementFactory parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, FetchType.LAZY, //
			ElementFactoryConstants.ELEMENT_CASCADE,//
			ElementFactoryConstants.ELEMENT_JOIN_COLUMN, //
			ElementFactoryConstants.ELEMENT_JOIN_TABLE, //
			ElementFactoryConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.mappedBy = this.getAttribute(ElementFactoryConstants.ATTR_MAPPED_BY, ElementFactoryConstants.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMappedBy() {
		return this.mappedBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(ElementFactory child) {
		super.handleChild(child);

		if (child instanceof PrimaryKeyJoinColumnElementFactory) {
			this.primaryKeyJoinColumns.add((PrimaryKeyJoinColumnMetadata) child);
		}
	}
}

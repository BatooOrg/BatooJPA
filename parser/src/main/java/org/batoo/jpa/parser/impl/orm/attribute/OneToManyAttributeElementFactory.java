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
package org.batoo.jpa.parser.impl.orm.attribute;

import java.util.List;
import java.util.Map;

import javax.persistence.FetchType;

import org.batoo.jpa.parser.impl.orm.ElementFactory;
import org.batoo.jpa.parser.impl.orm.ElementFactoryConstants;
import org.batoo.jpa.parser.impl.orm.ParentElementFactory;
import org.batoo.jpa.parser.impl.orm.PrimaryKeyJoinColumnElementFactory;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element factory for <code>one-to-many</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class OneToManyAttributeElementFactory extends AssociationElementFactory implements OneToManyAttributeMetadata {

	private boolean orphanRemoval;
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
	public OneToManyAttributeElementFactory(ParentElementFactory parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, FetchType.LAZY, //
			ElementFactoryConstants.ELEMENT_CASCADE,//
			ElementFactoryConstants.ELEMENT_JOIN_COLUMN, //
			ElementFactoryConstants.ELEMENT_JOIN_TABLE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.orphanRemoval = this.getAttribute(ElementFactoryConstants.ATTR_ORPHAN_REMOVAL, Boolean.FALSE);
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removesOprhans() {
		return this.orphanRemoval;
	}
}

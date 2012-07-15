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

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.MapKeyElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.PrimaryKeyJoinColumnElement;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>many-to-many</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToManyAttributeElement extends AssociationElement implements ManyToManyAttributeMetadata {

	private String mappedBy;
	private String mapKey;
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
	public ManyToManyAttributeElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, FetchType.LAZY, //
			ElementConstants.ELEMENT_CASCADE,//
			ElementConstants.ELEMENT_JOIN_COLUMN, //
			ElementConstants.ELEMENT_JOIN_TABLE, //
			ElementConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN, //
			ElementConstants.ELEMENT_MAP_KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.mappedBy = this.getAttribute(ElementConstants.ATTR_MAPPED_BY, ElementConstants.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKey() {
		return this.mapKey;
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
	protected void handleChild(Element child) {
		super.handleChild(child);

		if (child instanceof PrimaryKeyJoinColumnElement) {
			this.primaryKeyJoinColumns.add((PrimaryKeyJoinColumnMetadata) child);
		}

		if (child instanceof MapKeyElement) {
			this.mapKey = ((MapKeyElement) child).getName();
		}
	}
}

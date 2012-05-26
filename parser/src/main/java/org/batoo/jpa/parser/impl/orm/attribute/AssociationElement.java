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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.batoo.jpa.parser.impl.orm.CascadesElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.JoinColumnElement;
import org.batoo.jpa.parser.impl.orm.JoinTableElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.PrimaryKeyJoinColumnElement;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>one-to-one</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class AssociationElement extends AttributeElement implements AssociationAttributeMetadata {

	private String targetEntity;
	private FetchType fetchType;

	private Set<CascadeType> cascades;
	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();
	private JoinTableMetadata joinTable;
	private final List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param defaultFetchType
	 *            the default fetch type
	 * @param expectedChildElements
	 *            the name of the elements expected
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationElement(ParentElement parent, Map<String, String> attributes, FetchType defaultFetchType,
		String... expectedChildElements) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_CASCADE,//
			ElementConstants.ELEMENT_JOIN_COLUMN, //
			ElementConstants.ELEMENT_JOIN_TABLE, //
			ElementConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN);
		this.fetchType = defaultFetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.targetEntity = this.getAttribute(ElementConstants.ATTR_TARGET_ENTITY, ElementConstants.EMPTY);
		this.fetchType = FetchType.valueOf(this.getAttribute(ElementConstants.ATTR_FETCH, this.fetchType.name()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<CascadeType> getCascades() {
		return this.cascades;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchType getFetchType() {
		return this.fetchType;
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
	public JoinTableMetadata getJoinTable() {
		return this.joinTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTargetEntity() {
		return this.targetEntity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof CascadesElement) {
			this.cascades = ((CascadesElement) child).getCascades();
		}

		if (child instanceof JoinColumnElement) {
			this.joinColumns.add((JoinColumnMetadata) child);
		}

		if (child instanceof JoinTableElement) {
			this.joinTable = (JoinTableMetadata) child;
		}

		if (child instanceof PrimaryKeyJoinColumnElement) {
			this.primaryKeyJoinColumns.add((PrimaryKeyJoinColumnMetadata) child);
		}
	}
}

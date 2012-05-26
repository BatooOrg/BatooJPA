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
package org.batoo.jpa.parser.impl.metadata;

import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.UniqueConstraint;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link JoinTableMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinTableMetadaImpl implements JoinTableMetadata {

	private final AbstractLocator locator;
	private final String catalog;
	private final String schema;
	private final String name;
	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();
	private final List<JoinColumnMetadata> inverseJoinColumns = Lists.newArrayList();
	private final List<UniqueConstraintMetadata> uniqueConstraints = Lists.newArrayList();

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinTableMetadaImpl(AbstractLocator locator, JoinTable annotation) {
		super();

		this.locator = locator;

		this.catalog = annotation.catalog();
		this.schema = annotation.schema();
		this.name = annotation.name();

		for (final JoinColumn joinColumn : annotation.joinColumns()) {
			this.joinColumns.add(new JoinColumnMetadataImpl(locator, joinColumn));
		}

		for (final JoinColumn joinColumn : annotation.inverseJoinColumns()) {
			this.inverseJoinColumns.add(new JoinColumnMetadataImpl(locator, joinColumn));
		}

		for (final UniqueConstraint constraint : annotation.uniqueConstraints()) {
			this.uniqueConstraints.add(new UniqueConstraintMetadataImpl(locator, constraint));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.catalog;
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
	public AbstractLocator getLocator() {
		return this.locator;
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
	public String getSchema() {
		return this.schema;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<UniqueConstraintMetadata> getUniqueConstraints() {
		return this.uniqueConstraints;
	}

}

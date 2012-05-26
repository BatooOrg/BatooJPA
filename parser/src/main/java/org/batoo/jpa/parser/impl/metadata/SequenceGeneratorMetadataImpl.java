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

import javax.persistence.SequenceGenerator;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;

/**
 * Implementation of {@link SequenceGeneratorMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class SequenceGeneratorMetadataImpl implements SequenceGeneratorMetadata {

	private final AbstractLocator locator;
	private final String catalog;
	private final String schema;
	private final String name;
	private final String sequenceName;
	private final int initialValue;
	private final int allocationSize;

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceGeneratorMetadataImpl(AbstractLocator locator, SequenceGenerator annotation) {
		super();

		this.locator = locator;
		this.catalog = annotation.catalog();
		this.schema = annotation.schema();
		this.name = annotation.name();
		this.sequenceName = annotation.sequenceName();
		this.initialValue = annotation.initialValue();
		this.allocationSize = annotation.allocationSize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getAllocationSize() {
		return this.allocationSize;
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
	public int getInitialValue() {
		return this.initialValue;
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
	public String getSequenceName() {
		return this.sequenceName;
	}
}

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

import javax.persistence.AttributeOverride;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Implementation of {@link AttributeOverrideMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributeOverrideMetadataImpl implements AttributeOverrideMetadata {

	private final AbstractLocator locator;
	private final String name;
	private final ColumnMetadata column;

	/**
	 * @param locator
	 *            the locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeOverrideMetadataImpl(AbstractLocator locator, AttributeOverride annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.column = new ColumnMetadataImpl(locator, annotation.column());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getColumn() {
		return this.column;
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
}

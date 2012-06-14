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

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;

/**
 * Implementation of {@link DiscriminatorColumnMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class DiscriminatorColumnMetadataImpl implements DiscriminatorColumnMetadata {

	private final DiscriminatorType discriminatorType;
	private final String columnDefinition;
	private final int length;
	private final String name;
	private final AbstractLocator locator;

	/**
	 * @param locator
	 *            the locator
	 * @param discriminatorColumn
	 *            the discriminator column annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DiscriminatorColumnMetadataImpl(AbstractLocator locator, DiscriminatorColumn discriminatorColumn) {
		super();

		this.locator = locator;
		this.discriminatorType = discriminatorColumn.discriminatorType();
		this.columnDefinition = discriminatorColumn.columnDefinition();
		this.length = discriminatorColumn.length();
		this.name = discriminatorColumn.name();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DiscriminatorType getDiscriminatorType() {
		return this.discriminatorType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.length;
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

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

import javax.persistence.UniqueConstraint;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.UniqueConstraintMetadata;

/**
 * Implementation of {@link UniqueConstraintMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class UniqueConstraintMetadataImpl implements UniqueConstraintMetadata {

	private final AbstractLocator locator;
	private final String[] columnNames;
	private final String name;

	/**
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public UniqueConstraintMetadataImpl(AbstractLocator locator, UniqueConstraint annotation) {
		super();

		this.locator = locator;
		this.name = annotation.name();
		this.columnNames = annotation.columnNames();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getColumnNames() {
		return this.columnNames;
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

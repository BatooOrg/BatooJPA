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

import javax.persistence.PrimaryKeyJoinColumn;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

/**
 * Implementation of {@link PrimaryKeyJoinColumnMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class PrimaryKeyJoinColumnMetadaImpl implements PrimaryKeyJoinColumnMetadata {

	private final JavaLocator locator;
	private final PrimaryKeyJoinColumn pkJoinColumn;

	/**
	 * @param locator
	 *            the java locator
	 * @param pkJoinColumn
	 *            the obtained {@link PrimaryKeyJoinColumn} annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PrimaryKeyJoinColumnMetadaImpl(JavaLocator locator, PrimaryKeyJoinColumn pkJoinColumn) {
		super();

		this.locator = locator;
		this.pkJoinColumn = pkJoinColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getColumnDefinition() {
		return this.pkJoinColumn.columnDefinition();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getLocation() {
		return this.locator.getLocation();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.pkJoinColumn.name();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getReferencedColumnName() {
		return this.pkJoinColumn.referencedColumnName();
	}

}

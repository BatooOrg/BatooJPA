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

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;

/**
 * Annotated definition of sequence generators.
 * 
 * @author hceylan
 * @since $version
 */
public class SequenceGeneratorMetadataImpl implements SequenceGeneratorMetadata {

	private final JavaLocator locator;
	private final SequenceGenerator sequenceGenerator;

	/**
	 * @param locator
	 *            the java locator
	 * @param sequenceGenerator
	 *            the obtained {@link SequenceGenerator} annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceGeneratorMetadataImpl(JavaLocator locator, SequenceGenerator sequenceGenerator) {
		super();

		this.locator = locator;
		this.sequenceGenerator = sequenceGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getAllocationSize() {
		return this.sequenceGenerator.allocationSize();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getCatalog() {
		return this.sequenceGenerator.catalog();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getInitialValue() {
		return this.sequenceGenerator.initialValue();
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
		return this.sequenceGenerator.name();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSchema() {
		return this.sequenceGenerator.schema();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSequenceName() {
		return this.sequenceGenerator.sequenceName();
	}

}

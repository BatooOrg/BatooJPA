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

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;

/**
 * Annotated definition of generated values.
 * 
 * @author hceylan
 * @since $version
 */
public class GeneratedValueMetadataImpl implements GeneratedValueMetadata {

	private final JavaLocator locator;
	private final GeneratedValue generatedValue;

	/**
	 * @param locator
	 *            the java locator
	 * @param generatedValue
	 *            the {@link GeneratedValue} annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public GeneratedValueMetadataImpl(JavaLocator locator, GeneratedValue generatedValue) {
		super();

		this.locator = locator;
		this.generatedValue = generatedValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getGenerator() {
		return this.generatedValue.generator();
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
	public GenerationType getStrategy() {
		return this.generatedValue.strategy();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.generatedValue.toString();
	}
}

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
package org.batoo.jpa.core.impl.model;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;

/**
 * Sequence based generator.
 * 
 * @author hceylan
 * @since $version
 */
public class SequenceGenerator extends AbstractGenerator {

	private static final String DEFAULT_SEQUENCE_NAME = "BATOO_ID";

	private final String sequenceName;

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceGenerator(SequenceGeneratorMetadata metadata) {
		super(metadata);

		this.sequenceName = (metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName()
			: SequenceGenerator.DEFAULT_SEQUENCE_NAME;
	}

	/**
	 * Returns the sequenceName of the sequence generator.
	 * 
	 * @return the sequenceName of the sequence generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSequenceName() {
		return this.sequenceName;
	}
}

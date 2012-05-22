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
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.reflect.Member;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

import org.batoo.jpa.parser.impl.metadata.GeneratedValueMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SequenceGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableGeneratorMetadataImpl;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.VersionAttributeMetadata;

/**
 * The implementation of the {@link IdAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class IdAttributeMetadataImpl extends BasicSingularAttributeMetadataImpl implements IdAttributeMetadata {

	private GeneratedValueMetadata generatedValue;
	private SequenceGeneratorMetadata sequenceGenerator;
	private TableGeneratorMetadata tableGenerator;

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param name
	 *            the name of the basic attribute
	 * @param column
	 *            the column definition of the basic attribute
	 * @param temporal
	 *            the temporal definition of the basic attribute
	 * @param generatedValue
	 *            the generated definition of the id attribute
	 * @param tableGenerator
	 *            the table generator of the id attribute
	 * @param sequenceGenerator
	 *            the sequence generator of the id attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeMetadataImpl(Member member, String name, Column column, Temporal temporal, GeneratedValue generatedValue,
		TableGenerator tableGenerator, SequenceGenerator sequenceGenerator) {
		super(member, name, column, null, temporal);

		this.generatedValue = generatedValue != null ? new GeneratedValueMetadataImpl(this.getLocator(), generatedValue) : null;
		this.tableGenerator = this.tableGenerator != null ? new TableGeneratorMetadataImpl(this.getLocator(), tableGenerator) : null;
		this.sequenceGenerator = this.sequenceGenerator != null ? new SequenceGeneratorMetadataImpl(this.getLocator(), sequenceGenerator)
			: null;
	}

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param metadata
	 *            the metadata definition of the basic attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeMetadataImpl(Member member, VersionAttributeMetadata metadata) {
		super(member, metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public GeneratedValueMetadata getGeneratedValue() {
		return this.generatedValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SequenceGeneratorMetadata getSequenceGenerator() {
		return this.sequenceGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableGeneratorMetadata getTableGenerator() {
		return this.tableGenerator;
	}
}

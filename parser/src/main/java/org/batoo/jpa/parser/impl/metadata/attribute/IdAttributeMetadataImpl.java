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

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.GeneratedValueMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SequenceGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableGeneratorMetadataImpl;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;

/**
 * The implementation of the {@link IdAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class IdAttributeMetadataImpl extends PhysicalAttributeMetadataImpl implements IdAttributeMetadata {

	private final GeneratedValueMetadata generatedValue;
	private final SequenceGeneratorMetadata sequenceGenerator;
	private final TableGeneratorMetadata tableGenerator;

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param metadata
	 *            the metadata definition of the basic attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeMetadataImpl(Member member, IdAttributeMetadata metadata) {
		super(member, metadata);

		this.generatedValue = metadata.getGeneratedValue();
		this.sequenceGenerator = metadata.getSequenceGenerator();
		this.tableGenerator = metadata.getTableGenerator();
	}

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param name
	 *            the name of the basic attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed);

		final TableGenerator tableGenerator = ReflectHelper.getAnnotation(member, TableGenerator.class);
		final SequenceGenerator sequenceGenerator = ReflectHelper.getAnnotation(member, SequenceGenerator.class);
		final GeneratedValue generatedValue = ReflectHelper.getAnnotation(member, GeneratedValue.class);

		parsed.add(Id.class);
		parsed.add(TableGenerator.class);
		parsed.add(SequenceGenerator.class);
		parsed.add(GeneratedValue.class);

		this.generatedValue = generatedValue != null ? new GeneratedValueMetadataImpl(this.getLocator(), generatedValue) : null;
		this.tableGenerator = this.tableGenerator != null ? new TableGeneratorMetadataImpl(this.getLocator(), tableGenerator) : null;
		this.sequenceGenerator = this.sequenceGenerator != null ? new SequenceGeneratorMetadataImpl(this.getLocator(), sequenceGenerator)
			: null;
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

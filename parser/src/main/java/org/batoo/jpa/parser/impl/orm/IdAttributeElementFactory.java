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
package org.batoo.jpa.parser.impl.orm;

import java.util.Map;

import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;

/**
 * Element factory for <code>id</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class IdAttributeElementFactory extends SingularAttributeElementFactory implements IdAttributeMetadata {

	private GeneratedValueMetadata generatedValue;
	private SequenceGeneratorMetadata sequenceGenerator;
	private TableGeneratorMetadata tableGenerator;

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes, ElementFactoryConstants.ELEMENT_TEMPORAL, //
			ElementFactoryConstants.ELEMENT_COLUMN,//
			ElementFactoryConstants.ELEMENT_GENERATED_VALUE, //
			ElementFactoryConstants.ELEMENT_SEQUENCE_GENERATOR, //
			ElementFactoryConstants.ELEMENT_TABLE_GENERATOR);
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(ElementFactory child) {
		super.handleChild(child);

		if (child instanceof GeneratedValueElementFactory) {
			this.generatedValue = (GeneratedValueElementFactory) child;
		}

		if (child instanceof SequenceGeneratorElementFactory) {
			this.sequenceGenerator = (SequenceGeneratorElementFactory) child;
		}

		if (child instanceof TableGeneratorElementFactory) {
			this.tableGenerator = (TableGeneratorElementFactory) child;
		}
	}
}

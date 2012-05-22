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
import java.util.List;

import org.batoo.jpa.parser.metadata.AssociationOverrideMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;

/**
 * The implementation of the {@link EmbeddedAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttributeMetadataImpl extends AttributeMetadataImpl implements EmbeddedAttributeMetadata {

	private final List<AttributeOverrideMetadata> attributeOverrides;
	private final List<AssociationOverrideMetadata> associationOverrides;

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttributeMetadataImpl(Member member, EmbeddedAttributeMetadata metadata) {
		super(member, metadata);

		this.attributeOverrides = metadata.getAttributeOverrides();
		this.associationOverrides = metadata.getAssociationOverrides();
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param attributeOverrides
	 *            the attribute overrides
	 * @param associationOverrides
	 *            the association overrides
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttributeMetadataImpl(Member member, String name, List<AttributeOverrideMetadata> attributeOverrides,
		List<AssociationOverrideMetadata> associationOverrides) {
		super(member, name);

		this.attributeOverrides = attributeOverrides;
		this.associationOverrides = associationOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AssociationOverrideMetadata> getAssociationOverrides() {
		return this.associationOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return this.attributeOverrides;
	}

}

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

import javax.persistence.ManyToOne;

import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;

/**
 * Implementation of {@link ManyToOneAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToOneAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements ManyToOneAttributeMetadata {

	private final boolean optional;
	private final String mapsId;

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-one attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToOneAttributeMetadataImpl(Member member, ManyToOneAttributeMetadata metadata) {
		super(member, metadata);

		this.optional = metadata.isOptional();
		this.mapsId = metadata.getMapsId();
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param manyToOne
	 *            the annotation
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToOneAttributeMetadataImpl(Member member, String name, ManyToOne manyToOne, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed, manyToOne.targetEntity().getName(), manyToOne.fetch(), manyToOne.cascade());

		parsed.add(ManyToOne.class);

		this.optional = manyToOne.optional();
		this.mapsId = this.handleMapsId(member, parsed);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapsId() {
		return this.mapsId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}

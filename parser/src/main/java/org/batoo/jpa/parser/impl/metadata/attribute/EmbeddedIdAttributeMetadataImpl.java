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
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedIdAttributeMetadataImpl extends AttributeMetadataImpl implements EmbeddedIdAttributeMetadata {

	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedIdAttributeMetadataImpl(Member member, EmbeddedIdAttributeMetadata metadata) {
		super(member, metadata);

		this.attributeOverrides.addAll(metadata.getAttributeOverrides());
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedIdAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		// if there is AttributesOverrides annotation use it
		final AttributeOverrides attributeOverrides = ReflectHelper.getAnnotation(member, AttributeOverrides.class);
		final AttributeOverride attributeOverride = ReflectHelper.getAnnotation(member, AttributeOverride.class);

		if ((attributeOverrides != null) && (attributeOverrides.value() != null) && (attributeOverrides.value().length > 0)) {
			parsed.add(AttributeOverrides.class);

			for (final AttributeOverride a : attributeOverrides.value()) {
				this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), a));
			}
		}
		else if (attributeOverride != null) {
			parsed.add(AttributeOverride.class);

			this.attributeOverrides.add(new AttributeOverrideMetadataImpl(this.getLocator(), attributeOverride));
		}
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

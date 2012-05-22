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
package org.batoo.jpa.parser.impl.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.AttributeOverrideMetadataImpl;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;

import com.google.common.collect.Lists;

/**
 * PersistenceParser for {@link AttributeOverride} and {@link AttributeOverrides} annotations.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributeOverridesParser {

	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member
	 * @param parsed
	 *            the set annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeOverridesParser(Member member, Set<Class<? extends Annotation>> parsed) {
		super();

		final JavaLocator locator = new JavaLocator(member, null);

		final AttributeOverrides attributeOverrides = ReflectHelper.getAnnotation(member, AttributeOverrides.class);
		if (attributeOverrides != null) {

			for (final AttributeOverride attributeOverride : attributeOverrides.value()) {
				this.attributeOverrides.add(new AttributeOverrideMetadataImpl(locator, attributeOverride));
			}

			parsed.add(AttributeOverrides.class);
		}
		else {
			final AttributeOverride attributeOverride = ReflectHelper.getAnnotation(member, AttributeOverride.class);
			if (attributeOverride != null) {
				this.attributeOverrides.add(new AttributeOverrideMetadataImpl(locator, attributeOverride));

				parsed.add(AttributeOverride.class);
			}
		}
	}

	/**
	 * Returns the attributeOverrides of the AttributeOverridesParser.
	 * 
	 * @return the attributeOverrides of the AttributeOverridesParser
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return this.attributeOverrides;
	}
}

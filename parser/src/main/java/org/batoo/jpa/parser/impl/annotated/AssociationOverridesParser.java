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

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.AssociationOverrideMetadataImpl;
import org.batoo.jpa.parser.metadata.AssociationOverrideMetadata;

import com.google.common.collect.Lists;

/**
 * PersistenceParser for {@link AssociationOverride} and {@link AssociationOverrides} annotations.
 * 
 * @author hceylan
 * @since $version
 */
public class AssociationOverridesParser {

	private final List<AssociationOverrideMetadata> associationOverrides = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationOverridesParser(Member member, Set<Class<? extends Annotation>> parsed) {
		super();

		final JavaLocator locator = new JavaLocator(member, null);

		final AssociationOverrides associationOverrides = ReflectHelper.getAnnotation(member, AssociationOverrides.class);
		if (associationOverrides != null) {

			for (final AssociationOverride associationOverride : associationOverrides.value()) {
				this.getAssociationOverrides().add(new AssociationOverrideMetadataImpl(locator, associationOverride));
			}

			parsed.add(AssociationOverrides.class);
		}
		else {
			final AssociationOverride associationOverride = ReflectHelper.getAnnotation(member, AssociationOverride.class);
			if (associationOverride != null) {
				this.getAssociationOverrides().add(new AssociationOverrideMetadataImpl(locator, associationOverride));
				parsed.add(AssociationOverride.class);
			}
		}
	}

	/**
	 * Returns the list of association overrides parsed.
	 * 
	 * @return the list of association overrides parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<AssociationOverrideMetadata> getAssociationOverrides() {
		return this.associationOverrides;
	}
}

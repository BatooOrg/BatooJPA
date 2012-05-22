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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Embedded;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.AnnotationParser;
import org.batoo.jpa.parser.impl.metadata.attribute.EmbeddedAttributeMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.EmbeddedAttributeMetadata;

import com.google.common.collect.Sets;

/**
 * PersistenceParser for embedded attributes.
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedsParser extends AbstractAttributesParser<EmbeddedAttributeMetadata> {

	/**
	 * @param parent
	 *            the attributes parser
	 * @param memberMap
	 *            the map of members
	 * @param ormAttributes
	 *            the optional ORM Metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedsParser(AttributesMetadataImpl parent, Map<String, Member> memberMap, List<EmbeddedAttributeMetadata> ormAttributes) {
		super(parent, memberMap, ormAttributes, Embedded.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void parseAttribute(String name, Member member, EmbeddedAttributeMetadata ormMetadata) {
		if (ormMetadata != null) {
			this.getAttributes().add(new EmbeddedAttributeMetadataImpl(member, ormMetadata));

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, Collections.<Class<? extends Annotation>> emptySet());
		}
		else {
			final Set<Class<? extends Annotation>> parsed = Sets.<Class<? extends Annotation>> newHashSet(Embedded.class);

			final AttributeOverridesParser attributeOverridesParser = new AttributeOverridesParser(member, parsed);
			final AssociationOverridesParser associationOverridesParser = new AssociationOverridesParser(member, parsed);

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, parsed);

			this.getAttributes().add(
				new EmbeddedAttributeMetadataImpl(member, name, attributeOverridesParser.getAttributeOverrides(),
					associationOverridesParser.getAssociationOverrides()));
		}
	}
}

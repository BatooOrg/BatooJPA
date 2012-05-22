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

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.Version;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.AnnotationParser;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.attribute.VersionAttributeMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

import com.google.common.collect.Sets;

/**
 * PersistenceParser for version attributes that parses and merges the optional ORM File provided metadata.
 * 
 * @author hceylan
 * @since $version
 */
public class VersionsParser extends AbstractAttributesParser<VersionAttributeMetadata> {

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
	public VersionsParser(AttributesMetadataImpl parent, Map<String, Member> memberMap, List<VersionAttributeMetadata> ormAttributes) {
		super(parent, memberMap, ormAttributes, Version.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void parseAttribute(String name, Member member, VersionAttributeMetadata ormMetadata) {
		if (ormMetadata != null) {
			this.getAttributes().add(new VersionAttributeMetadataImpl(member, ormMetadata));

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, Collections.<Class<? extends Annotation>> emptySet());
		}
		else {
			final Column column = ReflectHelper.getAnnotation(member, Column.class);
			final Temporal temporal = ReflectHelper.getAnnotation(member, Temporal.class);

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, Sets.newHashSet(//
				Version.class, //
				Column.class, //
				Temporal.class));

			this.getAttributes().add(new VersionAttributeMetadataImpl(member, name, column, temporal));
		}
	}

}

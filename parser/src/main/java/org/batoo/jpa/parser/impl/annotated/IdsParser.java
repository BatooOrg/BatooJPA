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

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.AnnotationParser;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.attribute.IdAttributeMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

import com.google.common.collect.Sets;

/**
 * PersistenceParser for basic attributes that parses and merges the optional ORM File provided metadata.
 * 
 * @author hceylan
 * @since $version
 */
public class IdsParser extends AbstractAttributesParser<IdAttributeMetadata> {

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
	public IdsParser(AttributesMetadataImpl parent, Map<String, Member> memberMap, List<IdAttributeMetadata> ormAttributes) {
		super(parent, memberMap, ormAttributes, Id.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void parseAttribute(String name, Member member, IdAttributeMetadata ormMetadata) {
		if (ormMetadata != null) {
			this.getAttributes().add(new IdAttributeMetadataImpl(member, (VersionAttributeMetadata) ormMetadata));

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, Collections.<Class<? extends Annotation>> emptySet());
		}
		else {
			@SuppressWarnings("unchecked")
			final Set<Class<? extends Annotation>> parsed = Sets.newHashSet(Id.class, Column.class, Temporal.class);

			final TableGenerator tableGenerator = ReflectHelper.getAnnotation(member, TableGenerator.class);
			SequenceGenerator sequenceGenerator = ReflectHelper.getAnnotation(member, SequenceGenerator.class);
			GeneratedValue generatedValue = ReflectHelper.getAnnotation(member, GeneratedValue.class);

			if (tableGenerator != null) {
				parsed.add(TableGenerator.class);
				sequenceGenerator = null;
				generatedValue = null;
			}
			else if (sequenceGenerator != null) {
				parsed.add(SequenceGenerator.class);
				generatedValue = null;
			}
			else if (generatedValue != null) {
				parsed.add(GeneratedValue.class);
			}

			final Column column = ReflectHelper.getAnnotation(member, Column.class);
			final Temporal temporal = ReflectHelper.getAnnotation(member, Temporal.class);

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, parsed);

			this.getAttributes().add(
				new IdAttributeMetadataImpl(member, name, column, temporal, generatedValue, tableGenerator, sequenceGenerator));
		}
	}
}

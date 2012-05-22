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

import javax.persistence.OneToOne;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.AnnotationParser;
import org.batoo.jpa.parser.impl.metadata.attribute.OneToOneAttributeMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.OneToOneAttributeMetadata;

import com.google.common.collect.Sets;

/**
 * PersistenceParser for one-to-one associations.
 * 
 * @author hceylan
 * @since $version
 */
public class OneToOnesParser extends AbstractAttributesParser<OneToOneAttributeMetadata> {

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
	public OneToOnesParser(AttributesMetadataImpl parent, Map<String, Member> memberMap, List<OneToOneAttributeMetadata> ormAttributes) {
		super(parent, memberMap, ormAttributes, OneToOne.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void parseAttribute(String name, Member member, OneToOneAttributeMetadata ormMetadata) {
		if (ormMetadata != null) {
			this.getAttributes().add(new OneToOneAttributeMetadataImpl(member, ormMetadata));

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, Collections.<Class<? extends Annotation>> emptySet());
		}
		else {
			final OneToOne oneToOne = ReflectHelper.getAnnotation(member, OneToOne.class);

			final Set<Class<? extends Annotation>> parsed = Sets.<Class<? extends Annotation>> newHashSet(OneToOne.class);

			final JoinsParser joinsParser = new JoinsParser(member, parsed);
			final PrimaryKeyJoinColumnsParser pkJoinColumnsParser = new PrimaryKeyJoinColumnsParser(member, parsed);

			ReflectHelper.warnAnnotations(AnnotationParser.LOG, member, parsed);

			this.getAttributes().add(
				new OneToOneAttributeMetadataImpl(member, name, oneToOne, pkJoinColumnsParser.getPkJoinColumns(),
					joinsParser.getJoinColumns(), joinsParser.getJoinTable()));
		}
	}
}

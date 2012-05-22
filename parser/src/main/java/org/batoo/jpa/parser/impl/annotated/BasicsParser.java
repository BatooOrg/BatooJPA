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

import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Temporal;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.attribute.BasicAttributeMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * PersistenceParser for basic attributes that parses and merges the optional ORM File provided metadata.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicsParser extends AbstractAttributesParser<BasicAttributeMetadata> {

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
	public BasicsParser(AttributesMetadataImpl parent, Map<String, Member> memberMap, List<BasicAttributeMetadata> ormAttributes) {
		super(parent, memberMap, ormAttributes, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void parseAttribute(String name, Member member, BasicAttributeMetadata ormMetadata) {
		if (ormMetadata != null) {
			this.getAttributes().add(new BasicAttributeMetadataImpl(member, ormMetadata));
		}
		else {
			final Basic basic = ReflectHelper.getAnnotation(member, Basic.class);
			final Column column = ReflectHelper.getAnnotation(member, Column.class);
			final Temporal temporal = ReflectHelper.getAnnotation(member, Temporal.class);
			final Lob lob = ReflectHelper.getAnnotation(member, Lob.class);
			final Enumerated enumerated = ReflectHelper.getAnnotation(member, Enumerated.class);

			this.getAttributes().add(new BasicAttributeMetadataImpl(member, name, basic, enumerated, lob, column, temporal));
		}
	}

}

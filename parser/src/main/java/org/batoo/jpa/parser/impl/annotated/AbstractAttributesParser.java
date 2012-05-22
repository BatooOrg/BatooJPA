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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Common parser for attributes that parses and merges the optional ORM File provided metadata.
 * 
 * @param <A>
 *            the type of the attribute
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractAttributesParser<A extends AttributeMetadata> {

	private final AttributesMetadataImpl parent;
	private final Map<String, Member> memberMap;
	private final List<A> ormAttributes;
	private final List<A> attributes = Lists.newArrayList();
	private final Class<? extends Annotation> indicativeAnnotation;

	/**
	 * @param parent
	 *            the attributes parser
	 * @param memberMap
	 *            the map of members
	 * @param ormAttributes
	 *            the optional ORM Metadata
	 * @param indicativeAnnotation
	 *            the annotation that indicates the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AbstractAttributesParser(AttributesMetadataImpl parent, Map<String, Member> memberMap, List<A> ormAttributes,
		Class<? extends Annotation> indicativeAnnotation) {
		super();

		this.parent = parent;
		this.memberMap = memberMap;
		this.ormAttributes = (List<A>) (ormAttributes != null ? ormAttributes : Lists.newArrayList());
		this.indicativeAnnotation = indicativeAnnotation;

		this.probe();

		this.ormAttributes.removeAll(this.attributes);
		if (this.ormAttributes.size() > 0) {
			throw new MappingException("The following attributes defined in orm.xml could be located\n" + ormAttributes);
		}
	}

	/**
	 * Returns the list of attributes parsed.
	 * 
	 * @return the list of attributes parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final List<A> getAttributes() {
		return this.attributes;
	}

	/**
	 * Parses the <code>member</code>.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param member
	 *            the member
	 * @param parseDefault
	 *            true if by default the member should be parsed
	 * @param attribute
	 *            the optional ORM Metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract void parseAttribute(String name, Member member, A attribute);

	@SuppressWarnings("unchecked")
	private void probe() {
		// if the ORM Attributes is null or empty then nothing interesting here
		if (this.ormAttributes != null) {

			// process the list of attributes defined by ORM Mapping
			for (final AttributeMetadata attribute : this.ormAttributes) {
				final Member member = this.memberMap.remove(attribute.getName());

				if (member == null) {
					throw new MappingException("The attribute " + attribute.getName() + " cannot be found.", attribute.getLocation());
				}

				// parse the attribute
				this.parseAttribute(attribute.getName(), member, (A) attribute);
			}
		}

		// probe the rest of the properties only if metadata is not complete
		if (!this.parent.isMetadataComplete()) {

			// enumerate all the members
			final Iterator<Entry<String, Member>> i = this.memberMap.entrySet().iterator();
			while (i.hasNext()) {
				final Entry<String, Member> entry = i.next();

				final String name = entry.getKey();
				final Member member = entry.getValue();

				// if the member has an indicative annotation then probed
				if ((this.indicativeAnnotation == null) || (ReflectHelper.getAnnotation(member, this.indicativeAnnotation) != null)) {

					// remove the member as it is probed
					i.remove();

					// parse the attribute
					this.parseAttribute(name, member, null);
				}

			}

		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.attributes.toString();
	}
}

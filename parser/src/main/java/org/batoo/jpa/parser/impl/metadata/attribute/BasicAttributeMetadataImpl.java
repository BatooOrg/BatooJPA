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

import javax.persistence.Basic;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * The implementation of the {@link BasicAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttributeMetadataImpl extends PhysicalAttributeMetadataImpl implements BasicAttributeMetadata {

	private final boolean lob;
	private final EnumType enumType;
	private final boolean optional;
	private final FetchType fetchType;

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param metadata
	 *            the metadata definition of the basic attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttributeMetadataImpl(Member member, BasicAttributeMetadata metadata) {
		super(member, metadata);

		this.lob = metadata.isLob();
		this.enumType = metadata.getEnumType();
		this.optional = metadata.isOptional();
		this.fetchType = metadata.getFetchType();
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
	public BasicAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed);

		final Basic basic = ReflectHelper.getAnnotation(member, Basic.class);
		final Lob lob = ReflectHelper.getAnnotation(member, Lob.class);
		final Enumerated enumerated = ReflectHelper.getAnnotation(member, Enumerated.class);

		parsed.add(Lob.class);
		parsed.add(Basic.class);
		parsed.add(Enumerated.class);
		parsed.add(Basic.class);

		this.optional = basic != null ? basic.optional() : true;
		this.fetchType = basic != null ? basic.fetch() : FetchType.EAGER;
		this.lob = lob != null;
		this.enumType = enumerated != null ? enumerated.value() : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchType getFetchType() {
		return this.fetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLob() {
		return this.lob;
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

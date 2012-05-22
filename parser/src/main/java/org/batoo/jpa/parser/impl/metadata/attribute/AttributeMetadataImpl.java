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

import java.lang.reflect.Field;
import java.lang.reflect.Member;

import javax.persistence.AccessType;

import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.AttributeMetadata;

/**
 * The implementation of {@link AttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributeMetadataImpl implements AttributeMetadata {

	private final Member member;
	private final String name;
	private final AccessType access;
	private final JavaLocator locator;

	/**
	 * @param member
	 *            the java member of attribute
	 * @param metadata
	 *            the metadata definition of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeMetadataImpl(Member member, AttributeMetadata metadata) {
		this(member, metadata.getName(), metadata.getAccess(), metadata.getLocation());
	}

	/**
	 * @param member
	 *            the java member of attribute
	 * @param name
	 *            the name of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeMetadataImpl(Member member, String name) {
		this(member, name, member instanceof Field ? AccessType.FIELD : AccessType.PROPERTY, null);
	}

	private AttributeMetadataImpl(Member member, String name, AccessType access, String ormLocation) {
		super();

		this.name = name;
		this.member = member;
		this.access = access;
		this.locator = new JavaLocator(member, ormLocation);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AccessType getAccess() {
		return this.access;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getLocation() {
		return this.locator.getLocation();
	}

	/**
	 * Returns the locator of the attribute.
	 * 
	 * @return the locator of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JavaLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the java member of the attribute.
	 * 
	 * @return the java member of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final Member getMember() {
		return this.member;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)//
		.excludeFieldNames("access") //
		.toString();
	}
}

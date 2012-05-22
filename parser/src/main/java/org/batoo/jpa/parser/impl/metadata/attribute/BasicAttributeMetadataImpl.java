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

import java.lang.reflect.Member;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Temporal;

import org.batoo.jpa.parser.metadata.BasicAttributeMetadata;

/**
 * The implementation of the {@link BasicAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttributeMetadataImpl extends BasicSingularAttributeMetadataImpl implements BasicAttributeMetadata {

	private final boolean lob;
	private final EnumType enumType;
	private final boolean optional;

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
	}

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param name
	 *            the name of the basic attribute
	 * @param basic
	 *            the basic definition of the basic attribute
	 * @param enumerated
	 *            the enumerated definition of the basic attribute
	 * @param lob
	 *            the lob definition of the basic attribute
	 * @param column
	 *            the column definition of the basic attribute
	 * @param temporal
	 *            the temporal definition of the basic attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttributeMetadataImpl(Member member, String name, Basic basic, Enumerated enumerated, Lob lob, Column column,
		Temporal temporal) {
		super(member, name, column, basic != null ? basic.fetch() : FetchType.EAGER, temporal);

		this.lob = lob != null;
		this.enumType = enumerated != null ? enumerated.value() : EnumType.ORDINAL;
		this.optional = basic != null ? basic.optional() : true;
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

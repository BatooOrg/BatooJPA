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

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.impl.metadata.ColumnMetadataImpl;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.PhysicalAttributeMetadata;

/**
 * 
 * The implementation of the {@link PhysicalAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicSingularAttributeMetadataImpl extends AttributeMetadataImpl implements PhysicalAttributeMetadata {

	private final ColumnMetadata column;
	private final TemporalType temporalType;
	private final FetchType fetchType;

	/**
	 * @param member
	 *            the java member of singular attribute
	 * @param metadata
	 *            the metadata definition of the singular attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicSingularAttributeMetadataImpl(Member member, PhysicalAttributeMetadata metadata) {
		super(member, metadata);

		this.column = metadata.getColumn();
		this.fetchType = metadata.getFetchType();
		this.temporalType = metadata.getTemporalType();
	}

	/**
	 * @param member
	 *            the java member of singular attribute
	 * @param name
	 *            the name of the singular attribute
	 * @param column
	 *            the column definition of the singular attribute
	 * @param fetch
	 *            the fetch definition of the singular attribute
	 * @param temporal
	 *            the temporal definition of the singular attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicSingularAttributeMetadataImpl(Member member, String name, Column column, FetchType fetch, Temporal temporal) {
		super(member, name);

		this.column = column != null ? new ColumnMetadataImpl(this.getLocator(), column) : null;
		this.fetchType = fetch;
		this.temporalType = temporal != null ? temporal.value() : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final ColumnMetadata getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final FetchType getFetchType() {
		return this.fetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final TemporalType getTemporalType() {
		return this.temporalType;
	}

}

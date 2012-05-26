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

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.batoo.jpa.common.reflect.ReflectHelper;
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
public class PhysicalAttributeMetadataImpl extends AttributeMetadataImpl implements PhysicalAttributeMetadata {

	private final ColumnMetadata column;
	private final TemporalType temporalType;

	/**
	 * @param member
	 *            the java member of singular attribute
	 * @param metadata
	 *            the metadata definition of the singular attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalAttributeMetadataImpl(Member member, PhysicalAttributeMetadata metadata) {
		super(member, metadata);

		this.column = metadata.getColumn();
		this.temporalType = metadata.getTemporalType();
	}

	/**
	 * @param member
	 *            the java member of singular attribute
	 * @param name
	 *            the name of the singular attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		final Column column = ReflectHelper.getAnnotation(member, Column.class);
		final Temporal temporal = ReflectHelper.getAnnotation(member, Temporal.class);

		parsed.add(Column.class);
		parsed.add(Temporal.class);

		this.column = column != null ? new ColumnMetadataImpl(this.getLocator(), column) : null;
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
	public final TemporalType getTemporalType() {
		return this.temporalType;
	}

}

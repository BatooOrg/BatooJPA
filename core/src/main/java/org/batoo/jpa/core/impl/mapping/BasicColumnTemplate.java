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
package org.batoo.jpa.core.impl.mapping;

import javax.persistence.Column;

import org.batoo.jpa.core.impl.types.SingularAttributeImpl;
import org.batoo.jpa.core.jdbc.IdType;

/**
 * Template for the basic column.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicColumnTemplate<X, T> extends ColumnTemplate<X, T> {

	private final IdType idType;
	private final String generator;

	private final int length;
	private final int precision;
	private final int scale;

	/**
	 * Implicit basic column template
	 * 
	 * @param attribute
	 *            the attribute that owns that column
	 * @param idType
	 *            the type of the id generation
	 * @param generator
	 *            the id generator name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicColumnTemplate(SingularAttributeImpl<X, T> attribute, IdType idType, String generator) {
		super(attribute, "", "", idType == null, false, true, true);

		this.idType = idType;
		this.generator = generator;

		this.length = 255;
		this.precision = 0;
		this.scale = 0;
	}

	/**
	 * Explicit basic column template
	 * 
	 * @param attribute
	 *            the attribute that owns that column
	 * @param idType
	 *            the type of the id generation
	 * @param generator
	 *            the id generator name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicColumnTemplate(SingularAttributeImpl<X, T> attribute, IdType idType, String generator, Column column) {
		super(attribute, column.table(), column.name(), column.nullable(), column.unique(), column.insertable(), column.updatable());

		this.idType = idType;
		this.generator = generator;

		this.length = column.length();
		this.precision = column.precision();
		this.scale = column.scale();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SingularAttributeImpl<X, T> getAttribute() {
		return (SingularAttributeImpl<X, T>) super.getAttribute();
	}

	/**
	 * Returns the generator.
	 * 
	 * @return the generator
	 * @since $version
	 */
	public final String getGenerator() {
		return this.generator;
	}

	/**
	 * Returns the idType.
	 * 
	 * @return the idType
	 * @since $version
	 */
	public final IdType getIdType() {
		return this.idType;
	}

	/**
	 * Returns the length.
	 * 
	 * @return the length
	 * @since $version
	 */
	public final int getLength() {
		return this.length;
	}

	/**
	 * Returns the precision.
	 * 
	 * @return the precision
	 * @since $version
	 */
	public final int getPrecision() {
		return this.precision;
	}

	/**
	 * Returns the scale.
	 * 
	 * @return the scale
	 * @since $version
	 */
	public final int getScale() {
		return this.scale;
	}

}

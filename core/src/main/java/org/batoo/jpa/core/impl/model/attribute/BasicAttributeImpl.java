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
package org.batoo.jpa.core.impl.model.attribute;

import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.TypeFactory;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for basic attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttributeImpl<X, T> extends PhysicalAttributeImpl<X, T> {

	private final boolean optional;
	private BasicColumn basicColumn;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttributeImpl(ManagedTypeImpl<X> declaringType, BasicAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.optional = metadata.isOptional();

		this.initColumn(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicColumn getColumn() {
		return this.basicColumn;
	}

	/**
	 * Initializes the basicColumn for the attribute.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initColumn(BasicAttributeMetadata metadata) {
		final int sqlType = TypeFactory.getSqlType(this.getJavaType(), this.getTemporalType(), null, false);

		final JdbcAdaptor jdbcAdaptor = this.getDeclaringType().getMetamodel().getJdbcAdaptor();

		this.basicColumn = new BasicColumn(jdbcAdaptor, this, sqlType, (metadata != null) && (metadata.getColumn() != null)
			? metadata.getColumn() : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}

}

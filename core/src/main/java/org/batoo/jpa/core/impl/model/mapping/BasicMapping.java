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
package org.batoo.jpa.core.impl.model.mapping;

import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.TypeFactory;
import org.batoo.jpa.core.impl.model.attribute.BasicAttributeImpl;
import org.batoo.jpa.core.impl.model.type.BasicTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * Mapping for id attributes.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Y>
 *            the type of the value
 * 
 * @author hceylan
 * @since $version
 */
public class BasicMapping<X, Y> extends PhysicalMapping<X, Y> {

	private final BasicAttributeImpl<? super X, Y> attribute;
	private final BasicColumn column;

	/**
	 * @param entity
	 *            the entity
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping(EntityTypeImpl<X> entity, BasicAttributeImpl<? super X, Y> attribute) {
		super(entity);

		this.attribute = attribute;

		final BasicAttributeMetadata metadata = (BasicAttributeMetadata) attribute.getMetadata();
		final ColumnMetadata columnMetadata = this.getEntity().getAttributeOverride(attribute, this.attribute.getName());
		final int sqlType = TypeFactory.getSqlType(this.attribute.getJavaType(), metadata.getTemporalType(), metadata.getEnumType(),
			metadata.isLob());

		this.column = new BasicColumn(this, sqlType, columnMetadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicAttributeImpl<? super X, Y> getAttribute() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicColumn getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicTypeImpl<Y> getType() {
		return this.attribute.getType();
	}
}

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
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.TypeFactory;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.type.BasicTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Mapping for basic types, id, version, basic attributes.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Y>
 *            the type of the value
 * 
 * @author hceylan
 * @since $version
 */
public class BasicMapping<X, Y> extends SingularMapping<X, Y> {

	private final BasicAttribute<? super X, Y> attribute;
	private BasicColumn column;

	/**
	 * The default constructor.
	 * 
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * @param entity
	 *            the entity
	 * @param attribute
	 *            the basic attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity, BasicAttribute<? super X, Y> attribute) {
		this(parent, entity, attribute, false);
	}

	/**
	 * Constructor for embedded attribute sub mappings.
	 * 
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * @param entity
	 *            the entity
	 * @param attribute
	 *            the basic attribute
	 * @param id
	 *            if the attribute should be treated as id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity, BasicAttribute<? super X, Y> attribute, boolean id) {
		super(parent, entity);

		this.attribute = attribute;

		final ColumnMetadata columnMetadata = this.getColumnMetadata();
		final int sqlType = TypeFactory.getSqlType(this.getAttribute().getJavaType(), attribute.getTemporalType(), attribute.getEnumType(),
			attribute.isLob());

		if (id || attribute.isId()) {
			this.column = new PkColumn(this, sqlType, columnMetadata);
		}
		else {
			this.column = new BasicColumn(this, sqlType, columnMetadata);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean fillValue(Object instance) {
		return this.getAttribute().fillValue(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final BasicAttribute<? super X, Y> getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the physical column of the attribute.
	 * 
	 * @return the physical column of the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicColumn getColumn() {
		return this.column;
	}

	/**
	 * Returns the effective column metadata for the attribute checking with the parent mappings and entities.
	 * 
	 * @return the column metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ColumnMetadata getColumnMetadata() {
		String path = this.attribute.getName();

		ColumnMetadata metadata = null;

		// check with the parent chain if there is an attribute override
		AbstractMapping<?, ?> mapping = this;
		while (mapping.getParent() != null) {
			final EmbeddedMapping<?, ?> parent = mapping.getParent();

			if (parent.getAttribute().getAttributeOverride(path) != null) {
				metadata = parent.getAttribute().getAttributeOverride(path);
			}

			path = parent.getAttribute().getName() + "." + path;

			mapping = parent;
		}

		// if the embeddable defined in this entity then return it
		if ((metadata != null) && (mapping.getAttribute().getDeclaringType() == this.getEntity())) {
			return metadata;
		}

		// check with the override path
		EntityTypeImpl<? super X> entity = this.getEntity();
		while (entity != null) {
			// only up to declaring entity
			if (entity == mapping.getAttribute().getDeclaringType()) {
				break;
			}

			// if there is an override return it
			metadata = entity.getAttributeOverride(path);
			if (metadata != null) {
				return metadata;
			}

			if (entity.getSupertype() instanceof EntityTypeImpl) {
				entity = (EntityTypeImpl<? super X>) entity.getSupertype();
			}
		}

		// fall back to attribute's column metadata
		return this.attribute.getMetadata().getColumn();
	}

	/**
	 * Returns the bindable entity type.
	 * 
	 * @return the bindable entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final BasicTypeImpl<Y> getType() {
		return this.getAttribute().getType();
	}
}

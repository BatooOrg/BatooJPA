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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.TypeFactory;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.MappedSuperclassTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * The mapping for basic attributes.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since $version
 */
public class BasicMapping<Z, X> extends Mapping<Z, X, X> implements SingularMapping<Z, X> {

	private final BasicAttribute<? super Z, X> attribute;
	private final BasicColumn column;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping(ParentMapping<?, Z> parent, BasicAttribute<? super Z, X> attribute) {
		super(parent, attribute, attribute.getJavaType(), attribute.getName());

		this.attribute = attribute;

		final ColumnMetadata columnMetadata = this.getColumnMetadata();
		final int sqlType = TypeFactory.getSqlType(this.attribute.getJavaType(), attribute.getTemporalType(), attribute.getEnumType(), attribute.isLob());

		final JdbcAdaptor jdbcAdaptor = attribute.getMetamodel().getJdbcAdaptor();

		if (this.attribute.isId() || this.getParent().isId()) {
			this.column = new PkColumn(jdbcAdaptor, this, sqlType, columnMetadata);
		}
		else {
			this.column = new BasicColumn(jdbcAdaptor, this, sqlType, columnMetadata);
		}

		final String tableName = this.column.getTableName();

		if (this.getRoot().isEntity()) {
			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.getRoot().getType();
			// if table name is blank, it means the column should belong to the primary table
			if (StringUtils.isBlank(tableName)) {
				this.column.setTable(type.getPrimaryTable());
			}
			// otherwise locate the table
			else {
				final AbstractTable table = type.getTable(tableName);
				if (table == null) {
					throw new MappingException("Table " + tableName + " could not be found", this.column.getLocator());
				}

				this.column.setTable(table);
			}
		}
		else {
			this.column.setTable(((ElementMapping<?>) this.getRoot()).getCollectionTable());
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean fillValue(EntityTypeImpl<?> type, ManagedInstance<?> managedInstance, Object instance) {
		return this.attribute.fillValue(type, managedInstance, instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicAttribute<? super Z, X> getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the column of the mapping.
	 * 
	 * @return the column of the mapping
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
		ColumnMetadata metadata = null;

		final String path = this.getParent().getRootPath(this.attribute.getName());
		final AttributeImpl<?, ?> rootAttribute = this.getParent().getRootAttribute(this.attribute);

		/**
		 * The priorities are like below:
		 * 
		 * 1. If the root attribute is defined in the root type (thus the entity) then locate the attribute override on the attribute chain<br />
		 * 2. If the root attribute is defined in a parent mapped super class then locate the attribute on the entity<br />
		 * 3. If the parent is an embeddable mapping then locate the attribute override again on the attribute chain<br />
		 * 4. return the column metadata from the attribute<br />
		 */

		// Clause 0
		if ((rootAttribute.getDeclaringType() == this.getRoot().getType()) && (this.getParent() instanceof EmbeddedMapping)) {
			metadata = ((EmbeddedMapping<?, ?>) this.getParent()).getAttributeOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 1
		if ((rootAttribute.getDeclaringType() == this.getRoot().getType()) && (this.getParent() instanceof EmbeddedMapping)) {
			metadata = ((EmbeddedMapping<?, ?>) this.getParent()).getAttributeOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 2
		if (rootAttribute.getDeclaringType() instanceof MappedSuperclassTypeImpl) {
			metadata = ((EntityTypeImpl<?>) this.getRoot().getType()).getAttributeOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 3
		if (this.getParent() instanceof EmbeddedMapping) {
			metadata = ((EmbeddedMapping<?, ?>) this.getParent()).getAttributeOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 4: fall back to attribute's column metadata
		return this.attribute.getMetadata().getColumn();
	}
}

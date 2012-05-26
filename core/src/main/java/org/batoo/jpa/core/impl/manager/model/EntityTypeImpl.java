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
package org.batoo.jpa.core.impl.manager.model;

import java.util.Collection;
import java.util.Map;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.manager.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.manager.jdbc.EntityTable;
import org.batoo.jpa.core.impl.manager.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.manager.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.model.attribute.PhysicalAttributeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link EntityType}.
 * 
 * @param <X>
 *            The represented entity type
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

	private final String name;
	private EntityTable primaryTable;
	private final Map<String, AbstractTable> tables = Maps.newHashMap();

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> parent, Class<X> javaType, EntityMetadata metadata) {
		super(metamodel, parent, javaType, metadata);

		this.name = metadata.getName();

		this.initTables(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<X> getBindableJavaType() {
		return this.getJavaType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BindableType getBindableType() {
		return BindableType.ENTITY_TYPE;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the tables of the entity.
	 * 
	 * @return the tables of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<AbstractTable> getTables() {
		return this.tables.values();
	}

	/**
	 * Initializes the tables.
	 * 
	 * @since $version
	 * @author hceylan
	 * @param metadata
	 */
	private void initTables(EntityMetadata metadata) {
		this.primaryTable = new EntityTable(this, metadata.getTable());

		this.tables.put(this.primaryTable.getName(), this.primaryTable);

		for (final SecondaryTableMetadata secondaryTableMetadata : metadata.getSecondaryTables()) {
			this.tables.put(secondaryTableMetadata.getName(), new SecondaryTable(this, secondaryTableMetadata));
		}

		for (final SingularAttribute<X, ?> attribute : this.getDeclaredSingularAttributes()) {
			if (attribute instanceof PhysicalAttributeImpl) {
				final PhysicalColumn column = ((PhysicalAttributeImpl<X, ?>) attribute).getColumn();
				final String tableName = column.getTableName();

				// if table name is blank, it means the column should belong to the primary table
				if (StringUtils.isBlank(tableName)) {
					column.setTable(this.primaryTable);
				}
				// otherwise locate the table
				else {
					final AbstractTable table = this.tables.get(tableName);
					if (table == null) {
						throw new MappingException("Table " + tableName + " could not be found", column.getLocator());
					}

					column.setTable(table);
				}
			}
		}
	}
}

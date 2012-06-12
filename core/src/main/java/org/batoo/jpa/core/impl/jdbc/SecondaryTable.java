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
package org.batoo.jpa.core.impl.jdbc;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;

/**
 * Table representing an secondary table for entity persistent storage.
 * 
 * @author hceylan
 * @since $version
 */
public class SecondaryTable extends EntityTable {

	private List<PrimaryKeyJoinColumnMetadata> metadata;
	private ForeignKey foreignKey;

	/**
	 * Default secondary table constructor.
	 * 
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the table metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SecondaryTable(EntityTypeImpl<?> entity, SecondaryTableMetadata metadata) {
		super(entity, metadata);

		this.metadata = metadata.getPrimaryKeyJoinColumnMetadata();
	}

	/**
	 * Constructor primary table as join table in inheritance.
	 * 
	 * @param entity
	 *            the root entity
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SecondaryTable(EntityTypeImpl<?> entity, TableMetadata metadata) {
		super(entity, metadata);
	}

	/**
	 * @param primaryTableAlias
	 *            the primary table alias
	 * @param alias
	 *            the table alias
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String joinPrimary(String primaryTableAlias, String alias) {
		return this.foreignKey.createSourceJoin(JoinType.LEFT, primaryTableAlias, alias);
	}

	/**
	 * Links the secondary table to the primary table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link() {
		if (this.foreignKey == null) {
			this.foreignKey = new ForeignKey(this, this.getEntity(), this.metadata);
		}
	}
}

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

import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;

/**
 * Table representing an secondary table for entity persistent storage.
 * 
 * @author hceylan
 * @since $version
 */
public class SecondaryTable extends EntityTable {

	/**
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
	}

	/**
	 * @param primaryTableAlias
	 * @param alias
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String joinPrimary(String primaryTableAlias, String alias) {
		// TODO Auto-generated method stub
		return null;
	}
}

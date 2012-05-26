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

import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Column to persist id attributes of the entity.
 * 
 * @author hceylan
 * @since $version
 */
public class PkPhysicalColumn extends PhysicalColumn {

	private final IdType idType;

	/**
	 * @param attribute
	 *            the attribute
	 * @param sqlType
	 *            the SQL type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PkPhysicalColumn(IdAttributeImpl<?, ?> attribute, int sqlType, ColumnMetadata metadata) {
		super(attribute, sqlType, metadata);

		this.idType = attribute.getIdType();
	}

	/**
	 * Returns the idType of the column.
	 * 
	 * @return the idType of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdType getIdType() {
		return this.idType;
	}
}

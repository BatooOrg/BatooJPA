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
package org.batoo.jpa.parser.metadata.attribute;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Definitions for plural attributes.
 * 
 * @author hceylan
 * @since $version
 */
public interface PluralAttributeMetadata {

	/**
	 * Returns the map key.
	 * 
	 * @return the map key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getMapKey();

	/**
	 * Returns the list of map key attribute overrides.
	 * 
	 * @return the list of map key attribute overrides
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<AttributeOverrideMetadata> getMapKeyAttributeOverrides();

	/**
	 * Returns the class name of the map key.
	 * 
	 * @return the class name of the map key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getMapKeyClassName();

	/**
	 * Returns the map key column definition.
	 * 
	 * @return the map key column definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ColumnMetadata getMapKeyColumn();

	/**
	 * Returns the enum type of the map key.
	 * 
	 * @return the enum type of the map key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	EnumType getMapKeyEnumType();

	/**
	 * Returns the temporal type of the map key.
	 * 
	 * @return the temporal type of the map key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TemporalType getMapKeyTemporalType();

	/**
	 * Returns the order by.
	 * 
	 * @return the order by
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getOrderBy();

	/**
	 * Returns the order column definition.
	 * 
	 * @return the order column definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ColumnMetadata getOrderColumn();
}

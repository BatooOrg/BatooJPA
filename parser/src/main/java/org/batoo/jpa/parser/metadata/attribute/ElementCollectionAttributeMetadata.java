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

import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * Definition for element collection attributes.
 * 
 * @author hceylan
 * @since $version
 */
public interface ElementCollectionAttributeMetadata extends PluralAttributeMetadata, EmbeddedAttributeMetadata {

	/**
	 * Returns the collection table definition.
	 * 
	 * @return the collection table definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	CollectionTableMetadata getCollectionTable();

	/**
	 * Returns the column definition.
	 * 
	 * @return the column definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ColumnMetadata getColumn();

	/**
	 * Returns the enum type.
	 * 
	 * @return the enum type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	EnumType getEnumType();

	/**
	 * Returns the fetch type.
	 * 
	 * @return the fetch type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	FetchType getFetchType();

	/**
	 * Returns the name of the target class.
	 * 
	 * @return the name of the target class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTargetClass();

	/**
	 * Returns the temporal type.
	 * 
	 * @return the enum type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TemporalType getTemporalType();

	/**
	 * Returns if the attribute is lob type.
	 * 
	 * @return true if the attribute is lob type, false othwerwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isLob();
}

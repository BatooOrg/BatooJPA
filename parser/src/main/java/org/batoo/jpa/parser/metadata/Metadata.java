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
package org.batoo.jpa.parser.metadata;

import java.util.List;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

/**
 * The root of the meta model.
 * 
 * @author hceylan
 * @since $version
 */
public interface Metadata {

	/**
	 * Return if persists should be cascaded by default.
	 * 
	 * @return true if persists should be cascaded by default, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadePersists();

	/**
	 * Returns the default access type.
	 * 
	 * @return the default access type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	AccessType getAccessType();

	/**
	 * Returns the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getCatalog();

	/**
	 * Returns the list of entity listeners.
	 * 
	 * @return the list of entity Listeners
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<EntityListenerMetadata> getEntityListeners();

	/**
	 * Returns the entity mappings.
	 * 
	 * @return the entity mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<ManagedTypeMetadata> getEntityMappings();

	/**
	 * Returns the list of named native queries.
	 * 
	 * @return the list of named native queries
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<NamedNativeQueryMetadata> getNamedNativeQueries();

	/**
	 * Returns the list of named queries.
	 * 
	 * @return the list of named queries
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<NamedQueryMetadata> getNamedQueries();

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getSchema();

	/**
	 * Returns the list of sequence generators.
	 * 
	 * @return the list of sequence generators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<SequenceGeneratorMetadata> getSequenceGenerators();

	/**
	 * Returns the list of table generators.
	 * 
	 * @return the list of table generators
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<TableGeneratorMetadata> getTableGenerators();

	/**
	 * Returns if the xml mapping metadata is complete.
	 * 
	 * @return true if the xml mapping metadata is complete, false otherwise.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isXmlMappingMetadataComplete();
}

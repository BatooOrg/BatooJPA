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
package org.batoo.jpa.parser.impl.orm;

/**
 * Constants
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("javadoc")
public class ElementConstants {

	/**
	 * Default values.
	 */
	protected static final String FALSE = null;
	protected static final String EMPTY = "";

	protected static final String ATTR_NAME = "name";

	/**
	 * Elements
	 */
	public static final String ELEMENT_ENTITY_MAPPINGS = "entity-mappings";
	protected static final String ELEMENT_ACCESS = "access";
	protected static final String ELEMENT_ASSOCIATION_OVERRIDE = "association-override";
	protected static final String ELEMENT_ATTRIBUTE_OVERRIDE = "attribute-override";
	protected static final String ELEMENT_ATTRIBUTES = "attributes";
	protected static final String ELEMENT_BASIC = "basic";
	protected static final String ELEMENT_CASCADE = "cascade";
	protected static final String ELEMENT_CASCADE_ALL = "cascade-all";
	protected static final String ELEMENT_CASCADE_DETACH = "cascade-detach";
	protected static final String ELEMENT_CASCADE_MERGE = "cascade-merge";
	protected static final String ELEMENT_CASCADE_PERSIST = "cascade-persist";
	protected static final String ELEMENT_CASCADE_REFRESH = "cascade-refresh";
	protected static final String ELEMENT_CASCADE_REMOVE = "cascade-remove";
	protected static final String ELEMENT_COLUMN = "column";
	protected static final String ELEMENT_COLUMN_NAME = "column-name";
	protected static final String ELEMENT_EMBEDDED = "embedded";
	protected static final String ELEMENT_EMBEDDED_ID = "embedded-id";
	protected static final String ELEMENT_MAPPED_SUPERCLASS = "mapped-superclass";
	protected static final String ELEMENT_ENTITY = "entity";
	protected static final String ELEMENT_ENUMERATED = "enumerated";
	protected static final String ELEMENT_GENERATED_VALUE = "generated-value";
	protected static final String ELEMENT_ID = "id";
	protected static final String ELEMENT_INVERSE_JOIN_COLUMN = "inverse-join-column";
	protected static final String ELEMENT_JOIN_COLUMN = "join-column";
	protected static final String ELEMENT_JOIN_TABLE = "join-table";
	protected static final String ELEMENT_LOB = "lob";
	protected static final String ELEMENT_MANY_TO_MANY = "many-to-many";
	protected static final String ELEMENT_MANY_TO_ONE = "many-to-one";
	protected static final String ELEMENT_ONE_TO_MANY = "one-to-many";
	protected static final String ELEMENT_ONE_TO_ONE = "one-to-one";
	protected static final String ELEMENT_PRIMARY_KEY_JOIN_COLUMN = "primary-key-join-column";
	protected static final String ELEMENT_SEQUENCE_GENERATOR = "sequence-generator";
	protected static final String ELEMENT_TABLE_GENERATOR = "sequence-generator";
	protected static final String ELEMENT_TEMPORAL = "temporal";
	protected static final String ELEMENT_TRANSIENT = "transient";
	protected static final String ELEMENT_UNIQUE_CONSTRAINT = "unique-constraint";
	protected static final String ELEMENT_VERSION = "version";
	protected static final String ELEMENT_TABLE = "table";
	protected static final String ELEMENT_SECONDARY_TABLE = "secondary-table";
	protected static final String ELEMENT_INHERITANCE = "inheritance";
	protected static final String ELEMENT_DISCRIMINATOR_COLUMN = "discriminator-column";
	protected static final String ELEMENT_DISCRIMINATOR_VALUE = "discriminator-value";

	/**
	 * Column Attribute names.
	 */
	protected static final String ATTR_ACCESS = "access";
	protected static final String ATTR_COLUMN_DEFINITION = "column";
	protected static final String ATTR_INSERTABLE = "insertable";
	protected static final String ATTR_LENGTH = "length";
	protected static final String ATTR_NULLABLE = "nullable";
	protected static final String ATTR_UNIQUE = "unique";
	protected static final String ATTR_UPDATABLE = "updatable";
	protected static final String ATTR_PRECISION = "precision";
	protected static final String ATTR_SCALE = "scale";
	protected static final String ATTR_REFERENCED_COLUMN_NAME = "referenced-column-name";
	protected static final String ATTR_DISCRIMINATOR_TYPE = "discriminator-type";

	/**
	 * Schema, Catalog, table, etc.
	 */
	protected static final String ATTR_CATALOG = "catalog";
	protected static final String ATTR_SCHEMA = "schema";
	protected static final String ATTR_SEQUENCE_NAME = "sequence-name";
	protected static final String ATTR_TABLE = "table";

	/**
	 * Generators
	 */
	protected static final String ATTR_GENERATOR = "generator";
	protected static final String ATTR_STRATEGY = "strategy";
	protected static final String ATTR_ALLOCATION_SIZE = "allocation-size";
	protected static final String ATTR_INITIAL_VALUE = "initial-value";
	protected static final String ATTR_PK_COLUMN_NAME = "pk-column-name";
	protected static final String ATTR_PK_COLUMN_VALUE = "pk-column-value";
	protected static final String ATTR_VALUE_COLUMN_NAME = "value-column-name";

	/**
	 * Associations
	 */
	protected static final String ATTR_MAPPED_BY = "mapped-by";
	protected static final String ATTR_TARGET_ENTITY = "target-entity";
	protected static final String ATTR_ORPHAN_REMOVAL = "orphan-removal";
	protected static final String ATTR_FETCH = "fetch";
	protected static final String ATTR_OPTIONAL = "optional";

	/**
	 * ManagedType
	 */
	protected static final String ATTR_CLASS = "class";
	protected static final String ATTR_CACHABLE = "cachable";
	protected static final String ATTR_METADATA_COMPLETE = "metadata-complete";
}

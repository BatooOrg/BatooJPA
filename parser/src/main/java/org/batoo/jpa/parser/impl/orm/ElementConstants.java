/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.parser.impl.orm;

/**
 * Constants
 * 
 * @author hceylan
 * @since 2.0.0
 */
@SuppressWarnings("javadoc")
public class ElementConstants {

	/**
	 * Default values.
	 */
	protected static final String FALSE = null;
	protected static final String EMPTY = "";

	/**
	 * Elements
	 */
	public static final String ELEMENT_ENTITY_MAPPINGS = "entity-mappings";
	protected static final String ELEMENT_PERSISTENT_UNIT_METADATA = "persistence-unit-metadata";
	protected static final String ELEMENT_ACCESS = "access";
	protected static final String ELEMENT_ASSOCIATION_OVERRIDE = "association-override";
	protected static final String ELEMENT_ATTRIBUTE_OVERRIDE = "attribute-override";
	protected static final String ELEMENT_ATTRIBUTES = "attributes";
	protected static final String ELEMENT_BASIC = "basic";
	protected static final String ELEMENT_CATALOG = "catalog";
	protected static final String ELEMENT_XML_MAPPING_METADATA_COMPLETE = "xml-mapping-metadata-complete";
	protected static final String ELEMENT_PERSISTENCE_UNIT_DEFAULTS = "persistence-unit-defaults";
	protected static final String ELEMENT_SCHEMA = "schema";
	protected static final String ELEMENT_CASCADE = "cascade";
	protected static final String ELEMENT_CASCADE_ALL = "cascade-all";
	protected static final String ELEMENT_CASCADE_DETACH = "cascade-detach";
	protected static final String ELEMENT_CASCADE_MERGE = "cascade-merge";
	protected static final String ELEMENT_CASCADE_PERSIST = "cascade-persist";
	protected static final String ELEMENT_CASCADE_REFRESH = "cascade-refresh";
	protected static final String ELEMENT_CASCADE_REMOVE = "cascade-remove";
	protected static final String ELEMENT_COLUMN = "column";
	protected static final String ELEMENT_MAP_KEY_COLUMN = "map-key-column";
	protected static final String ELEMENT_COLUMN_NAME = "column-name";
	protected static final String ELEMENT_EMBEDDED = "embedded";
	protected static final String ELEMENT_EMBEDDED_ID = "embedded-id";
	protected static final String ELEMENT_MAPPED_SUPERCLASS = "mapped-superclass";
	protected static final String ELEMENT_EMBEDDABLE = "embeddable";
	protected static final String ELEMENT_ENTITY = "entity";
	protected static final String ELEMENT_ENUMERATED = "enumerated";
	protected static final String ELEMENT_GENERATED_VALUE = "generated-value";
	protected static final String ELEMENT_HINT = "hint";
	protected static final String ELEMENT_ID = "id";
	protected static final String ELEMENT_ID_CLASS = "id-class";
	protected static final String ELEMENT_INVERSE_JOIN_COLUMN = "inverse-join-column";
	protected static final String ELEMENT_JOIN_COLUMN = "join-column";
	protected static final String ELEMENT_COLLECTION_TABLE = "collection-table";
	protected static final String ELEMENT_JOIN_TABLE = "join-table";
	protected static final String ELEMENT_LOB = "lob";
	protected static final String ELEMENT_LOCK_MODE = "lock-mode";
	protected static final String ELEMENT_ELEMENT_COLLECTION = "element-collection";
	protected static final String ELEMENT_MANY_TO_MANY = "many-to-many";
	protected static final String ELEMENT_MANY_TO_ONE = "many-to-one";
	protected static final String ELEMENT_ONE_TO_MANY = "one-to-many";
	protected static final String ELEMENT_ONE_TO_ONE = "one-to-one";
	protected static final String ELEMENT_MAP_KEY = "map-key";
	protected static final String ELEMENT_MAP_KEY_ATTRIBUTE_OVERRIDE = "map-key-attribute-override";
	protected static final String ELEMENT_MAP_KEY_CLASS = "map-key-class";
	protected static final String ELEMENT_MAP_KEY_ENUMERATED = "map-key-enumerated";
	protected static final String ELEMENT_MAP_KEY_TEMPORAL = "map-key-temporal";
	protected static final String ELEMENT_NAMED_QUERY = "named-query";
	protected static final String ELEMENT_NAMED_NATIVE_QUERY = "named-native-query";
	protected static final String ELEMENT_SQL_RESULT_SET_MAPPING = "sql-result-set-mapping";
	protected static final String ELEMENT_COLUMN_RESULT = "column-result";
	protected static final String ELEMENT_FIELD_RESULT = "field-result";
	protected static final String ELEMENT_ENTITY_RESULT = "entity-result";
	protected static final String ELEMENT_QUERY = "query";
	protected static final String ELEMENT_ORDER_BY = "order-by";
	protected static final String ELEMENT_ORDER_COLUMN = "order-column";
	protected static final String ELEMENT_PRIMARY_KEY_JOIN_COLUMN = "primary-key-join-column";
	protected static final String ELEMENT_SEQUENCE_GENERATOR = "sequence-generator";
	protected static final String ELEMENT_TABLE_GENERATOR = "table-generator";
	protected static final String ELEMENT_TEMPORAL = "temporal";
	protected static final String ELEMENT_TRANSIENT = "transient";
	protected static final String ELEMENT_UNIQUE_CONSTRAINT = "unique-constraint";
	protected static final String ELEMENT_VERSION = "version";
	protected static final String ELEMENT_TABLE = "table";
	protected static final String ELEMENT_SECONDARY_TABLE = "secondary-table";
	protected static final String ELEMENT_INHERITANCE = "inheritance";
	protected static final String ELEMENT_DISCRIMINATOR_COLUMN = "discriminator-column";
	protected static final String ELEMENT_DISCRIMINATOR_VALUE = "discriminator-value";
	protected static final String ELEMENT_EXCLUDE_DEFAULT_LISTENERS = "exclude-default-listeners";
	protected static final String ELEMENT_EXCLUDE_SUPERCLASS_LISTENERS = "exclude-superclass-listeners";
	protected static final String ELEMENT_ENTITY_LISTENERS = "entity-listeners";
	protected static final String ELEMENT_ENTITY_LISTENER = "entity-listener";
	protected static final String ELEMENT_POST_LOAD = "post-load";
	protected static final String ELEMENT_POST_PERSIST = "post-persist";
	protected static final String ELEMENT_POST_REMOVE = "post-remove";
	protected static final String ELEMENT_POST_UPDATE = "post-update";
	protected static final String ELEMENT_PRE_PERSIST = "pre-persist";
	protected static final String ELEMENT_PRE_REMOVE = "pre-remove";
	protected static final String ELEMENT_PRE_UPDATE = "pre-update";

	/**
	 * Atrributes
	 */
	protected static final String ATTR_NAME = "name";
	protected static final String ATTR_VALUE = "value";
	protected static final String ATTR_RESULT_CLASS = "result-class";
	protected static final String ATTR_RESULT_SET_MAPPING = "result-set-mapping";
	protected static final String ATTR_COLUMN = "column";
	public static final String ATTR_DISCRIMINATOR_COLUMN = "discriminator-column";
	public static final String ATTR_ENTITY_CLASS = "entity-class";

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
	protected static final String ATTR_ID = "id";
	protected static final String ATTR_MAPS_ID = "maps-id";

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
	protected static final String ATTR_TARGET_CLASS = "target-entity";
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

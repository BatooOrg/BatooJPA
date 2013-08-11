/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.parser.metadata;

import java.util.List;

import javax.persistence.AccessType;

import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

/**
 * The root of the meta model.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface Metadata {

	/**
	 * Return if persists should be cascaded by default.
	 * 
	 * @return true if persists should be cascaded by default, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean cascadePersists();

	/**
	 * Returns the default access type.
	 * 
	 * @return the default access type
	 * 
	 * @since 2.0.0
	 */
	AccessType getAccessType();

	/**
	 * Returns the catalog.
	 * 
	 * @return the catalog
	 * 
	 * @since 2.0.0
	 */
	String getCatalog();

	/**
	 * Returns the list of entity listeners.
	 * 
	 * @return the list of entity Listeners
	 * 
	 * @since 2.0.0
	 */
	List<EntityListenerMetadata> getEntityListeners();

	/**
	 * Returns the entity mappings.
	 * 
	 * @return the entity mappings
	 * 
	 * @since 2.0.0
	 */
	List<ManagedTypeMetadata> getEntityMappings();

	/**
	 * Returns the list of named native queries.
	 * 
	 * @return the list of named native queries
	 * 
	 * @since 2.0.0
	 */
	List<NamedNativeQueryMetadata> getNamedNativeQueries();

	/**
	 * Returns the list of named queries.
	 * 
	 * @return the list of named queries
	 * 
	 * @since 2.0.0
	 */
	List<NamedQueryMetadata> getNamedQueries();

	/**
	 * Returns the schema.
	 * 
	 * @return the schema
	 * 
	 * @since 2.0.0
	 */
	String getSchema();

	/**
	 * Returns the list of sequence generators.
	 * 
	 * @return the list of sequence generators
	 * 
	 * @since 2.0.0
	 */
	List<SequenceGeneratorMetadata> getSequenceGenerators();

	/**
	 * Returns the list of SqlResultSetMappingMetadata
	 * 
	 * @return the list of SqlResultSetMappingMetadata
	 * 
	 * @since 2.0.1
	 */
	List<SqlResultSetMappingMetadata> getSqlResultSetMapping();

	/**
	 * Returns the list of table generators.
	 * 
	 * @return the list of table generators
	 * 
	 * @since 2.0.0
	 */
	List<TableGeneratorMetadata> getTableGenerators();

	/**
	 * Returns if the xml mapping metadata is complete.
	 * 
	 * @return true if the xml mapping metadata is complete, false otherwise.
	 * 
	 * @since 2.0.0
	 */
	boolean isXmlMappingMetadataComplete();
}

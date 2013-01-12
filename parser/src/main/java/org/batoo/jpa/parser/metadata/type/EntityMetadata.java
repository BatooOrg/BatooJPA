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
package org.batoo.jpa.parser.metadata.type;

import java.util.List;

import javax.persistence.InheritanceType;

import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.BindableMetadata;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;
import org.batoo.jpa.parser.metadata.IndexMetadata;
import org.batoo.jpa.parser.metadata.NamedNativeQueryMetadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.SqlResultSetMappingMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;

/**
 * The definition of the entities.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface EntityMetadata extends IdentifiableTypeMetadata, BindableMetadata {

	/**
	 * Returns the associationOverrides of the entity.
	 * 
	 * @return the associationOverrides of the entity
	 * 
	 * @since 2.0.0
	 */
	List<AssociationMetadata> getAssociationOverrides();

	/**
	 * Returns the attributeOverrides of the entity.
	 * 
	 * @return the attributeOverrides of the entity
	 * 
	 * @since 2.0.0
	 */
	List<AttributeOverrideMetadata> getAttributeOverrides();

	/**
	 * Returns if the entity is cachable.
	 * 
	 * @return true if the entity is cachable
	 * 
	 * @since 2.0.0
	 */
	Boolean getCacheable();

	/**
	 * Returns the discriminator column definition of the entity.
	 * 
	 * @return the discriminator column definition of the entity
	 * 
	 * @since 2.0.0
	 */
	DiscriminatorColumnMetadata getDiscriminatorColumn();

	/**
	 * Returns the discriminator value for the entity.
	 * 
	 * @return the discriminator value for the entity
	 * 
	 * @since 2.0.0
	 */
	String getDiscriminatorValue();

	/**
	 * Returns the list of indexes of the entity.
	 * 
	 * @return the list of indexes of the entity
	 * 
	 * @since 2.0.0
	 */
	List<IndexMetadata> getIndexes();

	/**
	 * Returns the inheritance type of the entity.
	 * 
	 * @return the inheritance type of the entity
	 * 
	 * @since 2.0.0
	 */
	InheritanceType getInheritanceType();

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
	 * Returns the list secondary tables of the entity.
	 * 
	 * @return the list secondary tables of the entity
	 * 
	 * @since 2.0.0
	 */
	List<SecondaryTableMetadata> getSecondaryTables();

	/**
	 * Returns the sequence Generator of the entity.
	 * 
	 * @return the sequence Generator of the entity
	 * 
	 * @since 2.0.0
	 */
	SequenceGeneratorMetadata getSequenceGenerator();

	/**
	 * Returns the list of SqlResultSetMappingMetadata
	 * 
	 * @return the list of SqlResultSetMappingMetadata
	 * 
	 * @since $version
	 */
	List<SqlResultSetMappingMetadata> getSqlResultSetMappings();

	/**
	 * Returns the table of the entity.
	 * 
	 * @return the table of the entity
	 * 
	 * @since 2.0.0
	 */
	TableMetadata getTable();

	/**
	 * Returns the table generator of the entity.
	 * 
	 * @return the table Generator of the entity
	 * 
	 * @since 2.0.0
	 */
	TableGeneratorMetadata getTableGenerator();
}

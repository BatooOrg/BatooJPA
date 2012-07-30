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
package org.batoo.jpa.parser.metadata.type;

import java.util.List;

import javax.persistence.InheritanceType;

import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.BindableMetadata;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;

/**
 * The definition of the entities.
 * 
 * @author hceylan
 * @since $version
 */
public interface EntityMetadata extends IdentifiableTypeMetadata, BindableMetadata {

	/**
	 * Returns the associationOverrides of the entity.
	 * 
	 * @return the associationOverrides of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<AssociationMetadata> getAssociationOverrides();

	/**
	 * Returns the attributeOverrides of the entity.
	 * 
	 * @return the attributeOverrides of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<AttributeOverrideMetadata> getAttributeOverrides();

	/**
	 * Returns if the entity is cachable.
	 * 
	 * @return true if the entity is cachable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Boolean getCacheable();

	/**
	 * Returns the discriminator column definition of the entity.
	 * 
	 * @return the discriminator column definition of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	DiscriminatorColumnMetadata getDiscriminatorColumn();

	/**
	 * Returns the discriminator value for the entity.
	 * 
	 * @return the discriminator value for the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getDiscriminatorValue();

	/**
	 * Returns the inheritance type of the entity.
	 * 
	 * @return the inheritance type of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	InheritanceType getInheritanceType();

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
	 * Returns the list secondary tables of the entity.
	 * 
	 * @return the list secondary tables of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<SecondaryTableMetadata> getSecondaryTables();

	/**
	 * Returns the sequence Generator of the entity.
	 * 
	 * @return the sequence Generator of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	SequenceGeneratorMetadata getSequenceGenerator();

	/**
	 * Returns the table of the entity.
	 * 
	 * @return the table of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TableMetadata getTable();

	/**
	 * Returns the table generator of the entity.
	 * 
	 * @return the table Generator of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TableGeneratorMetadata getTableGenerator();
}

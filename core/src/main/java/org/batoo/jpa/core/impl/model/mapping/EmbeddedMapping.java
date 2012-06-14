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
package org.batoo.jpa.core.impl.model.mapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.parser.MappingException;

import com.google.common.collect.Maps;

/**
 * The mapping for embedded attributes.
 * 
 * @param <X>
 *            the type of the entity
 * @param <Y>
 *            the inverse entity type
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedMapping<X, Y> extends SingularMapping<X, Y> {

	private final EmbeddedAttribute<? super X, Y> attribute;
	private final Map<String, AbstractMapping<? super X, ?>> mappings = Maps.newHashMap();

	/**
	 * 
	 * @param parent
	 *            the parent mapping, may be <code>null</code>
	 * 
	 * @param entity
	 *            the entity
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedMapping(EmbeddedMapping<?, ?> parent, EntityTypeImpl<X> entity, EmbeddedAttribute<? super X, Y> attribute) {
		super(parent, entity);

		this.attribute = attribute;

		this.link();
	}

	/**
	 * Adds the associations that are within the the embeddable.
	 * 
	 * @param associations
	 *            the list to add associations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void addAssociations(List<AssociationMapping<?, ?, ?>> associations) {
		for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
			if (mapping instanceof AssociationMapping) {
				associations.add((AssociationMapping<? super X, ?, ?>) mapping);
			}

			if (mapping instanceof EmbeddedMapping) {
				final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) mapping;
				embeddedMapping.addAssociations(associations);
			}
		}
	}

	/**
	 * Adds the basic attributes that are within the the embeddable.
	 * 
	 * @param basicMappings
	 *            the list to add basic mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addBasicAttributes(List<BasicMapping<?, ?>> basicMappings) {
		for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
			if (mapping instanceof BasicMapping) {
				basicMappings.add((BasicMapping<?, ?>) mapping);
			}

			if (mapping instanceof EmbeddedMapping) {
				final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) mapping;
				embeddedMapping.addBasicAttributes(basicMappings);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean fillValue(Object instance) {
		final Y value = this.get(instance);

		if (value == null) {
			throw new NullPointerException();
		}

		for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
			// mapping is another embedded mapping
			if (mapping instanceof EmbeddedMapping) {
				((EmbeddedMapping<? super X, Y>) mapping).fillValue(value);
			}
			// mapping is basic mapping
			else if (mapping instanceof BasicMapping) {
				((BasicMapping<? super X, Y>) mapping).getAttribute().fillValue(instance);
			}
			// no other mappings allowed in id classes
			else {
				throw new MappingException("Embbeded ids can pnly have basic and embedded attributes.", mapping.getAttribute().getLocator());
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddedAttribute<? super X, Y> getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the mappings of the EmbeddedMapping.
	 * 
	 * @return the mappings of the EmbeddedMapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<AbstractMapping<? super X, ?>> getMappings() {
		return this.mappings.values();
	}

	/**
	 * Links the embedded attributes.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void link() {
		for (final Attribute<? super Y, ?> attribute : this.attribute.getType().getAttributes()) {
			AbstractMapping<X, Y> mapping = null;

			switch (attribute.getPersistentAttributeType()) {
				case BASIC:
					mapping = new BasicMapping(this, this.getEntity(), (BasicAttribute) attribute, this.getAttribute().isId());
					this.mappings.put(attribute.getName(), mapping);

					final BasicColumn basicColumn = ((BasicMapping) mapping).getColumn();
					final String tableName = basicColumn.getTableName();

					// if table name is blank, it means the column should belong to the primary table
					if (StringUtils.isBlank(tableName)) {
						basicColumn.setTable(this.getEntity().getPrimaryTable());
					}
					// otherwise locate the table
					else {
						final AbstractTable table = this.getEntity().getTable(tableName);
						if (table == null) {
							throw new MappingException("Table " + tableName + " could not be found", basicColumn.getLocator());
						}

						basicColumn.setTable(table);
					}

					break;
				case ONE_TO_ONE:
				case MANY_TO_ONE:
					mapping = new SingularAssociationMapping(this, this.getEntity(), (AssociatedSingularAttribute) attribute);
					this.mappings.put(attribute.getName(), mapping);
					break;
				case MANY_TO_MANY:
				case ONE_TO_MANY:
					mapping = new PluralAssociationMapping(this, this.getEntity(), (PluralAttributeImpl) attribute);
					this.mappings.put(attribute.getName(), mapping);
					break;
				case EMBEDDED:
					mapping = new EmbeddedMapping(this, this.getEntity(), (EmbeddedAttribute) attribute);
					this.mappings.put(attribute.getName(), mapping);
				default:
					break;
			}
		}
	}
}

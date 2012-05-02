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
package org.batoo.jpa.core.impl.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Inheritance;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.mapping.EntityInheritence;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.mapping.TableTemplate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Complimentary implementation of {@link EntityType}.
 * 
 * @author hceylan
 * @since $version
 */
abstract class BaseEntityTypeImpl<X> extends AbstractEntityTypeImpl<X> {

	private static final BLogger LOG = BLogger.getLogger(EntityTypeImpl.class);

	protected EntityInheritence inheritance;
	private Object discriminatorValue;
	private final Map<Object, EntityTypeImpl<? extends X>> discrimination = Maps.newHashMap();

	private PhysicalColumn discriminatorColumn;

	/**
	 * @param metaModel
	 *            the meta model of the persistence
	 * @param supertype
	 *            the mapped super class that this type is extending
	 * @param javaType
	 *            the javatype of the entity
	 * @param name
	 *            name of the entity
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseEntityTypeImpl(MetamodelImpl metaModel, IdentifiableTypeImpl<? super X> supertype, Class<X> javaType)
		throws MappingException {
		super(metaModel, supertype, javaType);
	}

	/**
	 * Returns the discriminator column.
	 * 
	 * @return the discriminator column or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn getDiscriminatorColumn() {
		if (this.getRoot() == this) {
			return this.discriminatorColumn;
		}

		return this.getRoot().getDiscriminatorColumn();
	}

	/**
	 * Returns the discriminatorValue.
	 * 
	 * @return the discriminatorValue
	 * @since $version
	 */
	public Object getDiscriminatorValue() {
		return this.discriminatorValue;
	}

	/**
	 * Returns the set of discriminator values for the type.
	 * 
	 * @return the set of discriminator values for the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<Object> getDiscriminatorValues() {
		return this.discrimination.keySet();
	}

	/**
	 * Returns the Child type for the discrimination value.
	 * 
	 * @param discriminatorValue
	 *            the value for the discriminator
	 * @return the Entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<?> getTypeForDiscriminator(Object discriminatorValue) {
		return this.discrimination.get(discriminatorValue);
	}

	/**
	 * Adds the discrimination mapping.
	 * 
	 * @param discriminatorValue
	 *            the discriminator value
	 * @param child
	 *            the child that maps to discriminator value
	 * @throws MappingException
	 *             thrown in case of a clash
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void mapDiscrimination(Object discriminatorValue, EntityTypeImpl<? extends X> child) throws MappingException {
		final EntityTypeImpl<? extends X> other = this.discrimination.get(discriminatorValue);
		if (other != null) {
			throw new MappingException("Duplicate mapping for discriminator value " + discriminatorValue + " for " + this.javaType + ": "
				+ other.javaType + ", " + child.javaType);
		}

		this.discrimination.put(discriminatorValue, child);

		if (this.getSupertype() instanceof EntityTypeImpl) {
			((EntityTypeImpl<? super X>) this.getSupertype()).mapDiscrimination(discriminatorValue, child);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		super.parse(parsed);

		this.parseInheritence(parsed);

		this.parseDiscrimination(parsed);
	}

	private void parseDiscrimination(Set<Class<? extends Annotation>> parsed) throws MappingException {
		final Class<X> type = this.getJavaType();
		final boolean abztract = Modifier.isAbstract(type.getModifiers());

		final DiscriminatorValue discriminatorValue = type.getAnnotation(DiscriminatorValue.class);
		if (discriminatorValue != null) {
			if (this.getRoot().inheritance == null) {
				throw new MappingException("DiscriminatorValue is not allowed for " + this.javaType + " as it is not part of inheritence");
			}

			if (abztract) {
				throw new MappingException("DiscriminatorValue is not allowed for " + this.javaType + " as it is abstract");
			}

			final String value = discriminatorValue.value();
			switch (this.getRoot().inheritance.getType()) {
				case CHAR:
					if (value.length() != 1) {
						throw new MappingException("DiscriminatorValue should be exactly 1 character long for " + this.javaType
							+ ". Value specified is " + value);
					}
					this.discriminatorValue = new Character(value.charAt(0));
					break;
				case INTEGER:
					try {
						final Integer intValue = Integer.valueOf(value);
						this.discriminatorValue = intValue;
					}
					catch (final NumberFormatException e) {
						throw new MappingException("DiscriminatorValue should be an integer for " + this.javaType + ". Value specified is "
							+ value);
					}
					break;
				default:
					this.discriminatorValue = value;
					if (value.isEmpty()) {
						throw new MappingException("DiscriminatorValue cannot be empty for " + this.javaType);
					}
			}

			parsed.add(DiscriminatorValue.class);
		}
		else if (!abztract) {
			this.discriminatorValue = this.javaType.getSimpleName();
		}

		if ((this.discriminatorValue != null)) {
			this.mapDiscrimination(this.discriminatorValue, (EntityTypeImpl<? extends X>) this);
		}
	}

	private void parseInheritence(Set<Class<? extends Annotation>> parsed) throws BatooException {
		final Class<X> type = this.getJavaType();

		final DiscriminatorColumn discriminatorColumn = type.getAnnotation(DiscriminatorColumn.class);
		if (discriminatorColumn != null) {
			parsed.add(DiscriminatorColumn.class);
		}

		final Inheritance inheritance = type.getAnnotation(Inheritance.class);
		if (inheritance != null) {
			parsed.add(Inheritance.class);
		}

		if ((discriminatorColumn != null) || (inheritance != null)) {
			this.inheritance = new EntityInheritence(discriminatorColumn, inheritance);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void vlink() throws MappingException {
		super.vlink();

		final Set<SingularAttribute<? super X, ?>> idAttributes = Sets.newHashSet();
		for (final SingularAttribute<? super X, ?> attribute : this.getSingularAttributes()) {
			if (attribute.isId()) {
				if (this.getRoot() != this) {
					throw new MappingException("Id attribute is not allowed for type " + this.javaType + " as it is part of inheritence");
				}
				idAttributes.add(attribute);
			}
		}
		this.idAttributes = new SingularAttributeImpl[idAttributes.size()];
		idAttributes.toArray(this.idAttributes);

		this.vlinkTables();
	}

	/**
	 * VLinks tables from super type.
	 * 
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void vlinkTables() throws MappingException {
		// first do the self defined tables
		for (final TableTemplate template : this.tableTemplates.values()) {
			final EntityTable table = new EntityTable((EntityTypeImpl<?>) this, template, this.metaModel.getJdbcAdapter());
			if (template.isPrimary()) {
				this.primaryTable = table;
			}

			this.tables.put(table.getName(), table);
		}

		// Then add in the tables defined by the super types
		final EntityTypeImpl<? super X> superType = this.getRoot();
		if (superType != this) {
			switch (superType.inheritance.getInheritenceType()) {
				case SINGLE_TABLE:
					this.primaryTable = superType.getPrimaryTable();
					break;

				case TABLE_PER_CLASS:
					throw new MappingException("TABLE_PER_CLASS inheritence not yet supported");
			}
		}

		if (this.inheritance != null) {
			// create the discriminator column
			this.discriminatorColumn = new PhysicalColumn(this.primaryTable, this.inheritance);
		}
	}
}

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
package org.batoo.jpa.parser.impl.metadata.type;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.attribute.AttributesMetadataImpl;
import org.batoo.jpa.parser.metadata.AssociationOverrideMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

/**
 * 
 * Implementation of {@link EntityMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityMetadataImpl implements EntityMetadata {

	private final Class<?> clazz;
	private final AccessType defaultAccessType;
	private final EntityMetadata ormMetadata;
	private final AttributesMetadataImpl attributes;

	/**
	 * @param clazz
	 *            the class of the entity
	 * @param defaultAccessType
	 *            the default access supplied by the Persistence XML File
	 * @param ormMetadata
	 *            the optional entity metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityMetadataImpl(Class<?> clazz, AccessType defaultAccessType, EntityMetadata ormMetadata) {
		super();

		this.clazz = clazz;
		this.defaultAccessType = defaultAccessType;
		this.ormMetadata = ormMetadata;

		// check consistency with ORM metadata
		if (ormMetadata != null) {

			// class shouldn't have @MappedSuperclass
			final MappedSuperclass mappedSuperclass = clazz.getAnnotation(MappedSuperclass.class);
			if (mappedSuperclass != null) {
				throw new MappingException("Class " + clazz + " mapped as entity in the ORM File but annotaed as @MappedSuperclass");
			}

			// class shouldn't have @Embeddable
			final Embeddable embeddable = clazz.getAnnotation(Embeddable.class);
			if (embeddable != null) {
				throw new MappingException("Class " + clazz + " mapped as entity in the ORM File but annotaed as @Embeddable");
			}
		}

		// visit the attributes
		this.attributes = new AttributesMetadataImpl(this, ormMetadata != null ? ormMetadata.getAttributes() : null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		// if ORM Metadata specifies then return the access type of the ORM Metadata.
		if ((this.ormMetadata != null) && (this.ormMetadata.getAccessType() != null)) {
			return this.ormMetadata.getAccessType();
		}

		// if entity specifies an access type then value is return, otherwise the default access type provided is returned
		final Access access = this.clazz.getAnnotation(Access.class);
		return access != null ? access.value() : this.defaultAccessType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AssociationOverrideMetadata> getAssociationOverrides() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesMetadata getAttributes() {
		return this.attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean getCachable() {
		// if ORM Metadata specifies then return the value of the ORM Metadata.
		if ((this.ormMetadata != null) && (this.ormMetadata.getCachable() != null)) {
			return this.ormMetadata.getCachable();
		}

		// if entity specifies a value then it is return, otherwise the null value returned indicating no preference
		final Cacheable cacheable = this.clazz.getAnnotation(Cacheable.class);
		return cacheable != null ? cacheable.value() : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClassName() {
		return this.clazz.getName();
	}

	/**
	 * Returns the java class of the entity.
	 * 
	 * @return the java class of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getLocation() {
		return this.clazz.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		// if ORM Metadata specifies then return the name of the ORM Metadata.
		if ((this.ormMetadata != null) && StringUtils.isNotBlank(this.ormMetadata.getName())) {
			return this.ormMetadata.getName();
		}

		// if entity specifies a name then it is return, otherwise the simple name of the class is returned
		final Entity entity = this.clazz.getAnnotation(Entity.class);
		if (entity == null) {
			return this.clazz.getSimpleName();
		}

		return StringUtils.isNotBlank(entity.name()) ? entity.name() : this.clazz.getSimpleName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SequenceGeneratorMetadata getSequenceGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableMetadata getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableGeneratorMetadata getTableGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		// if ORM Metadata specifies then it is returned, otherwise false is returned as default
		return this.ormMetadata != null ? this.ormMetadata.isMetadataComplete() : false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)//
		.excludeFieldNames("ormMetadata", "defaultAccessType") //
		.toString();
	}
}

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
package org.batoo.jpa.core.impl;

import java.util.Set;

import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.parser.impl.annotated.MetadataImpl;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Sets;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class MetamodelImpl implements Metamodel {

	private final Set<EntityType<?>> entities = Sets.newHashSet();

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MetamodelImpl(MetadataImpl metadata) {
		for (final EntityMetadata entity : metadata.getEntities()) {
			try {
				EntityTypeImpl<?> entityType;
				entityType = new EntityTypeImpl(this, Class.forName(entity.getClassName()), entity);
				this.entities.add(entityType);
			}
			catch (final ClassNotFoundException e) {} // not possible at this time
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> EmbeddableType<X> embeddable(Class<X> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> EntityType<X> entity(Class<X> cls) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<EmbeddableType<?>> getEmbeddables() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<EntityType<?>> getEntities() {
		return Sets.newHashSet(this.entities);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ManagedType<?>> getManagedTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> ManagedType<X> managedType(Class<X> cls) {
		// TODO Auto-generated method stub
		return null;
	}

}

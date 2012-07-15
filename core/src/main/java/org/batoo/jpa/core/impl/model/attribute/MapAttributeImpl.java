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
package org.batoo.jpa.core.impl.model.attribute;

import java.util.Map;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.collections.ManagedMap;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.PluralAttributeMetadata;

/**
 * Implementation of {@link ListAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <K>
 *            The key element type of the represented collection
 * @param <V>
 *            The value element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, Map<K, V>, V> implements MapAttribute<X, K, V> {

	private Type<K> keyType;
	private Class<K> keyJavaType;
	private final String mapKey;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            attribute type
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public MapAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		super(declaringType, metadata, attributeType, 1);

		if ((metadata instanceof AssociationAttributeMetadata) && StringUtils.isNotBlank(((AssociationAttributeMetadata) metadata).getTargetEntity())) {
			try {
				this.keyJavaType = (Class<K>) Class.forName(((AssociationAttributeMetadata) metadata).getTargetEntity());
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Target enttity class not found", metadata.getLocator());
			}
		}
		else {
			this.keyJavaType = ReflectHelper.getGenericType(this.getJavaMember(), 0);
		}

		this.mapKey = ((PluralAttributeMetadata) metadata).getMapKey();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.MAP;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<K> getKeyJavaType() {
		return this.keyJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Type<K> getKeyType() {
		if (this.keyType != null) {
			return this.keyType;
		}

		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();

		return this.keyType = metamodel.embeddable(this.keyJavaType) != null ? metamodel.embeddable(this.keyJavaType)
			: metamodel.createBasicType(this.keyJavaType);
	}

	/**
	 * Returns the map key of the Map Attribute.
	 * 
	 * @return the map key of the Map Attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<K, V> newCollection(PluralAssociationMapping<?, Map<K, V>, V> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		return new ManagedMap<X, K, V>(mapping, managedInstance, lazy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<K, V> newCollection(PluralAssociationMapping<?, Map<K, V>, V> mapping, ManagedInstance<?> managedInstance, Object values) {
		return new ManagedMap<X, K, V>(mapping, managedInstance, (Map<? extends K, ? extends V>) values);
	}
}

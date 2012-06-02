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
package org.batoo.jpa.core.impl.model;

import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.ManyToOneAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link ManagedType}.
 * 
 * @param <X>
 *            The represented type.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractManagedTypeImpl<X> extends TypeImpl<X> implements ManagedType<X> {

	private final AbstractLocator locator;
	private final ManagedTypeImpl<? super X> parent;
	private final Map<String, AttributeImpl<X, ?>> declaredAttributes = Maps.newHashMap();
	private final Map<String, AttributeImpl<? super X, ?>> attributes = Maps.newHashMap();

	/**
	 * @param metamodel
	 *            the meta model
	 * @param parent
	 *            the parent type
	 * @param clazz
	 *            the class of the represented type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractManagedTypeImpl(MetamodelImpl metamodel, ManagedTypeImpl<? super X> parent, Class<X> clazz, EntityMetadata metadata) {
		super(metamodel, clazz);

		this.parent = parent;
		this.locator = metadata.getLocator();

		this.addAttributes(metadata);
	}

	/**
	 * Creates and adds the attributes of the managed type from the metadata.
	 * 
	 * @param entity
	 *            the
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addAttributes(EntityMetadata entity) {
		final AttributesMetadata metadata = entity.getAttributes();

		// id attributes
		for (final IdAttributeMetadata id : metadata.getIds()) {
			this.declaredAttributes.put(id.getName(), new IdAttributeImpl((ManagedTypeImpl) this, id));
		}

		// basic attributes
		for (final BasicAttributeMetadata basic : metadata.getBasics()) {
			this.declaredAttributes.put(basic.getName(), new BasicAttributeImpl((ManagedTypeImpl) this, basic));
		}

		// many to one attributes
		for (final ManyToOneAttributeMetadata manyToOne : metadata.getManyToOnes()) {
			this.declaredAttributes.put(manyToOne.getName(), new ManyToOneAttributeImpl((ManagedTypeImpl) this, manyToOne));
		}

		if (this.parent != null) {
			// force parent to initialize
			for (final Attribute<?, ?> attribute : this.parent.getAttributes()) {
				this.attributes.put(attribute.getName(), (AttributeImpl<? super X, ?>) attribute);
			}
		}

		this.attributes.putAll(this.declaredAttributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Attribute<? super X, ?> getAttribute(String name) {
		return this.declaredAttributes.get(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Attribute<? super X, ?>> getAttributes() {
		final Set<Attribute<? super X, ?>> attributes = Sets.newHashSet();

		attributes.addAll(this.attributes.values());

		return attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionAttribute<? super X, ?> getCollection(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Attribute<X, ?> getDeclaredAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Attribute<X, ?>> getDeclaredAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListAttribute<X, ?> getDeclaredList(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SetAttribute<X, ?> getDeclaredSet(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
		final Set<SingularAttribute<X, ?>> attributes = Sets.newHashSet();
		for (final Attribute<? super X, ?> attribute : this.declaredAttributes.values()) {
			if ((attribute instanceof SingularAttributeImpl) && (attribute.getDeclaringType() == this)) {
				attributes.add((SingularAttribute<X, ?>) attribute);
			}
		}

		return attributes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListAttribute<? super X, ?> getList(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the locator of the managed type.
	 * 
	 * @return the locator of the managed type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MapAttribute<? super X, ?, ?> getMap(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the parent of the managed type.
	 * 
	 * @return the parent of the managed type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedTypeImpl<? super X> getParent() {
		return this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SetAttribute<? super X, ?> getSet(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}

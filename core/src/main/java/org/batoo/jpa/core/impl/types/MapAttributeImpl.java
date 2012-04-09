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
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverrides;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.NotImplementedException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.collections.ManagedMap;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;

import com.google.common.collect.Sets;

/**
 * The implementation of {@link MapAttribute}
 * 
 * @since $version
 * @author hceylan
 */
public final class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, Map<K, V>, V> implements MapAttribute<X, K, V> {

	private final Class<K> keyJavaType;

	// Parse phase properties
	private String mapKey;
	private MapKeyColumn mapKeyColumn;
	private final Set<MapKeyJoinColumn> mapKeyJoinColumns;

	// Link phase properties
	private Type<K> keyType;

	/**
	 * @param declaringType
	 *            the type declaring this attribute
	 * @param javaMember
	 *            the {@link Member} this attribute is associated with
	 * @param javaType
	 *            the java type of the member
	 * 
	 * @throws MappingException
	 *             thrown in case of a parsing error *
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public MapAttributeImpl(ManagedType<X> owner, Member javaMember, Class<Map<K, V>> javaType) throws MappingException {
		super(owner, javaMember, javaType, (Class<V>) ReflectHelper.getGenericType(javaMember, 1));

		this.keyJavaType = (Class<K>) ReflectHelper.getGenericType(javaMember, 0);

		this.mapKeyJoinColumns = Sets.newHashSet();
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
		return this.keyType;
	}

	/**
	 * Returns the mapKey.
	 * 
	 * @return the mapKey
	 * @since $version
	 */
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * Returns the mapKeyColumn.
	 * 
	 * @return the mapKeyColumn
	 * @since $version
	 */
	public MapKeyColumn getMapKeyColumn() {
		return this.mapKeyColumn;
	}

	/**
	 * Returns the mapKeyJoinColumns.
	 * 
	 * @return the mapKeyJoinColumns
	 * @since $version
	 */
	public Collection<MapKeyJoinColumn> getMapKeyJoinColumns() {
		return Collections.unmodifiableCollection(this.mapKeyJoinColumns);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void initialize(ManagedInstance<?> managedInstance, SessionImpl session) {
		final ManagedMap<K, V> managedMap = this.newInstance(session, managedInstance);
		managedMap.getMap().putAll(this.get(managedInstance.getInstance()));

		this.set(managedInstance.getInstance(), managedMap);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ManagedMap<K, V> newInstance(SessionImpl session, ManagedInstance<?> managedInstance) {
		return new ManagedMap<K, V>(session, managedInstance, (CollectionMapping<?, Map<K, V>, V>) this.mapping);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Set<Class<? extends Annotation>> parse() throws MappingException {
		final Set<Class<? extends Annotation>> parsed = super.parse();

		final Member member = this.getJavaMember();

		final MapKey mapKey = ReflectHelper.getAnnotation(member, MapKey.class);
		if (mapKey != null) {
			this.mapKey = mapKey.name();

			parsed.add(AttributeOverrides.class);
		}

		final MapKeyColumn mapKeyColumn = ReflectHelper.getAnnotation(member, MapKeyColumn.class);
		if (mapKeyColumn != null) {
			this.mapKeyColumn = mapKeyColumn;

			parsed.add(MapKeyColumn.class);
		}

		final MapKeyJoinColumns mapKeyJoinColumns = ReflectHelper.getAnnotation(member, MapKeyJoinColumns.class);
		if (mapKeyJoinColumns != null) {
			for (final MapKeyJoinColumn mapKeyJoinColumn : mapKeyJoinColumns.value()) {
				this.mapKeyJoinColumns.add(mapKeyJoinColumn);
			}

			parsed.add(MapKeyJoinColumns.class);
		}

		final MapKeyJoinColumn mapKeyJoinColumn = ReflectHelper.getAnnotation(member, MapKeyJoinColumn.class);
		if (mapKeyJoinColumn != null) {
			this.mapKeyJoinColumns.add(mapKeyJoinColumn);

			parsed.add(MapKeyJoinColumn.class);
		}

		return parsed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void set(Object instance, Object value) {
		throw new NotImplementedException();
	}
}

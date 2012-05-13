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
package org.batoo.jpa.core.impl.metamodel;

import java.lang.reflect.Member;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.metamodel.SetAttribute;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.collections.ManagedSet;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;

/**
 * The implementation of {@link SetAttribute}
 * 
 * @since $version
 * @author hceylan
 */
public final class SetAttributeImpl<X, E> extends PluralAttributeImpl<X, Set<E>, E> implements SetAttribute<X, E> {

	/**
	 * @param declaringType
	 *            the type declaring this attribute
	 * @param javaMember
	 *            the {@link Member} this attribute is associated with
	 * @param javaType
	 *            the java type of the member
	 * 
	 * @throws MappingException
	 *             thrown in case of a parsing error
	 *             *
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public SetAttributeImpl(ManagedTypeImpl<X> owner, Member javaMember, Class<Set<E>> javaType) throws MappingException {
		super(owner, javaMember, javaType, (Class<E>) ReflectHelper.getGenericType(javaMember, 0));
	}

	/**
	 * Cloning constructor
	 * 
	 * @param declaringType
	 *            the type redeclaring this attribute
	 * @param original
	 *            the original
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SetAttributeImpl(ManagedTypeImpl<X> declaringType, SetAttributeImpl<?, E> original) {
		super(declaringType, original);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 * 
	 */
	@Override
	public <T> SetAttributeImpl<T, E> clone(ManagedTypeImpl<T> declaringType) {
		return new SetAttributeImpl<T, E>(declaringType, this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.SET;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void newInstance(ManagedInstance<?> managedInstance, boolean lazy) {
		final ManagedSet<E> managedSet = new ManagedSet<E>(managedInstance, (CollectionMapping<?, Set<E>, E>) this.mapping, lazy);
		this.accessor.set(managedInstance.getInstance(), managedSet);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(Object instance, Object reference) {
		final Set<E> set = this.get(instance);

		return (set != null) && set.contains(reference);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object instance, Object value) {
		final HashSet<E> list = ((ManagedSet<E>) this.accessor.get(instance)).getCollection();
		if (!list.contains(value)) {
			list.add((E) value);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setCollection(Object instance, ManagedCollection<?> collection) {
		this.accessor.set(instance, collection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String toString() {
		final String name = this.declaringType != null ? this.declaringType.getName() : "";

		return "SetAttributeImpl [name=" + this.getName() + ", owner=" + this.declaringType.getName() + ", type=" + name + ", mappedBy="
			+ this.mappedBy + "]";
	}
}

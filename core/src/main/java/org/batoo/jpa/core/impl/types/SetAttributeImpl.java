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

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Set;

import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SetAttribute;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
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
	public SetAttributeImpl(ManagedType<X> owner, Member javaMember, Class<Set<E>> javaType) throws MappingException {
		super(owner, javaMember, javaType, (Class<E>) ReflectHelper.getGenericType(javaMember, 0));
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
	public void initialize(ManagedInstance<?> managedInstance, SessionImpl session) {
		final ManagedSet<E> managedSet = this.newInstance0(session, managedInstance, this.get(managedInstance.getInstance()));

		this.set(managedInstance.getInstance(), managedSet);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ManagedSet<E> newInstance(SessionImpl session, ManagedInstance<?> managedInstance) {
		return this.newInstance0(session, managedInstance, null);
	}

	@SuppressWarnings("unchecked")
	private ManagedSet<E> newInstance0(SessionImpl session, ManagedInstance<?> managedInstance, Set<E> existing) {
		return new ManagedSet<E>(session, managedInstance, (CollectionMapping<?, Set<E>, E>) this.mapping, existing);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object instance, Object value) {
		if (value instanceof Set) {
			this.getAccessor().set(instance, (Set<E>) value);
		}
		else {
			final Collection<E> collection = ((ManagedSet<E>) this.getAccessor().get(instance)).getCollection();
			if (!collection.contains(value)) {
				collection.add((E) value);
			}
		}
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

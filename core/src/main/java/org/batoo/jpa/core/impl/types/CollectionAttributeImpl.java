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

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ManagedType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;

/**
 * @author hceylan
 * 
 * @since $version
 */
public final class CollectionAttributeImpl<X, E> extends PluralAttributeImpl<X, Collection<E>, E> implements CollectionAttribute<X, E> {

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
	private CollectionAttributeImpl(EntityTypeImpl<X> declaringType, CollectionAttributeImpl<?, E> original) {
		super(declaringType, original);
	}

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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public CollectionAttributeImpl(ManagedType<X> declaringType, Member javaMember, Class<Collection<E>> javaType) throws MappingException {
		super(declaringType, javaMember, javaType, (Class<E>) ReflectHelper.getGenericType(javaMember, 0));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 * 
	 */
	@Override
	public <T> CollectionAttributeImpl<T, E> clone(EntityTypeImpl<T> declaringType) {
		return new CollectionAttributeImpl<T, E>(declaringType, this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.COLLECTION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void newInstance(ManagedInstance<?> managedInstance, boolean lazy) {
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object instance, Object value) {
		final Collection<E> collection = ((ManagedList<E>) this.accessor.get(instance)).getCollection();
		if (!collection.contains(value)) {
			collection.add((E) value);
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

		return "CollectionAttributeImpl [name=" + this.getName() + ", owner=" + this.declaringType.getName() + ", type=" + name
			+ ", mappedBy=" + this.mappedBy + "]";
	}

}

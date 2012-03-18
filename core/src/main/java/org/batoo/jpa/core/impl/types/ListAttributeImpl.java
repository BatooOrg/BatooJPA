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
import java.util.List;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;

/**
 * The implementation of {@link ListAttribute}
 * 
 * @since $version
 * @author hceylan
 */
public final class ListAttributeImpl<X, E> extends PluralAttributeImpl<X, List<E>, E> implements ListAttribute<X, E> {

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
	public ListAttributeImpl(ManagedType<X> owner, Member javaMember, Class<List<E>> javaType) throws MappingException {
		super(owner, javaMember, javaType, (Class<E>) ReflectHelper.getGenericType(javaMember, 0));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.LIST;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object instance, Object value) {
		this.getAccessor().get(instance).add((E) value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String toString() {
		final String name = this.declaringType != null ? this.declaringType.getName() : "";

		return "ListAttributeImpl [name=" + this.getName() + ", owner=" + name + ", type=" + this.getElementType().getName()
			+ ", mappedBy=" + this.mappedBy + "]";
	}
}

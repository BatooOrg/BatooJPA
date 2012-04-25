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
package org.batoo.jpa.core.impl.mapping;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.batoo.jpa.core.impl.instance.AbstractResolver;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

/**
 * Abstract base class for Mappings.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractMapping<X, T> implements Mapping<X, T> {

	private final AssociationType associationType;
	private final AttributeImpl<X, T> declaringAttribute;
	protected final Deque<AttributeImpl<?, ?>> path;
	private final EntityTypeImpl<?> owner;

	private int h;

	private String strPath;

	/**
	 * Subclasses must call the following as the last call in their constructor.
	 * 
	 * <pre>
	 * this.getOwner().addMapping(this);
	 * </pre>
	 * 
	 * @param associationType
	 *            the type of the association
	 * @param declaringAttribute
	 *            the attribute which declares the mapping
	 * @param path
	 *            the path to the declaringAttribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractMapping(AssociationType associationType, AttributeImpl<X, T> declaringAttribute, Deque<AttributeImpl<?, ?>> path) {
		super();

		this.associationType = associationType;
		this.declaringAttribute = declaringAttribute;

		this.path = new LinkedList<AttributeImpl<?, ?>>(path);
		this.owner = (EntityTypeImpl<?>) this.getPath().getFirst().getDeclaringType();
	}

	/**
	 * Returns a resolver for the mapping.
	 * <p>
	 * Subclasses should return an appropriate resolver their type.
	 * 
	 * @param instance
	 *            the instance
	 * @return a resolver for the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract AbstractResolver createResolver(Object instance);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AbstractMapping<?, ?> other = (AbstractMapping<?, ?>) obj;
		if (this.declaringAttribute == null) {
			if (other.declaringAttribute != null) {
				return false;
			}
		}
		else if (!this.declaringAttribute.equals(other.declaringAttribute)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final AssociationType getAssociationType() {
		return this.associationType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributeImpl<X, T> getDeclaringAttribute() {
		return this.declaringAttribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<T> getJavaType() {
		return this.getDeclaringAttribute().getJavaType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final EntityTypeImpl<?> getOwner() {
		return this.owner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Deque<AttributeImpl<?, ?>> getPath() {
		return this.path;
	}

	/**
	 * Returns the path as a qualified name.
	 * 
	 * @return the path as a qualified name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPathAsString() {
		if (this.strPath == null) {
			this.strPath = TypeFactory.pathAsString(this.path);
		}

		return this.strPath;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final T getValue(Object instance) {
		Object value = instance;
		for (final AttributeImpl<?, ?> attribute : this.path) {
			if (value == null) {
				return null;
			}
			value = attribute.get(value);
		}

		return (T) value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		final int prime = 31;
		final int result = 1;
		this.h = (prime * result) + ((this.declaringAttribute == null) ? 0 : this.declaringAttribute.hashCode());

		return this.h;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final void setValue(Object instance, Object value) {
		final Iterator<AttributeImpl<?, ?>> i = this.path.iterator();
		while (i.hasNext()) {
			final AttributeImpl<?, ?> attribute = i.next();

			if (!i.hasNext()) { // we have reached the destination
				attribute.set(instance, value);
				return;
			}

			Object nextInstance = attribute.get(instance);
			if (nextInstance == null) {
				final EmbeddableTypeImpl<?> type = (EmbeddableTypeImpl<?>) attribute.getMapping().getType();

				nextInstance = type.newInstance();
				attribute.set(instance, nextInstance);
			}

			instance = nextInstance;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [associationType=" + this.associationType + ", path=" + this.getPathAsString()
			+ ", declaringAttribute=" + this.declaringAttribute + "]";
	}

}

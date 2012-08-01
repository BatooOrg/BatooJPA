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
package org.batoo.jpa.core.impl.criteria.path;

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.join.Joinable;

/**
 * The abstract implementation of {@link Path}.
 * 
 * @param <X>
 *            the type of the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractPath<X> extends AbstractExpression<X> implements Path<X> {

	private final ParentPath<?, ?> parent;

	/**
	 * @param parent
	 *            the parent path, may be null
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractPath(ParentPath<?, ?> parent, Class<X> javaType) {
		super(javaType);

		this.parent = parent;
	}

	/**
	 * returns cannot dereference exception.
	 * 
	 * @param name
	 *            the attribute name
	 * @return the exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected IllegalArgumentException cannotDereference(String name) {
		return new IllegalArgumentException("Cannot dereference: " + name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<? super X, K, V> map) {
		throw this.cannotDereference(map.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<? super X, C, E> collection) {
		throw this.cannotDereference(collection.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
		throw this.cannotDereference(attribute.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Path<Y> get(String attributeName) {
		throw this.cannotDereference(attributeName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ParentPath<?, ?> getParentPath() {
		return this.parent;
	}

	/**
	 * Returns the root of the path.
	 * 
	 * @return the root of the path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Joinable getRootPath() {
		AbstractPath<?> root = this;
		while ((root.getParentPath() != null) && !(root instanceof Joinable)) {
			root = root.getParentPath();
		}

		return (Joinable) root;
	}
}

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

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.CollectionExpression;
import org.batoo.jpa.core.impl.criteria.expression.MapExpression;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Maps;

/**
 * Abstract implementation of {@link Path}.
 * 
 * @param <Z>
 *            the type of the parent path
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ParentPath<Z, X> extends AbstractPath<X> implements Path<X> {

	final Map<String, AbstractExpression<?>> children = Maps.newHashMap();

	/**
	 * @param parent
	 *            the parent path, may be null
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParentPath(ParentPath<?, Z> parent, Class<X> javaType) {
		super(parent, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <K, V, M extends Map<K, V>> MapExpression<M, K, V> get(MapAttribute<? super X, K, V> map) {
		return new MapExpression<M, K, V>(this.<Map<K, V>, V> getMapping(map.getName()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <E, C extends Collection<E>> CollectionExpression<C, E> get(PluralAttribute<? super X, C, E> collection) {
		return new CollectionExpression<C, E>(this.<Collection<E>, E> getMapping(collection.getName()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> AbstractPath<Y> get(SingularAttribute<? super X, Y> attribute) {
		final Mapping<? super X, Y, Y> mapping = this.getMapping(attribute.getName());

		switch (attribute.getPersistentAttributeType()) {
			case EMBEDDED:
				return new EmbeddedAttributePath<X, Y>(this, (EmbeddedMapping<? super X, Y>) mapping);
			case BASIC:
				return new BasicPath<Y>(this, (BasicMapping<? super X, Y>) mapping);
			default:
				return new EntityPath<X, Y>(this, attribute.getName(), (EntityTypeImpl<Y>) attribute.getType());
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> AbstractPath<Y> get(String name) {
		// try to resolve from existing children
		final AbstractExpression<Y> path = (AbstractExpression<Y>) this.children.get(name);
		if (path != null) {
			return (AbstractPath<Y>) path;
		}

		final Mapping<? super X, ?, Y> mapping = this.getMapping(name);
		final AttributeImpl<? super X, Y> attribute = (AttributeImpl<? super X, Y>) mapping.getAttribute();

		if (attribute.isCollection()) {
			throw new IllegalArgumentException("Cannot deference a plural attribute as path: " + name);
		}

		return this.get((SingularAttribute<? super X, Y>) attribute);
	}

	/**
	 * Returns the fetch root of the path.
	 * 
	 * @return the fetch root
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract FetchParentImpl<?, X> getFetchRoot();

	/**
	 * Returns the child mapping for the name.
	 * 
	 * @param name
	 *            the name of the child mapping
	 * @param <C>
	 *            the collection type of the child mapping
	 * @param <Y>
	 *            the type of the child mapping
	 * @return the child mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract <C, Y> Mapping<? super X, C, Y> getMapping(String name);
}

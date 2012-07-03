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
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.ParentMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

import com.google.common.collect.Maps;

/**
 * Abstract implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractPath<X> extends AbstractExpression<X> implements Path<X> {

	private final AbstractPath<?> parent;
	final Map<String, AbstractPath<?>> children = Maps.newHashMap();

	/**
	 * @param parent
	 *            the parent path, may be null
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractPath(AbstractPath<?> parent, Class<X> javaType) {
		super(javaType);

		this.parent = parent;
	}

	/**
	 * returns cannot dereference exception.
	 * 
	 * @return the exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected IllegalArgumentException cannotDereference() {
		return new IllegalArgumentException("Cannot dereference");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <K, V, M extends Map<K, V>> AbstractExpression<M> get(MapAttribute<? super X, K, V> map) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <E, C extends Collection<E>> AbstractExpression<C> get(PluralAttribute<? super X, C, E> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> AbstractPath<Y> get(SingularAttribute<? super X, Y> attribute) {
		return this.get(attribute.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> AbstractPath<Y> get(String pathName) {
		// try to resolve from path
		AbstractPath<Y> path = (AbstractPath<Y>) this.children.get(pathName);
		if (path != null) {
			return path;
		}

		if (!(this.getMapping() instanceof ParentMapping)) {
			throw this.cannotDereference();
		}

		final Mapping<? super X, ?, Y> mapping = (Mapping<? super X, ?, Y>) ((ParentMapping<?, X>) this.getMapping()).getChild(pathName);

		if (mapping == null) {
			throw this.cannotDereference();
		}

		// generate and return
		if (mapping instanceof BasicMapping) {
			path = new BasicPath<Y>(this, (BasicMapping<? super X, Y>) mapping);
		}
		else if (mapping instanceof PluralAssociationMapping) {
			path = new PluralAssociationPath<Y>(this, (PluralAssociationMapping<?, ?, Y>) mapping);
		}
		else {
			path = new EmbeddedAttributePath<Y>(this, (EmbeddedMapping<? super X, Y>) mapping);
		}

		this.children.put(pathName, path);

		return path;
	}

	/**
	 * Returns the mapping of the path.
	 * 
	 * @return the mapping of the path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract Mapping<?, ?, X> getMapping();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractPath<?> getParentPath() {
		return this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public AbstractPath<Class<? extends X>> type() {
		return (AbstractPath<Class<? extends X>>) this;
	}
}

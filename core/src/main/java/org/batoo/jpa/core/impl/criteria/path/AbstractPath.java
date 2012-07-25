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
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.ParentMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;

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
	public AbstractPath(ParentPath<?, ?> parent, Class<X> javaType) {
		super(javaType);

		this.parent = (AbstractPath<?>) parent;
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
	public final <Y> AbstractPath<Y> get(String name) {
		// try to resolve from path
		AbstractPath<Y> path = (AbstractPath<Y>) this.children.get(name);
		if (path != null) {
			return path;
		}

		if (!(this instanceof ParentPath)) {
			throw this.cannotDereference(name);
		}

		Mapping<? super X, ?, Y> mapping = null;
		if (this.getMapping() instanceof ParentPath) {
			mapping = (Mapping<? super X, ?, Y>) ((ParentMapping<?, X>) this.getMapping()).getChild(name);
		}
		else {
			if (this instanceof AbstractFrom) {
				mapping = (Mapping<? super X, ?, Y>) ((AbstractFrom<?, X>) this).getEntity().getRootMapping().getChild(name);
			}
			else if (this.getMapping() instanceof AssociationMapping) {
				mapping = (Mapping<? super X, ?, Y>) ((AssociationMapping<?, ?, X>) this.getMapping()).getType().getRootMapping().getChild(name);
			}
		}

		if (mapping == null) {
			throw this.cannotDereference(name);
		}

		// generate and return
		if (mapping instanceof BasicMapping) {
			path = new BasicPath<Y>((ParentPath<?, ?>) this, (BasicMapping<? super X, Y>) mapping);
		}
		else if (mapping instanceof PluralAssociationMapping) {
			path = new PluralAssociationPath<X, Y>((ParentPath<?, X>) this, (PluralAssociationMapping<X, ?, Y>) mapping);
		}
		else if (mapping instanceof SingularAssociationMapping) {
			path = new SingularAssociationPath<X, Y>((ParentPath<?, X>) this, (SingularAssociationMapping<X, Y>) mapping);
		}
		else {
			path = new EmbeddedAttributePath<X, Y>((ParentPath<?, X>) this, (EmbeddedMapping<? super X, Y>) mapping);
		}

		this.children.put(name, path);

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
	public abstract Mapping<?, ?, X> getMapping();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractPath<?> getParentPath() {
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
	protected Joinable getRootPath() {
		AbstractPath<?> root = this;
		while ((root.getParentPath() != null) && !(root instanceof Joinable)) {
			root = root.getParentPath();
		}

		return (Joinable) root;
	}
}

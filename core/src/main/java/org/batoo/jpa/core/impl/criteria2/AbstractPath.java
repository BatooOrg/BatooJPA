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
package org.batoo.jpa.core.impl.criteria2;

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.ParentMapping;

/**
 * The abstract implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractPath<X> extends AbstractExpression<X> implements Path<X> {

	private final Mapping<?, X> mapping;

	/**
	 * @param mapping
	 *            the mapping
	 * @param generatedAlias
	 *            the generated alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractPath(Mapping<?, X> mapping, String generatedAlias) {
		super(mapping.getJavaType(), generatedAlias);

		this.mapping = mapping;
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
	public String generateJpql() {
		final StringBuilder sb = new StringBuilder(StringUtils.isNotBlank(this.getAlias()) ? this.getAlias() : this.mapping.getName());

		AbstractPath<?> parent = this.getParentPath();
		while (parent != null) {
			sb.insert(0, ".");
			sb.insert(0, StringUtils.isNotBlank(parent.getAlias()) ? parent.getAlias() : parent.mapping.getName());
			parent = parent.getParentPath();
		}

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<? super X, K, V> map) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<? super X, C, E> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> AbstractPath<Y> get(String attributeName) {
		if (this.mapping instanceof ParentMapping) {
			final ParentMapping<?, X> mapping = (ParentMapping<?, X>) this.mapping;

			final Mapping<? super X, Y> child = (Mapping<? super X, Y>) mapping.getChild(attributeName);

			if (child instanceof BasicMapping) {
				return new BasicPath<Y>(this, (BasicMapping<?, Y>) child);
			}
		}

		throw this.cannotDereference();
	}

	/**
	 * Returns the mapping of the path.
	 * 
	 * @return the mapping of the path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Mapping<?, X> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract AbstractPath<?> getParentPath();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		// TODO Auto-generated method stub
		return null;
	}
}

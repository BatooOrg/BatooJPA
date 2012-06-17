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
package org.batoo.jpa.core.impl.criteria;

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Maps;

/**
 * 
 * The root implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class RootPathImpl<X> extends PathImpl<X> {

	private final EntityTypeImpl<X> entity;
	private final Map<String, PathImpl<?>> children = Maps.newHashMap();

	/**
	 * @param entity
	 *            the entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RootPathImpl(EntityTypeImpl<X> entity) {
		super(null);

		this.entity = entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String describe() {
		return StringUtils.isNotBlank(this.getAlias()) ? this.getAlias() : this.entity.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> ExpressionImpl<M> get(MapAttribute<? super X, K, V> map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> ExpressionImpl<C> get(PluralAttribute<? super X, C, E> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> PathImpl<Y> get(SingularAttribute<? super X, Y> attribute) {
		// try to resolve from path
		PathImpl<Y> path = (PathImpl<Y>) this.children.get(attribute.getName());
		if (path != null) {
			return path;
		}

		final AbstractMapping<? super X, ?> mapping = this.entity.getMapping(attribute.getName());

		if (mapping == null) {
			throw new IllegalArgumentException("Cannot dereference");
		}

		// generate and return
		if (attribute instanceof BasicAttribute) {
			path = new PhysicalAttributePathImpl<Y>(this, (BasicMapping<? super X, Y>) mapping);
		}
		else {
			path = new EmbeddedAttributePathImpl<Y>(this, (EmbeddedMapping<? super X, Y>) mapping);
		}

		this.children.put(attribute.getName(), path);

		return path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> PathImpl<Y> get(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}

	/**
	 * Returns the alias for the table.
	 * <p>
	 * if table does not have an alias, it is generated.
	 * 
	 * @param query
	 *            the query
	 * @param table
	 *            the table
	 * @return the alias for the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract String getTableAlias(CriteriaQueryImpl<?> query, EntityTable table);
}

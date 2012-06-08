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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.model.attribute.PhysicalAttributeImpl;

/**
 * Physical Attribute implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalAttributePathImpl<X> extends AbstractPathImpl<X> {

	private final PhysicalAttributeImpl<?, X> attribute;

	/**
	 * @param parent
	 *            the parent path
	 * @param attribute
	 *            the physical attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalAttributePathImpl(AbstractPathImpl<?> parent, PhysicalAttributeImpl<?, X> attribute) {
		super(parent);

		this.attribute = attribute;
	}

	private IllegalArgumentException cannotDereference() {
		return new IllegalArgumentException("Cannot dereference");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		final BasicColumn column = this.getModel().getColumn();

		AbstractPathImpl<?> root = this;
		while (root.getParentPath() != null) {
			root = root.getParentPath();
		}

		final String tableAlias = ((AbstractFromImpl<X, X>) root).getTableAlias(query, column.getTable());

		return tableAlias + "." + column.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map) {
		throw this.cannotDereference();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection) {
		throw this.cannotDereference();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
		throw this.cannotDereference();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Path<Y> get(String attributeName) {
		throw this.cannotDereference();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PhysicalAttributeImpl<?, X> getModel() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (this.getParentPath() != null) {
			builder.append(this.getParentPath());
		}
		else {
			builder.append(this.attribute.getDeclaringType().getJavaType().getSimpleName());
		}

		builder.append(".").append(this.attribute.getName());

		return builder.toString();
	}
}

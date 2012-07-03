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
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.CompoundExpressionImpl.Comparison;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;

/**
 * Physical Attribute implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public class PhysicalAttributePathImpl<X> extends PathImpl<X> {

	private final BasicMapping<?, X> mapping;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the physical mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalAttributePathImpl(PathImpl<?> parent, BasicMapping<?, X> mapping) {
		super(parent);

		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String describe() {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().describe());

		builder.append(".").append(this.mapping.getAttribute().getName());

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		final BasicColumn column = this.mapping.getColumn();

		PathImpl<?> root = this;
		while (root.getParentPath() != null) {
			root = root.getParentPath();
		}

		final String tableAlias = ((RootPathImpl<X>) root).getTableAlias(query, column.getTable());

		return tableAlias + "." + column.getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter) {
		// expand the mapping
		final String sql = this.generate(query) + comparison.getFragment() + parameter.generate(query);

		// seal the parameter count
		parameter.registerParameter(query, this.mapping);

		return sql;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<? super X, K, V> map) {
		throw this.cannotDereference();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<? super X, C, E> collection) {
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
	public BasicAttribute<?, X> getModel() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<X> handle(SessionImpl session, List<Map<String, Object>> data, MutableInt rowNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.describe();
	}
}

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Path;

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.CompoundExpression.Comparison;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Physical Attribute implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttributePath<X> extends AbstractPath<X> {

	private final EmbeddedMapping<?, X> mapping;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the embedded mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttributePath(AbstractPath<?> parent, EmbeddedMapping<?, X> mapping) {
		super(parent, mapping.getType().getJavaType());

		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter) {
		final List<String> fragments = Lists.newArrayList();

		// expand the mappings
		this.generate(query, comparison, parameter, this.mapping, fragments);

		// seal the parameter count
		parameter.registerParameter(query, this.mapping);

		// return the joined restriction
		return Joiner.on(" AND ").join(fragments);
	}

	@SuppressWarnings({ "rawtypes" })
	private void generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter, EmbeddedMapping<?, ?> mapping,
		final List<String> fragments) {
		for (final Mapping<?, ?, ?> child : mapping.getChildren()) {
			// handle basic mapping
			if (child instanceof BasicMapping) {
				final BasicColumn column = ((BasicMapping) child).getColumn();
				AbstractPath<?> root = this;
				while (root.getParentPath() != null) {
					root = root.getParentPath();
				}

				final String tableAlias = this.getRootPath().getTableAlias(query, column.getTable());
				fragments.add(tableAlias + "." + column.getName() + comparison.getFragment() + parameter.generateSqlSelect(query));
			}
			// further expand the embedded mappings
			else if (child instanceof EmbeddedMapping) {
				this.generate(query, comparison, parameter, (EmbeddedMapping<?, ?>) child, fragments);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction() {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlRestriction());

		builder.append(".").append(this.mapping.getAttribute().getName());

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "rawtypes" })
	public String generateSqlSelect(CriteriaQueryImpl<?> query) {
		final List<String> fragments = Lists.newArrayList();

		for (final Mapping<? super X, ?, ?> mapping : this.mapping.getChildren()) {
			if (mapping instanceof BasicMapping) {
				final BasicColumn column = ((BasicMapping) mapping).getColumn();
				AbstractPath<?> root = this;
				while (root.getParentPath() != null) {
					root = root.getParentPath();
				}

				final String tableAlias = this.getRootPath().getTableAlias(query, column.getTable());
				fragments.add(tableAlias + "." + column.getName());
			}
		}

		return Joiner.on(", ").join(fragments);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddedMapping<?, X> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddedAttribute<?, X> getModel() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X handle(SessionImpl session, Map<String, Object> row, HashMap<ManagedInstance<?>, ManagedInstance<?>> instances) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.generateJpqlRestriction();
	}
}

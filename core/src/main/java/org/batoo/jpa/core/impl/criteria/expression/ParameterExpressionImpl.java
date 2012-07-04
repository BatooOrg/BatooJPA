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
package org.batoo.jpa.core.impl.criteria.expression;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.ParameterExpression;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.CompoundExpression.Comparison;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;

import com.google.common.collect.Maps;

/**
 * Type of criteria query parameter expressions.
 * 
 * @param <T>
 *            the type of the parameter expression
 * @author hceylan
 * @since $version
 */
public class ParameterExpressionImpl<T> extends AbstractExpression<T> implements ParameterExpression<T> {

	private final String name;
	private Integer position;
	private int expandedCount = 0;
	private final Map<Integer, Mapping<?, ?, ?>> mappingMap = Maps.newHashMap();

	/**
	 * @param paramClass
	 *            the class of the parameter
	 * @param name
	 *            the name of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl(Class<T> paramClass, String name) {
		super(paramClass);

		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction() {
		final StringBuilder builder = new StringBuilder();

		if (StringUtils.isNotBlank(this.name)) {
			return builder.append(":").append(this.name).toString();
		}

		if (this.position != null) {
			return builder.append("?").append(this.position).toString();
		}

		return builder.append("?").toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect() {
		return "?";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query) {
		if (this.position == null) {
			query.addParameter(this);
		}

		if (this.mappingMap.isEmpty()) {
			this.expandedCount++;
		}

		return "?";
	}

	/**
	 * Returns the expanded parameter count of the parameter.
	 * 
	 * @return the expanded parameter count of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getExpandedCount() {
		return this.expandedCount;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<T> getParameterType() {
		return (Class<T>) this.getJavaType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getPosition() {
		return this.position;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<T> handle(SessionImpl session, List<Map<String, Object>> data, MutableInt rowNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Registers the parameter with the query.
	 * 
	 * @param query
	 *            the query
	 * @param mapping
	 *            the mapping to bind to
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void registerParameter(CriteriaQueryImpl<?> query, Mapping<?, ?, ?> mapping) {
		this.mappingMap.put(query.setNextSqlParam(this), mapping);
	}

	/**
	 * Sets the parameters by expanding the embedded mapping.
	 * 
	 * @param parameters
	 *            the SQL parameters
	 * @param paramIndex
	 *            the index corresponding to the parameter
	 * @param mapping
	 *            the embedded mapping
	 * @param value
	 *            the value to set to the parameter
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void setParameter(Object[] parameters, MutableInt sqlParamindex, EmbeddedMapping<?, ?> mapping, Object value) {
		for (final Mapping<?, ?, ?> child : mapping.getChildren()) {
			if (child instanceof BasicMapping) {
				parameters[sqlParamindex.intValue()] = child.getAttribute().get(value);

				sqlParamindex.increment();
			}
			else if (child instanceof EmbeddedMapping) {
				this.setParameter(parameters, sqlParamindex, (EmbeddedMapping<?, ?>) child, mapping.getAttribute().get(value));
			}
		}
	}

	/**
	 * Sets the parameters expanding if necessary.
	 * 
	 * @param parameters
	 *            the SQL parameters
	 * @param paramIndex
	 *            the index corresponding to the parameter
	 * @param sqlParamindex
	 *            the index corresponding to expanded SQL parameter
	 * @param value
	 *            the value to set to the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setParameter(Object[] parameters, MutableInt paramIndex, MutableInt sqlParamindex, Object value) {
		final Mapping<?, ?, ?> mapping = this.mappingMap.get(paramIndex.intValue());

		if (mapping instanceof BasicMapping) {
			parameters[sqlParamindex.intValue()] = value;

			sqlParamindex.increment();
		}
		else if (mapping instanceof EmbeddedMapping) {
			this.setParameter(parameters, sqlParamindex, (EmbeddedMapping<?, ?>) mapping, value);
		}

		paramIndex.increment();
	}

	/**
	 * Sets the position of the parameter
	 * 
	 * @param position
	 *            the position
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPosition(int position) {
		this.position = position;
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

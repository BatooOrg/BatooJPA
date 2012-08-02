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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Selection;

import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Commons super class for criteria queries.
 * 
 * @param <T>
 *            the type of the query
 * 
 * @author hceylan
 * @since $version
 */
public abstract class BaseQueryImpl<T> implements BaseQuery<T> {

	private final MetamodelImpl metamodel;

	private int nextEntityAlias;
	private int nextSelection;
	private int nextparam;

	private final HashMap<Selection<?>, String> selections = Maps.newHashMap();
	private final HashBiMap<ParameterExpressionImpl<?>, Integer> parameters = HashBiMap.create();
	private final HashMap<String, List<AbstractColumn>> fields = Maps.newHashMap();

	private String sql;
	private String jpql;

	private final List<ParameterExpressionImpl<?>> sqlParameters = Lists.newArrayList();

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BaseQueryImpl(MetamodelImpl metamodel) {
		this.metamodel = metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateTableAlias(boolean entity) {
		return "E" + (!entity ? "C" : "") + this.nextEntityAlias++;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias(AbstractSelection<?> selection) {
		String alias = this.selections.get(selection);
		if (alias == null) {
			alias = "S" + this.nextSelection++;
			this.selections.put(selection, alias);
		}

		return alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getAlias(ParameterExpressionImpl<?> parameter) {
		Integer alias = this.parameters.get(parameter);
		if (alias == null) {
			alias = this.nextparam++;
			this.parameters.put(parameter, alias);
		}

		return alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getFieldAlias(String tableAlias, AbstractColumn column) {
		List<AbstractColumn> fields = this.fields.get(tableAlias);
		if (fields == null) {
			fields = Lists.newArrayList();
			this.fields.put(tableAlias, fields);
		}

		final int i = fields.indexOf(column);
		if (i >= 0) {
			return Integer.toString(i);
		}

		fields.add(column);
		return Integer.toString(fields.size() - 1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getJpql() {
		if (this.jpql != null) {
			return this.jpql;
		}

		synchronized (this) {
			if (this.jpql != null) {
				return this.jpql;
			}

			return this.jpql = this.generateJpql();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ParameterExpressionImpl<?> getParameter(int position) {
		return this.parameters.inverse().get(position);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ParameterExpression<?>> getParameters() {
		final Set<ParameterExpression<?>> parameters = Sets.newHashSet();
		parameters.addAll(this.parameters.keySet());

		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSql() {
		if (this.sql != null) {
			return this.sql;
		}

		synchronized (this) {
			if (this.sql != null) {
				return this.sql;
			}

			return this.sql = this.generateSql();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ParameterExpressionImpl<?>> getSqlParameters() {
		return this.sqlParameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int setNextSqlParam(ParameterExpressionImpl<?> parameter) {
		this.sqlParameters.add(parameter);

		return this.sqlParameters.size() - 1;
	}
}

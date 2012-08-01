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
public abstract class BaseQuery<T> {

	private int nextEntityAlias;
	private int nextSelection;
	private int nextparam;

	private final HashMap<Selection<?>, String> selections = Maps.newHashMap();
	private final HashBiMap<ParameterExpressionImpl<?>, Integer> parameters = HashBiMap.create();
	private final HashMap<String, List<AbstractColumn>> fields = Maps.newHashMap();

	/**
	 * Returns the generated entity alias.
	 * 
	 * @param entity
	 *            true if the table is an entity table, false for element collections
	 * @return the generated entity alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateTableAlias(boolean entity) {
		return "E" + (!entity ? "C" : "") + this.nextEntityAlias++;
	}

	/**
	 * Returns the generated alias for the selection.
	 * 
	 * @param selection
	 *            the selection
	 * @return the alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getAlias(AbstractSelection<?> selection) {
		String alias = this.selections.get(selection);
		if (alias == null) {
			alias = "S" + this.nextSelection++;
			this.selections.put(selection, alias);
		}

		return alias;
	}

	/**
	 * Returns the generated alias for the parameter.
	 * 
	 * @param parameter
	 *            the parameter
	 * @return the alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getAlias(ParameterExpressionImpl<?> parameter) {
		Integer alias = this.parameters.get(parameter);
		if (alias == null) {
			alias = this.nextparam++;
			this.parameters.put(parameter, alias);
		}

		return alias;
	}

	/**
	 * @param tableAlias
	 *            the alias of the table
	 * @param column
	 *            the column
	 * @return the field alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
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
	 * Returns the parameter at position.
	 * 
	 * @param position
	 *            the position
	 * @return the parameter at position
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl<?> getParameter(int position) {
		return this.parameters.inverse().get(position);
	}

	/**
	 * Returns the parameters of the query. Returns empty set if there are no parameters.
	 * <p>
	 * Modifications to the set do not affect the query.
	 * 
	 * @return the query parameters
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<ParameterExpression<?>> getParameters() {
		final Set<ParameterExpression<?>> parameters = Sets.newHashSet();
		parameters.addAll(this.parameters.keySet());

		return parameters;
	}
}

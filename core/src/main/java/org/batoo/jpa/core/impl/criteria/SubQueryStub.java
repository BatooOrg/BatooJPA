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

import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * 
 * @param <T>
 *            the type of the sub query.
 * 
 * @author hceylan
 * @since $version
 */
public class SubQueryStub<T> extends AbstractCriteriaQueryImpl<T> {

	private final BaseQueryImpl<?> parent;

	/**
	 * @param parent
	 *            the parent query
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SubQueryStub(BaseQueryImpl<?> parent, MetamodelImpl metamodel, Class<T> resultType) {
		super(metamodel, resultType);

		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateTableAlias(boolean entity) {
		return this.parent.generateTableAlias(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias(AbstractSelection<?> selection) {
		return this.parent.getAlias(selection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getAlias(ParameterExpressionImpl<?> parameter) {
		return this.parent.getAlias(parameter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getFieldAlias(String tableAlias, AbstractColumn column) {
		return this.parent.getFieldAlias(tableAlias, column);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getJpql();
	}
}

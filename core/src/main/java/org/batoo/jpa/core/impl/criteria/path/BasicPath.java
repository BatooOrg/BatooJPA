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

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.PathTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
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
public class BasicPath<X> extends AbstractPath<X> {

	private final BasicMapping<?, X> mapping;
	private String fieldAlias;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the physical mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicPath(ParentPath<?, ?> parent, BasicMapping<?, X> mapping) {
		super(parent, mapping.getJavaType());

		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlRestriction(query));

		builder.append(".").append(this.mapping.getAttribute().getName());

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean seleselectedcted) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlSelect(query, false));

		builder.append(".").append(this.mapping.getAttribute().getName());
		if (StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		final BasicColumn column = this.mapping.getColumn();

		final Joinable rootPath = this.getRootPath();
		final String tableAlias = rootPath.getTableAlias(query, column.getTable());

		this.fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);

		if (selected) {
			return tableAlias + "." + column.getName() + " AS " + this.fieldAlias;
		}
		else {
			return tableAlias + "." + column.getName();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicMapping<?, X> getMapping() {
		return this.mapping;
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
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		final BasicColumn column = this.mapping.getColumn();

		final String tableAlias = this.getRootPath().getTableAlias(query, column.getTable());

		return new String[] { tableAlias + "." + column.getName() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final X value = (X) this.mapping.getColumn().convertValue(row.getObject(this.fieldAlias));

		return (X) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		return new PathTypeExpression<Class<? extends X>>(this);
	}
}

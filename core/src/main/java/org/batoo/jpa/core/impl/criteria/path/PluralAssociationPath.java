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

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.EntityTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.join.FetchImpl;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

/**
 * Physical Attribute implementation of {@link Path}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public class PluralAssociationPath<Z, X> extends AbstractPath<X> implements Joinable, ParentPath<Z, X> {

	private final PluralAssociationMapping<Z, ?, X> mapping;
	private final FetchImpl<Z, X> fetchRoot;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the physical mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PluralAssociationPath(ParentPath<?, Z> parent, PluralAssociationMapping<Z, ?, X> mapping) {
		super(parent, mapping.getType().getJavaType());

		this.mapping = mapping;
		this.fetchRoot = parent.getFetchRoot().join(mapping.getAttribute().getName(), JoinType.LEFT);
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
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder();

		if ((this.getParentPath() instanceof AbstractFrom) && StringUtils.isNotBlank(this.getParentPath().getAlias())) {
			builder.append(this.getParentPath().getAlias());
		}
		else {
			builder.append(this.getParentPath().generateJpqlSelect(null, false));
		}

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
		return this.fetchRoot.generateSqlSelect(query, selected, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, X> getFetchRoot() {
		return this.fetchRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAssociationMapping<?, ?, X> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAttributeImpl<? super Z, ?, X> getModel() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		return this.fetchRoot.getSqlRestrictionFragments(query, MapSelectType.VALUE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableAlias(CriteriaQueryImpl<?> query, AbstractTable table) {
		return this.fetchRoot.getTableAlias(query, table);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final X value = this.fetchRoot.handle(session, row);

		return (X) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeExpression<Class<? extends X>> type() {
		return new EntityTypeExpression<Class<? extends X>>(this);
	}
}

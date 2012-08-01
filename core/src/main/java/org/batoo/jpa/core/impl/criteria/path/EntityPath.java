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
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.AbstractTypeExpression;
import org.batoo.jpa.core.impl.criteria.expression.EntityTypeExpression;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.join.FetchImpl;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Entity type attribute implementation of {@link Path}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public class EntityPath<Z, X> extends ParentPath<Z, X> implements Joinable {

	private final FetchImpl<Z, X> fetchRoot;
	private final String pathName;
	private final EntityTypeImpl<X> entity;

	/**
	 * @param parent
	 *            the parent path
	 * @param pathName
	 *            the path name
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityPath(ParentPath<?, Z> parent, String pathName, EntityTypeImpl<X> entity) {
		super(parent, entity.getJavaType());

		this.pathName = pathName;
		this.entity = entity;

		this.fetchRoot = parent.getFetchRoot().join(pathName, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractCriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlRestriction(query));

		builder.append(".").append(this.pathName);

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder();

		if ((this.getParentPath() instanceof AbstractFrom) && StringUtils.isNotBlank(this.getParentPath().getAlias())) {
			builder.append(this.getParentPath().getAlias());
		}
		else {
			builder.append(this.getParentPath().generateJpqlSelect(null, false));
		}

		builder.append(".").append(this.pathName);
		if (selected && StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.fetchRoot.generateSqlSelect(query, selected, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<?> getEntity() {
		return this.entity;
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
	@SuppressWarnings("unchecked")
	protected <C, Y> Mapping<? super X, C, Y> getMapping(String name) {
		final Mapping<? super X, C, Y> mapping = (Mapping<? super X, C, Y>) this.entity.getRootMapping().getChild(name);

		if (mapping == null) {
			throw this.cannotDereference(name);
		}

		return mapping;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractCriteriaQueryImpl<?> query) {
		return this.fetchRoot.getSqlRestrictionFragments(query, MapSelectType.VALUE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableAlias(AbstractCriteriaQueryImpl<?> query, AbstractTable table) {
		return this.getFetchRoot().getTableAlias(query, table);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.fetchRoot.handle(session, row);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractTypeExpression<X> type() {
		if (this.entity.getRootType().getInheritanceType() != null) {
			return new EntityTypeExpression<X>(this, this.entity.getRootType().getDiscriminatorColumn());
		}

		return new StaticTypeExpression<X>(this, this.getModel().getBindableJavaType());
	}
}

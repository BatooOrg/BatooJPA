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
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.CompoundExpression.Comparison;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.join.FetchImpl;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.ParentMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * The root implementation of {@link Path}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class EntityPath<Z, X> extends AbstractPath<X> implements Joinable, ParentPath<Z, X> {

	final EntityTypeImpl<X> entity;
	private final FetchParentImpl<Z, X> fetchRoot;

	/**
	 * Constructor for root paths.
	 * 
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityPath(EntityTypeImpl<X> entity) {
		super(null, entity.getJavaType());

		this.entity = entity;

		this.fetchRoot = new FetchParentImpl<Z, X>(entity);
	}

	/**
	 * Constructor for child paths.
	 * 
	 * @param parent
	 *            the parent path
	 * @param entity
	 *            the entity type
	 * @param mapping
	 *            the mapping
	 * @param joinType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityPath(ParentPath<?, Z> parent, EntityTypeImpl<X> entity, AssociationMapping<? super Z, ?, X> mapping, JoinType joinType) {
		super(parent, entity.getJavaType());

		this.entity = entity;

		if (parent != null) {
			this.fetchRoot = new FetchImpl<Z, X>(parent.getFetchRoot(), mapping, joinType);
		}
		else {
			this.fetchRoot = new FetchParentImpl<Z, X>(entity);
		}
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
	 * Returns the JPQL fetch joins fragment.
	 * 
	 * @return the JPQL fetch joins fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateJpqlFetches() {
		final String root = StringUtils.isNotBlank(this.getAlias()) ? this.getAlias() : this.entity.getName();

		return this.fetchRoot.generateJpqlFetches(root);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction() {
		return StringUtils.isNotBlank(this.getAlias()) ? this.getAlias() : this.entity.getName();
	}

	/**
	 * Generates SQL joins fragment.
	 * 
	 * @param query
	 *            the query
	 * @param joins
	 *            the map of joins
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void generateSqlJoins(CriteriaQueryImpl<?> query, Map<Joinable, String> joins) {
		final List<String> selfJoins = Lists.newArrayList();

		this.fetchRoot.generateSqlJoins(query, selfJoins);

		if (selfJoins.size() > 0) {
			joins.put(this, Joiner.on("\n").join(selfJoins));
		}
		else {
			joins.put(this, null);
		}

		for (final Fetch<X, ?> fetch : this.fetchRoot.getFetches()) {
			((FetchParentImpl<Z, X>) fetch).generateSqlJoins(query, joins);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query) {
		return this.fetchRoot.generateSqlSelect(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<Z, X> getFetchRoot() {
		return this.fetchRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected ParentMapping<X, X> getMapping() {
		return this.entity.getRootMapping();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X handle(SessionImpl session, ResultSet row) throws SQLException {
		return this.fetchRoot.handle(session, row);
	}
}

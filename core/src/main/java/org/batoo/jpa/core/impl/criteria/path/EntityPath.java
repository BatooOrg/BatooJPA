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

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.Joinable;
import org.batoo.jpa.core.impl.model.mapping.RootMapping;
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
public abstract class EntityPath<Z, X> extends AbstractPath<X> implements Joinable {

	final EntityTypeImpl<X> entity;
	private final FetchParentImpl<Z, X> fetchRoot;

	/**
	 * @param parent
	 *            the parent
	 * @param entity
	 *            the entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityPath(AbstractPath<?> parent, EntityTypeImpl<X> entity) {
		super(parent, entity.getJavaType());

		this.entity = entity;

		this.fetchRoot = new FetchParentImpl<Z, X>(entity);
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
		final String root = StringUtils.isNotBlank(this.getAlias()) ? this.getAlias() : this.getModel().getName();

		return this.getFetchRoot().generateJpqlFetches(root);
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

		for (final Fetch<X, ?> fetch : this.getFetchRoot().getFetches()) {
			((FetchParentImpl<Z, X>) fetch).generateSqlJoins(query, joins);
		}
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
	@Override
	public void generateSqlJoinsUp(CriteriaQueryImpl<?> query, Map<Joinable, String> joins) {
		this.getFetchRoot().generateSqlJoins(query, joins);
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
	 * Returns the fetch root of the entity path.
	 * 
	 * @return the fetch root of the entity path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected FetchParentImpl<Z, X> getFetchRoot() {
		return this.fetchRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected RootMapping<X> getMapping() {
		return this.entity.getRootMapping();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}
}

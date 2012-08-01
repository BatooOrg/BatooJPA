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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.path.ParentPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.Mapping;

/**
 * 
 * @param <C>
 *            the type of the collection
 * @param <E>
 *            the type of the element
 * 
 * @author hceylan
 * @since $version
 */
public class CollectionExpression<C extends Collection<E>, E> extends AbstractExpression<C> {

	private final ParentPath<?, ?> parentPath;
	private final Mapping<?, Collection<E>, E> mapping;

	/**
	 * @param parentPath
	 *            the parent path
	 * @param mapping
	 *            the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public CollectionExpression(ParentPath<?, ?> parentPath, Mapping<?, Collection<E>, E> mapping) {
		super((Class<C>) mapping.getJavaType());

		this.parentPath = parentPath;
		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(AbstractQueryImpl<?> query) {
		return this.parentPath.generateJpqlRestriction(query) + "." + this.mapping.getAttribute().getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection paths cannot be selected");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection paths cannot be selected");
	}

	/**
	 * Returns the mapping of the CollectionExpression.
	 * 
	 * @return the mapping of the CollectionExpression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Mapping<?, Collection<E>, E> getMapping() {
		return this.mapping;
	}

	/**
	 * Returns the parentPath of the CollectionExpression.
	 * 
	 * @return the parentPath of the CollectionExpression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParentPath<?, ?> getParentPath() {
		return this.parentPath;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(AbstractQueryImpl<?> query) {
		throw new IllegalArgumentException("Collection paths cannot be restricted");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public C handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "CollectionExpression [parentPath=" + this.parentPath + ", mapping=" + this.mapping + "]";
	}
}

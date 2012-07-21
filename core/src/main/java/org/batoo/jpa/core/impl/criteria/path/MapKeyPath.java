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

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.Mapping;

/**
 * Path for Map join keys.
 * 
 * @param <X>
 *            the type of the key
 * 
 * @author hceylan
 * @since $version
 */
public class MapKeyPath<X> extends AbstractPath<X> {

	private final MapJoinImpl<?, X, ?> mapJoin;

	/**
	 * @param mapJoin
	 *            the map parent
	 * @param javaType
	 *            the java type of the key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MapKeyPath(MapJoinImpl<?, X, ?> mapJoin, Class<X> javaType) {
		super(mapJoin, javaType);

		this.mapJoin = mapJoin;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		return this.mapJoin.generateJpqlRestriction(query) + ".key";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query) {
		return this.mapJoin.generateJpqlSelect(null) + ".key";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query) {
		return this.mapJoin.generateSqlSelect(query, MapSelectType.KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, X> getMapping() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public MapAttributeImpl<?, ?, X> getModel() {
		return (MapAttributeImpl<?, ?, X>) this.mapJoin.getModel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		return this.mapJoin.getSqlRestrictionFragments(query, MapSelectType.KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (X) this.mapJoin.handle(session, row, MapSelectType.KEY);
	}
}

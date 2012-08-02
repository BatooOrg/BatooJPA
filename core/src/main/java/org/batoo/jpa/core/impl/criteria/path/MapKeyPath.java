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
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.ElementMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;

/**
 * Path for Map join keys.
 * 
 * @param <Z>
 *            the type of the parent path
 * @param <X>
 *            the type of the key
 * 
 * @author hceylan
 * @since $version
 */
public class MapKeyPath<Z, X> extends ParentPath<Z, X> {

	private final MapJoinImpl<?, X, ?> mapJoin;
	private ElementMapping<X> elementMapping;

	/**
	 * @param mapJoin
	 *            the map parent
	 * @param javaType
	 *            the java type of the key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MapKeyPath(MapJoinImpl<Z, X, ?> mapJoin, Class<X> javaType) {
		super(mapJoin.getParent(), javaType);

		this.mapJoin = mapJoin;
		if (this.mapJoin.getModel().getKeyType().getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.elementMapping = new ElementMapping<X>(null, (EmbeddableTypeImpl<X>) this.mapJoin.getModel().getKeyType());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return "key(" + this.mapJoin.generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.mapJoin.generateSqlSelect(query, selected, MapSelectType.KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, X> getFetchRoot() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <C, Y> Mapping<? super X, C, Y> getMapping(String name) {
		if (this.mapJoin.getModel().getKeyType() instanceof EmbeddableTypeImpl) {
			final Mapping<? super X, ?, ?> child = this.elementMapping.getChild(name);

			if (child != null) {
				return (Mapping<? super X, C, Y>) child;
			}
		}

		throw this.cannotDereference(name);
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
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return this.mapJoin.getSqlRestrictionFragments(query, MapSelectType.KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final X value = (X) this.mapJoin.handle(session, row, MapSelectType.KEY);

		return (X) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		return new StaticTypeExpression<X>(this, this.mapJoin.getModel().getKeyJavaType());
	}
}

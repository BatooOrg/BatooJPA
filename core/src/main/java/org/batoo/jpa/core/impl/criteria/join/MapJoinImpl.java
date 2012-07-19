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
package org.batoo.jpa.core.impl.criteria.join;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.criteria.expression.CompoundExpression.Comparison;
import org.batoo.jpa.core.impl.criteria.expression.MapEntryExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.path.MapKeyPath;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.util.Pair;

/**
 * Implementation of {@link ListJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * 
 * @author hceylan
 * @since $version
 */
public class MapJoinImpl<Z, K, V> extends AbstractJoin<Z, V> implements MapJoin<Z, K, V> {

	/**
	 * The select type for the map join
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum MapSelectType {
		/**
		 * Select value (default)
		 */
		VALUE,

		/**
		 * Select key
		 */
		KEY,

		/**
		 * Select entry
		 */
		ENTRY
	}

	/**
	 * @param parent
	 *            the parent
	 * @param mapping
	 *            the mapping
	 * @param jointType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public MapJoinImpl(AbstractFrom<?, Z> parent, PluralMapping<? super Z, Map<K, V>, V> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Expression<Entry<K, V>> entry() {
		return new MapEntryExpression(this, Entry.class);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query) {
		return this.generateSqlSelect(query, MapSelectType.VALUE);
	}

	/**
	 * Returns the SQL select fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selectType
	 *            the select type
	 * @return the SQL select fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateSqlSelect(CriteriaQueryImpl<?> query, MapSelectType selectType) {
		return this.getFetchRoot().generateSqlSelect(query, this.getParentPath() == null, selectType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public MapAttributeImpl<? super Z, K, V> getModel() {
		return (MapAttributeImpl<? super Z, K, V>) this.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public V handle(SessionImpl session, ResultSet row) throws SQLException {
		return (V) this.handle(session, row, MapSelectType.VALUE);
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the row
	 * @param selectType
	 *            the map select type
	 * @return the managed instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * @param <R>
	 *            the type of the return either K or V
	 * 
	 * @since $version
	 * @author hceylan
	 * @param <R>
	 */
	@SuppressWarnings("unchecked")
	public Object handle(SessionImpl session, ResultSet row, MapSelectType selectType) throws SQLException {
		if (this.getEntity() != null) {
			final EntryImpl<Object, ManagedInstance<?>> result = this.getFetchRoot().handle(session, row, selectType);
			switch (selectType) {
				case KEY:
					return result.getKey();
				case VALUE:
					return result.getValue().getInstance();
				default:
					return new Pair<K, V>((K) result.getKey(), (V) result.getValue().getInstance());
			}
		}

		final EntryImpl<Object, V> result = this.getFetchRoot().handleElementFetch(row, selectType);

		switch (selectType) {
			case KEY:
				return result.getKey();
			case VALUE:
				return result.getValue();
			default:
				return result;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Path<K> key() {
		return new MapKeyPath<K>(this, this.getModel().getKeyJavaType());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MapJoin<Z, K, V> on(Expression<Boolean> restriction) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MapJoin<Z, K, V> on(Predicate... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Path<V> value() {
		return this;
	}
}

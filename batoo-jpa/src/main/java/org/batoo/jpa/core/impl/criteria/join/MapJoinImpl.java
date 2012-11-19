/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
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

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.MapEntryExpression;
import org.batoo.jpa.core.impl.criteria.path.MapKeyPath;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;
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
 * @since 2.0.0
 */
public class MapJoinImpl<Z, K, V> extends AbstractPluralJoin<Z, Map<K, V>, V> implements MapJoin<Z, K, V> {

	/**
	 * The select type for the map join
	 * 
	 * @author hceylan
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public MapJoinImpl(AbstractFrom<?, Z> parent, PluralMappingEx<? super Z, Map<K, V>, V> mapping, JoinType jointType) {
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
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.generateSqlSelect(query, selected, MapSelectType.VALUE);
	}

	/**
	 * Returns the SQL select fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selected
	 *            id the join is selected
	 * @param selectType
	 *            the select type
	 * @return the SQL select fragment
	 * 
	 * @since 2.0.0
	 */
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected, MapSelectType selectType) {
		this.select(selected);

		return this.getFetchRoot().generateSqlSelect(query, selected, this.getParentPath() == null, selectType);
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
	public V handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
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
	 * 
	 * @since 2.0.0
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
		return new MapKeyPath<Z, K>(this, this.getModel().getKeyJavaType());
	}

	/**
	 * Create an expression corresponding to the type of the path.
	 * 
	 * @param selectType
	 *            the select type
	 * @return expression corresponding to the type of the path
	 * 
	 * @since 2.0.0
	 */
	public Expression<Class<?>> type(MapSelectType selectType) {
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

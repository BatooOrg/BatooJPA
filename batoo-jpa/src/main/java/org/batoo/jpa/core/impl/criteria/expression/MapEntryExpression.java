/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for map join entries.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MapEntryExpression<K, V> extends AbstractExpression<Entry<K, V>> {

	private final MapJoinImpl<?, K, V> mapJoin;

	/**
	 * @param mapJoin
	 *            the map join
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	public MapEntryExpression(MapJoinImpl<?, K, V> mapJoin, Class<Entry<K, V>> javaType) {
		super(javaType);

		this.mapJoin = mapJoin;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.mapJoin.generateJpqlRestriction(query) + ".entry";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.mapJoin.generateJpqlSelect(null, selected) + ".entry";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.mapJoin.generateSqlSelect(query, selected, MapSelectType.ENTRY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return this.mapJoin.getSqlRestrictionFragments(query, MapSelectType.ENTRY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EntryImpl<K, V> handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (EntryImpl<K, V>) this.mapJoin.handle(session, row, MapSelectType.ENTRY);
	}
}

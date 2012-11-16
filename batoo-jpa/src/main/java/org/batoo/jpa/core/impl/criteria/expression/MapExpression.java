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
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.Mapping;

/**
 * 
 * @param <M>
 *            the Map
 * @param <K>
 *            the key type
 * @param <V>
 *            the valur type
 * 
 * @author hceylan
 * @since $version
 */
public class MapExpression<M extends Map<K, V>, K, V> extends AbstractExpression<M> {

	/**
	 * @param mapping
	 *            the mapping
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public MapExpression(Mapping<?, Map<K, V>, V> mapping) {
		super((Class<M>) mapping.getJavaType());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public M handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the xpression
	 * 
	 * @since $version
	 */
	public CollectionExpression<Collection<V>, V> values() {
		// TODO Auto-generated method stub
		return null;
	}
}

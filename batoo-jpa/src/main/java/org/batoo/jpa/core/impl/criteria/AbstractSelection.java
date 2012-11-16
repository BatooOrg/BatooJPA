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
package org.batoo.jpa.core.impl.criteria;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.criteria.Selection;

import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The definition of an item that is to be returned in a query result.
 * 
 * @param <X>
 *            the type of the selection item
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractSelection<X> extends TupleElementImpl<X> implements Selection<X> {

	private String alias;

	/**
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	public AbstractSelection(Class<X> javaType) {
		super(javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Selection<X> alias(String alias) {
		if (alias == null) {
			throw new NullPointerException();
		}

		this.alias = alias;

		return this;
	}

	/**
	 * Returns the JPQL select fragment.
	 * 
	 * @param query
	 *            the criteria query
	 * @param selected
	 *            if the selection is selected
	 * 
	 * @return the JPQL select fragment
	 * 
	 * @since 2.0.0
	 */
	public abstract String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected);

	/**
	 * Returns the SQL select fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selected
	 *            if the selection is selected
	 * @return the SQL select fragment
	 * 
	 * @since 2.0.0
	 */
	public abstract String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected);

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias() {
		return this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Selection<?>> getCompoundSelectionItems() {
		return null;
	}

	/**
	 * Returns the SQL restriction fragments.
	 * 
	 * @param query
	 *            the query
	 * @return the SQL restriction fragments
	 * 
	 * @since 2.0.0
	 */
	public abstract String[] getSqlRestrictionFragments(BaseQueryImpl<?> query);

	/**
	 * Handles the row.
	 * 
	 * @param query
	 *            the query
	 * @param session
	 *            the session
	 * @param row
	 *            the row
	 * @return the managed instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	public abstract X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isCompoundSelection() {
		return false;
	}

	/**
	 * Returns if the selection is comprises of entities only.
	 * 
	 * @return true if the selection is comprises of entities only, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isEntityList() {
		return false;
	}
}

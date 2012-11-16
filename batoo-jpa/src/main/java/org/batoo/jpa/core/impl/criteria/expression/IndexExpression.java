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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.join.ListJoinImpl;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.OrderColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Expression for list join indices.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class IndexExpression extends AbstractExpression<Integer> {

	private final ListJoinImpl<?, ?> listJoin;
	private final OrderColumn orderColumn;
	private String alias;

	/**
	 * @param listJoin
	 *            the list join
	 * @param orderColumn
	 *            the order column
	 * 
	 * @since 2.0.0
	 */
	public IndexExpression(ListJoinImpl<?, ?> listJoin, OrderColumn orderColumn) {
		super(Integer.class);

		this.listJoin = listJoin;
		this.orderColumn = orderColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return "index(" + this.listJoin.getAlias() + ")";
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
		this.alias = query.getAlias(this);

		if (selected) {
			return this.getSqlRestrictionFragments(query)[0] + " AS " + this.alias;
		}

		return this.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		String tableAlias = this.listJoin.getTableAlias(query, this.orderColumn.getTable());
		if (this.orderColumn.getTable() instanceof JoinTable) {
			tableAlias += "_J";
		}

		return new String[] { tableAlias + "." + this.orderColumn.getName() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return ((Number) row.getObject(this.alias)).intValue();
	}
}

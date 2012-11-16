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

import javax.persistence.criteria.Expression;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.path.BasicPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * Wrapper expression for boolean expressions.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class BooleanExpression extends AbstractExpression<Boolean> {

	private final AbstractExpression<Boolean> inner;

	/**
	 * @param inner
	 *            the inner expression
	 * 
	 * @since 2.0.0
	 */
	public BooleanExpression(Expression<Boolean> inner) {
		super(Boolean.class);

		this.inner = (AbstractExpression<Boolean>) inner;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.inner.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.inner.generateJpqlSelect(null, selected);
	}

	/**
	 * Returns the SQL where fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the SQL select fragment
	 * 
	 * @since 2.0.0
	 */
	public String generateSqlRestriction(final BaseQueryImpl<?> query) {
		if (this.inner instanceof BasicPath) {
			return query.getJdbcAdaptor().castBoolean(this.inner.getSqlRestrictionFragments(query)[0]);
		}

		return this.inner.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.inner.generateSqlSelect(query, selected);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		if (this.inner != null) {
			return this.inner.getSqlRestrictionFragments(query);
		}

		return new String[] { this.generateSqlRestriction(query) };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.inner.handle(query, session, row);
	}
}

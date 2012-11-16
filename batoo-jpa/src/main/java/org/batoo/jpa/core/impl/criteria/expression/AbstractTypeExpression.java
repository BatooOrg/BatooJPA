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

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.criteria.path.BasicPath;

/**
 * Type for query expressions.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractTypeExpression<T> extends AbstractExpression<Class<? extends T>> {

	private final AbstractPath<?> path;
	private String alias;

	/**
	 * @param path
	 *            the path
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public AbstractTypeExpression(AbstractPath<T> path) {
		super((Class<Class<? extends T>>) path.getJavaType().getClass());

		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return "type(" + this.getPath().generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (this.getAlias() != null) {
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

		final String innerExpression = this.path instanceof BasicPath ? //
			this.path.getSqlRestrictionFragments(query)[0] : //
			this.getSqlRestrictionFragments(query)[0];

		if (selected) {
			return innerExpression + " AS " + this.alias;
		}

		return innerExpression;
	}

	/**
	 * Returns the path of the type expression.
	 * 
	 * @return the path of the type expression
	 * 
	 * @since 2.0.0
	 */
	public AbstractPath<?> getPath() {
		return this.path;
	}

	/**
	 * Returns the raw discriminator value from the resultset.
	 * 
	 * @param row
	 *            the row
	 * @return the raw discriminator value from the resultset
	 * @throws SQLException
	 * 
	 * @since 2.0.0
	 */
	protected Object handle(ResultSet row) throws SQLException {
		return row.getObject(this.alias);
	}
}

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
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Expression for constants.
 * 
 * @param <T>
 *            the type of the constant expression
 * 
 * @author hceylan
 * @since $version
 */
public class ConstantExpression<T> extends AbstractExpression<T> {

	private final T value;

	/**
	 * @param type
	 *            the type of the constant.
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ConstantExpression(TypeImpl<T> type, T value) {
		super(type != null ? type.getJavaType() : (Class<T>) value.getClass());

		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		if (Number.class.isAssignableFrom(this.getJavaType())) {
			return this.value.toString();
		}
		else {
			return "'" + this.value.toString() + "'";
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.getSqlRestrictionFragments(query)[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		if (Number.class.isAssignableFrom(this.getJavaType())) {
			return new String[] { this.value.toString() };
		}
		else {
			return new String[] { "'" + this.value.toString() + "'" };
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.value;
	}
}

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

import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP functions.
 * 
 * @param <T>
 *            the temporal type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CurrentTemporalExpression<T> extends AbstractExpression<T> {

	private final TemporalType temporalType;
	private String alias;

	/**
	 * @param temporalType
	 *            the temporal type
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	public CurrentTemporalExpression(TemporalType temporalType, Class<T> javaType) {
		super(javaType);

		this.temporalType = temporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		switch (this.temporalType) {
			case DATE:
				return "current_date";
			case TIME:
				return "current_time";
			default:
				return "current_timestamp";
		}
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
		switch (this.temporalType) {
			case DATE:
				return new String[] { query.getJdbcAdaptor().getCurrentDate() };
			case TIME:
				return new String[] { query.getJdbcAdaptor().getCurrentTime() };
			default:
				return new String[] { query.getJdbcAdaptor().getCurrentTimeStamp() };
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (T) row.getObject(this.alias);
	}
}

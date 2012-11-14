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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.ParameterExpression;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Type of criteria query parameter expressions.
 * 
 * @param <T>
 *            the type of the parameter expression
 * @author hceylan
 * @since $version
 */
public class ParameterExpressionImpl<T> extends AbstractParameterExpressionImpl<T> implements ParameterExpression<T> {

	private Integer position;

	/**
	 * @param q
	 *            the query
	 * @param type
	 *            the persistent type of the parameter
	 * @param paramClass
	 *            the class of the parameter
	 * @param position
	 *            the ordinal position of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl(BaseQueryImpl<?> q, TypeImpl<T> type, Class<T> paramClass, int position) {
		super(type, paramClass);

		this.position = position;
		this.alias(Integer.toString(position));

		q.getAlias(this);
	}

	/**
	 * @param type
	 *            the persistent type of the parameter
	 * @param paramClass
	 *            the class of the parameter
	 * @param name
	 *            the name of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParameterExpressionImpl(TypeImpl<T> type, Class<T> paramClass, String name) {
		super(type, paramClass);

		if (StringUtils.isNotBlank(name)) {
			this.alias(name);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void ensureAlias(BaseQueryImpl<?> query) {
		if ((this.position == null)) {
			this.position = query.getAlias(this);

			if (StringUtils.isBlank(this.getAlias())) {
				this.alias("param" + this.position);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		this.ensureAlias(query);

		try {
			final int positionNo = Integer.parseInt(this.getAlias());

			return "?" + positionNo;
		}
		catch (final Exception e) {}

		return ":" + this.getAlias();
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
		this.ensureAlias(query);

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.getAlias();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<T> getParameterType() {
		return (Class<T>) this.getJavaType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getPosition() {
		return this.position;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final T value = query.getParameterValue(this);

		return (T) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setParameter(MetamodelImpl metamodel, Connection connections, Object[] parameters, MutableInt sqlIndex, Object value) {
		super.setParameter(metamodel, connections, parameters, sqlIndex, value);
	}
}

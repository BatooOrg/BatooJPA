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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
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
public class EntityConstantExpression<T> extends AbstractParameterExpressionImpl<T> {

	private final T value;
	private Object position;

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
	public EntityConstantExpression(TypeImpl<T> type, T value) {
		super(type, (Class<T>) value.getClass());

		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void ensureAlias(BaseQueryImpl<?> query) {
		if (this.position == null) {
			this.position = query.getAlias(this);
			if (StringUtils.isBlank(this.getAlias())) {
				this.alias("const" + this.position);
			}
		}
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
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.value;
	}

	/**
	 * Sets the parameters expanding if necessary.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param connection
	 *            the connection
	 * @param parameters
	 *            the SQL parameters
	 * @param sqlIndex
	 *            the index corresponding to expanded SQL parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setParameter(MetamodelImpl metamodel, Connection connection, Object[] parameters, MutableInt sqlIndex) {
		super.setParameter(metamodel, connection, parameters, sqlIndex, this.value);
	}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.ParameterExpression;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Type of criteria query parameter expressions.
 * 
 * @param <T>
 *            the type of the parameter expression
 * @author hceylan
 * @since $version
 */
public class ParameterExpressionImpl<T> extends AbstractExpression<T> implements ParameterExpression<T> {

	private final TypeImpl<?> type;
	private Integer position;

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
		super(paramClass);

		this.type = type;
		if (StringUtils.isNotBlank(name)) {
			this.alias(name);
		}
	}

	/**
	 * Ensures the alias has been created.
	 * 
	 * @param query
	 *            the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void ensureAlias(CriteriaQueryImpl<?> query) {
		if (this.position == null) {
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
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		this.ensureAlias(query);

		return builder.append(":").append(this.getAlias()).toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.ensureAlias(query);

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.ensureAlias(query);

		return null;
	}

	/**
	 * Returns the number of SQL parameters when expanded.
	 * 
	 * @return the number of SQL parameters when expanded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getExpandedCount() {
		if (this.type.getPersistenceType() == PersistenceType.BASIC) {
			return 1;
		}
		else if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			return ((EmbeddableTypeImpl<?>) this.type).getAttributeCount();
		}

		return ((EntityTypeImpl<?>) this.type).getPrimaryTable().getPkColumns().size();
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
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		this.ensureAlias(query);

		query.setNextSqlParam(this);

		final String[] restrictions = new String[this.getExpandedCount()];

		for (int i = 0; i < restrictions.length; i++) {
			restrictions[i] = "?";
		}

		return restrictions;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final T value = query.getParameterValue(this);

		return (T) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * Sets the parameters expanding if necessary.
	 * 
	 * @param parameters
	 *            the SQL parameters
	 * @param sqlIndex
	 *            the index corresponding to expanded SQL parameter
	 * @param value
	 *            the value to set to the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setParameter(Object[] parameters, MutableInt sqlIndex, Object value) {
		if (this.type.getPersistenceType() == PersistenceType.BASIC) {
			parameters[sqlIndex.intValue()] = value;

			sqlIndex.increment();
		}
		else if (this.type.getPersistenceType() == PersistenceType.ENTITY) {
			final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.type;

			this.setParameter(parameters, sqlIndex, value, type);
		}
		else {
			final EmbeddableTypeImpl<?> type = (EmbeddableTypeImpl<?>) this.type;

			this.setParameter(parameters, sqlIndex, value, type);
		}
	}

	private void setParameter(Object[] parameters, MutableInt sqlIndex, Object value, final EmbeddableTypeImpl<?> type) {
		final SingularAttributeImpl<?, ?>[] attributes = type.getSingularMappings();

		for (final SingularAttributeImpl<?, ?> attribute : attributes) {
			switch (attribute.getPersistentAttributeType()) {
				case BASIC:
					parameters[sqlIndex.intValue()] = attribute.get(value);
					sqlIndex.increment();
					break;
				case MANY_TO_ONE:
				case ONE_TO_ONE:
					this.setParameter(parameters, sqlIndex, attribute.get(value), (EntityTypeImpl<?>) attribute.getType());
					break;
				case EMBEDDED:
					this.setParameter(parameters, sqlIndex, attribute.get(value), (EmbeddableTypeImpl<?>) this.type);
			}
		}
	}

	private void setParameter(Object[] parameters, MutableInt sqlIndex, Object value, final EntityTypeImpl<?> type) {
		for (final PkColumn column : type.getPrimaryTable().getPkColumns()) {
			parameters[sqlIndex.intValue()] = column.getMapping().get(value);

			sqlIndex.increment();
		}
	}
}

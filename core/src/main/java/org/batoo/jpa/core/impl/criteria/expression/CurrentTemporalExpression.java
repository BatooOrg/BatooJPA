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

import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP functions.
 * 
 * @param <T>
 *            the temporal type
 * 
 * @author hceylan
 * @since $version
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
	 * @since $version
	 * @author hceylan
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
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
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
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
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
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
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
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		switch (this.temporalType) {
			case DATE:
				return new String[] { "CURRENT_DATE" };
			case TIME:
				return new String[] { "CURRENT_TIME" };
			default:
				return new String[] { "CURRENT_TIMESTAMP" };
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (T) row.getObject(this.alias);
	}
}

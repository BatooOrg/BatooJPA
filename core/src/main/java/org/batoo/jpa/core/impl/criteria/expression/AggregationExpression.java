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
import java.text.MessageFormat;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * The expression for aggregate functions
 * 
 * @param <N>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class AggregationExpression<N extends Number> extends AbstractExpression<N> {

	@SuppressWarnings("javadoc")
	public enum AggregationFunctionType {
		AVG("AVG({0})", "avg({0})"),

		SUM("SUM({0})", "sum({0})"),

		MIN("MIN({0})", "min({0})"),

		MAX("MAX({0})", "max({0})");

		private final String sqlFragment;
		private final String jpqlFragment;

		private AggregationFunctionType(String sqlfragment, String jpqlFragment) {
			this.sqlFragment = sqlfragment;
			this.jpqlFragment = jpqlFragment;
		}
	}

	private final AggregationFunctionType type;
	private final AbstractExpression<?> x;
	private String alias;

	/**
	 * @param type
	 *            the type of the function
	 * @param x
	 *            the first parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AggregationExpression(AggregationFunctionType type, Expression<?> x) {
		super((Class<N>) x.getJavaType());

		this.type = type;
		this.x = (AbstractExpression<?>) x;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		return MessageFormat.format(this.type.jpqlFragment, this.x.generateJpqlRestriction(query));
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
		return new String[] { MessageFormat.format(this.type.sqlFragment, this.x.getSqlRestrictionFragments(query)[0]) };

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public N handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		if (this.getJavaType() == Long.class) {
			return (N) (Long) row.getLong(this.alias);
		}

		if (this.getJavaType() == Double.class) {
			return (N) (Double) row.getDouble(this.alias);
		}

		return (N) row.getObject(this.alias);
	}
}

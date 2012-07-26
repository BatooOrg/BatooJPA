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
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.criteria.Expression;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class InExpression extends BooleanExpression {

	private final AbstractExpression<?> inner;
	private final ArrayList<AbstractExpression<?>> values = Lists.newArrayList();
	private String alias;

	/**
	 * @param inner
	 *            the inner expression
	 * @param values
	 *            the values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public InExpression(AbstractExpression<?> inner, Expression<?>[] values) {
		super();

		this.inner = inner;
		for (final Expression<?> expression : values) {
			this.values.add((AbstractExpression<?>) expression);
		}
	}

	/**
	 * @param inner
	 *            the inner expression
	 * @param values
	 *            the values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public InExpression(Expression<?> inner, Collection<?> values) {
		super();

		this.inner = (AbstractExpression<?>) inner;
		for (final Object value : values) {
			if (value instanceof AbstractExpression) {
				this.values.add((AbstractExpression<?>) value);
			}
			else {
				this.values.add(new ConstantExpression<Object>(null, value));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(final CriteriaQueryImpl<?> query) {
		final String values = Joiner.on(", ").join(Lists.transform(this.values, new Function<AbstractExpression<?>, String>() {

			@Override
			public String apply(AbstractExpression<?> input) {
				return input.generateJpqlRestriction(query);
			}
		}));

		return this.inner.generateJpqlRestriction(query) + " in (" + values + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		if (selected && StringUtils.isBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlRestriction(final CriteriaQueryImpl<?> query) {
		final String inner = this.inner.getSqlRestrictionFragments(query)[0];

		final String values = Joiner.on(", ").join(Lists.transform(this.values, new Function<AbstractExpression<?>, String>() {

			@Override
			public String apply(AbstractExpression<?> input) {
				return input.getSqlRestrictionFragments(query)[0];
			}
		}));

		return inner + " IN (" + values + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		this.alias = query.getAlias(this);

		return this.generateSqlRestriction(query) + " AS " + this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return (Boolean) row.getObject(this.alias);
	}
}

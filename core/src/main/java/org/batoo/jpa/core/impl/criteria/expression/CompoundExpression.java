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
import java.util.HashMap;

import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class CompoundExpression extends AbstractExpression<Boolean> {

	/**
	 * The comparison types
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum Comparison {

		/**
		 * Equal comparison
		 */
		EQUAL(" = ");

		private final String fragment;

		Comparison(String fragment) {
			this.fragment = fragment;
		}

		/**
		 * Returns the fragment of the Comparison.
		 * 
		 * @return the fragment of the Comparison
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public String getFragment() {
			return this.fragment;
		}
	}

	private final Comparison comparison;
	private final AbstractExpression<?> x;
	private final AbstractExpression<?> y;

	/**
	 * @param comparison
	 *            the comparison
	 * @param x
	 *            the left side expression
	 * @param y
	 *            the right side expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CompoundExpression(Comparison comparison, AbstractExpression<?> x, AbstractExpression<?> y) {
		super(Boolean.class);

		this.comparison = comparison;
		this.x = x;
		this.y = y;

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query, Comparison comparison, ParameterExpressionImpl<?> parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction() {
		return this.x.generateJpqlRestriction() + this.comparison.getFragment() + this.y.generateJpqlRestriction();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect() {
		return this.x.generateJpqlSelect() + this.comparison.getFragment() + this.y.generateJpqlSelect();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(CriteriaQueryImpl<?> query) {
		if (this.x instanceof ParameterExpressionImpl) {
			return this.y.generate(query, this.comparison, (ParameterExpressionImpl<?>) this.x);
		}
		else if (this.y instanceof ParameterExpressionImpl) {
			return this.x.generate(query, this.comparison, (ParameterExpressionImpl<?>) this.y);
		}

		return this.x.generateSqlSelect(query) + this.comparison.getFragment() + this.y.generateSqlSelect(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean handle(SessionImpl session, ResultSet row, HashMap<ManagedInstance<?>, ManagedInstance<?>> instances) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.generateJpqlRestriction();
	}
}

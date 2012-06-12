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
package org.batoo.jpa.core.impl.criteria;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.manager.SessionImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class CompoundExpressionImpl extends ExpressionImpl<Boolean> {

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
	private final ExpressionImpl<?> x;
	private final ExpressionImpl<?> y;

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
	public CompoundExpressionImpl(Comparison comparison, ExpressionImpl<?> x, ExpressionImpl<?> y) {
		super();

		this.comparison = comparison;
		this.x = x;
		this.y = y;

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String describe() {
		return this.x + this.comparison.getFragment() + this.y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		return this.x.generate(query) + this.comparison.getFragment() + this.y.generate(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Boolean> handle(SessionImpl session, BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, MutableInt rowNo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.describe();
	}
}

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

/**
 * The comparison types
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("javadoc")
public enum Comparison {

	EQUAL("{0} = {1}"),

	NOT_EQUAL("{0} <> {1}"),

	LESS("{0} < {1}"),

	LESS_OR_EQUAL("{0} <= {1}"),

	GREATER("{0} > {1}"),

	GREATER_OR_EQUAL("{0} >= {1}"),

	LIKE("{0} LIKE {1}"),

	BETWEEN("{0} BETWEEN {1} AND {2}");

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

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
package org.batoo.jpa.core;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class QueryException extends BatooException {

	private static final long serialVersionUID = 1L;

	private final String queryString;

	/**
	 * @param queryString
	 * @since $version
	 * @author hceylan
	 */
	public QueryException(String queryString) {
		this(null, queryString, null);
	}

	/**
	 * @param message
	 * @param queryString
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryException(String message, String queryString) {
		this(message, queryString, null);
	}

	/**
	 * @param message
	 * @param cause
	 * @param queryString
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryException(String message, String queryString, Throwable cause) {
		super(message, cause);

		this.queryString = queryString;
	}

	/**
	 * @param cause
	 * @param queryString
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public QueryException(String queryString, Throwable cause) {
		this(null, queryString, cause);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMessage() {
		return super.getMessage() + " [" + this.queryString + ']';
	}
}

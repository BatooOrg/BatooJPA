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
package org.batoo.jpa.parser.metadata;

import java.util.Map;

import javax.persistence.LockModeType;

/**
 * The definition for named queries.
 * 
 * @author hceylan
 * @since $version
 */
public interface NamedQueryMetadata extends BindableMetadata, LocatableMatadata {

	/**
	 * Returns the list query hints.
	 * 
	 * @return the list query hints
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Map<String, Object> getHints();

	/**
	 * Returns the lock mode.
	 * 
	 * @return the lock mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	LockModeType getLockMode();

	/**
	 * Returns the query.
	 * 
	 * @return the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getQuery();
}

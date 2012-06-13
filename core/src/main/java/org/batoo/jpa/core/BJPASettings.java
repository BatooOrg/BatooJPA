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
public interface BJPASettings {

	/**
	 * The warn time for SQL operations taking too long.
	 */
	final long WARN_TIME = 2500;

	/**
	 * Class loader to use during deployment, default null
	 */
	String CLASS_LOADER_CLASS = "org.batoo.jpa.class_loader_class";

	/**
	 * The root package for the persistence unit to scan for entities, default null
	 */
	String ROOT_PACKAGE = "org.batoo.jpa.root_url";

	/**
	 * If JDBC drivers on the classpath should be scanned, default false
	 */
	String SCAN_EXTERNAL_JDBC_DRIVERS = "org.batoo.jpa.root_url";

	/**
	 * DDL operations, DROP | CREATE (*) | UPDATE | VERIFY | NONE
	 */
	String DDL = "org.batoo.jpa.ddl";

}

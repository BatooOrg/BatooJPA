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

import java.util.List;

/**
 * The common definitio of tables.
 * 
 * @author hceylan
 * @since $version
 */
public interface TableMetadata extends BindableMetadata {

	/**
	 * Returns the name of the catalog of the table.
	 * 
	 * @return the name of the catalog of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getCatalog();

	/**
	 * Returns the name of the schema of the table.
	 * 
	 * @return the name of the schema of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getSchema();

	/**
	 * Returns the list of unique constraints of the table.
	 * 
	 * @return the list of unique constraints of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<UniqueConstraintMetadata> getUniqueConstraints();

}

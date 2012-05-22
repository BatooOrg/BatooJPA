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
 * The definition of the table generators.
 * 
 * @author hceylan
 * @since $version
 */
public interface TableGeneratorMetadata extends GeneratorMetadata {

	/**
	 * Returns the name of the primary key column of the table generator.
	 * 
	 * @return the name of the primary key column of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getPkColumnName();

	/**
	 * Returns the primary key value of the table generator.
	 * 
	 * @return the primary key value of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getPkColumnValue();

	/**
	 * Returns the table name of the table generator.
	 * 
	 * @return the table name of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTable();

	/**
	 * Returns the list of unique constraints of the table generator.
	 * 
	 * @return the list of unique constraints of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<UniqueConstraintMetadata> getUniqueConstraints();

	/**
	 * Returns the name of the value column of the table generator.
	 * 
	 * @return the name of the value column of the table generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getValueColumnName();

}

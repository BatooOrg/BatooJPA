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

/**
 * The common definition of the columns.
 * 
 * @author hceylan
 * @since $version
 */
public interface BaseColumnMetadata extends BindableMetadata {

	/**
	 * Returns the raw column definition of the column.
	 * 
	 * @return the raw column definition of the column.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getColumnDefinition();

	/**
	 * Returns the table of the column.
	 * 
	 * @return the table of the column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTable();

	/**
	 * Returns if the column is insertable.
	 * 
	 * @return true if the column is insertable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isInsertable();

	/**
	 * Returns if the column is nullable.
	 * 
	 * @return true if the column is nullable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isNullable();

	/**
	 * Returns if the column is unique.
	 * 
	 * @return true if the column is unique
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isUnique();

	/**
	 * Returns if the column is updatable.
	 * 
	 * @return true if the column is updatable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean isUpdatable();
}

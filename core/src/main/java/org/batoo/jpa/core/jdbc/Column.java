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
package org.batoo.jpa.core.jdbc;

import java.sql.Types;

/**
 * 
 * @author hceylan
 * @since $version
 */
public interface Column {

	/**
	 * Returns the generationType.
	 * 
	 * @return the generationType
	 * @since $version
	 */
	IdType getIdType();

	/**
	 * Returns the length.
	 * 
	 * @return the length
	 * @since $version
	 */
	int getLength();

	/**
	 * Returns the physicalName.
	 * 
	 * @return the physicalName
	 * @since $version
	 */
	String getName();

	/**
	 * Returns the physicalName.
	 * 
	 * @return the physicalName
	 * @since $version
	 */
	String getPhysicalName();

	/**
	 * Returns the precision.
	 * 
	 * @return the precision
	 * @since $version
	 */
	int getPrecision();

	/**
	 * Returns the scale.
	 * 
	 * @return the scale
	 * @since $version
	 */
	int getScale();

	/**
	 * Returns the SQL type of the column
	 * 
	 * @return the SQL type corresponding {@link Types}
	 * @since $version
	 * @author hceylan
	 */
	int getSqlType();

	/**
	 * Returns the nullable.
	 * 
	 * @return the nullable
	 * @since $version
	 */
	boolean isNullable();

	/**
	 * Returns the unique.
	 * 
	 * @return the unique
	 * @since $version
	 */
	boolean isUnique();
}

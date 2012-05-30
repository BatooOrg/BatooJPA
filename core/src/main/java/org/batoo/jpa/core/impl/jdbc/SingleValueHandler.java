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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * A Handler to return single values from the result sets.
 * 
 * @param <T>
 *            the target type the input ResultSet will be converted to.
 * 
 * @author hceylan
 * @since $version
 */
public class SingleValueHandler<T> implements ResultSetHandler<T> {

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingleValueHandler() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(ResultSet rs) throws SQLException {
		if (rs.next()) {
			return (T) rs.getObject(1);
		}

		return null;
	}
}

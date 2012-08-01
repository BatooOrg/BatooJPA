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
package org.batoo.jpa.core.impl.criteria.join;

import org.batoo.jpa.core.impl.criteria.AbstractQueryImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Interface for joinable types.
 * 
 * @author hceylan
 * @since $version
 */
public interface Joinable {

	/**
	 * Returns the entity of the joinable.
	 * 
	 * @return the entity of the joinable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	EntityTypeImpl<?> getEntity();

	/**
	 * Returns the alias for the table.
	 * <p>
	 * if table does not have an alias, it is generated.
	 * 
	 * @param query
	 *            the query
	 * @param table
	 *            the table
	 * @return the alias for the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTableAlias(AbstractQueryImpl<?> query, AbstractTable table);
}

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
package org.batoo.jpa.core.impl.criteria;

import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.BatooUtils;

/**
 * A root type in the from clause. Query roots always reference entities.
 * 
 * @param <X>
 *            the entity type referenced by the root
 * @author hceylan
 * @since $version
 */
public class RootImpl<X> extends FromImpl<X, X> implements Root<X> {

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RootImpl(EntityTypeImpl<X> entity) {
		super(entity);
	}

	/**
	 * Returns the join descriptions.
	 * 
	 * @return the join descriptions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String describeJoins() {
		final String root = StringUtils.isNotBlank(this.getAlias()) ? this.getAlias() : this.getModel().getName();

		return BatooUtils.indent(this.getFetchRoot().describe(root));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generate(CriteriaQueryImpl<?> query) {
		return this.getFetchRoot().generate(query);
	}

	/**
	 * Returns the generated from SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated from SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateFrom(CriteriaQueryImpl<?> query) {
		final EntityTable primaryTable = this.getModel().getRootType().getPrimaryTable();

		return primaryTable.getName() + " AS " + this.getFetchRoot().getTableAlias(query, primaryTable);
	}
}

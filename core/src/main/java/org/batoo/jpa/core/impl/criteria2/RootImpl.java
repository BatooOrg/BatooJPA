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
package org.batoo.jpa.core.impl.criteria2;

import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.model.mapping.RootMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * A root type in the from clause. Query roots always reference entities.
 * 
 * @param <X>
 *            the entity type referenced by the root
 * 
 * @author hceylan
 * @since $version
 */
public class RootImpl<X> extends AbstractFrom<X, X> implements Root<X> {

	private final RootMapping<X> mapping;

	/**
	 * @param mapping
	 *            the mapping
	 * @param generatedAlias
	 *            the generated alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RootImpl(RootMapping<X> mapping, String generatedAlias) {
		super(null, mapping, generatedAlias);
		this.mapping = mapping;
	}

	/**
	 * Returns the <code>from</code> fragment for the <code>JPQL</code> generation.
	 * 
	 * @return the <code>from</code> fragment for the <code>JPQL</code> generation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateFromJpql() {
		final StringBuilder sb = new StringBuilder(this.mapping.getType().getName());

		if (StringUtils.isNotBlank(this.getAlias())) {
			sb.append(" as ").append(this.getAlias());
		}

		return sb.toString();
	}

	/**
	 * Returns the <code>from</code> fragment for the <code>SQL</code> generation.
	 * 
	 * @param query
	 *            the query
	 * @return the <code>from</code> fragment for the <code>SQL</code> generation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateFromSql(CriteriaQueryImpl<?> query) {
		final EntityTable primaryTable = this.getModel().getRootType().getPrimaryTable();

		return primaryTable.getName() + " AS " + this.getAlias();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.mapping.getType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractPath<?> getParentPath() {
		return null;
	}
}

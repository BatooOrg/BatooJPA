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
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.path.ParentPath;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The type expression for entities.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTypeExpression<T> extends AbstractTypeExpression<T> {

	private final DiscriminatorColumn discriminatorColumn;

	/**
	 * @param path
	 *            the from
	 * @param discriminatorColumn
	 *            the discriminator column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeExpression(ParentPath<?, T> path, DiscriminatorColumn discriminatorColumn) {
		super(path);

		this.discriminatorColumn = discriminatorColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final String tableAlias = this.getPath().getRootPath().getTableAlias(query, this.discriminatorColumn.getTable());
		return new String[] { tableAlias + "." + this.discriminatorColumn.getName() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends T> handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final Object object = this.getPath().handle(query, session, row);

		if (object == null) {
			return null;
		}

		if (object instanceof EnhancedInstance) {
			final ManagedInstance<?> managedInstance = ((EnhancedInstance) object).__enhanced__$$__getManagedInstance();
			final EntityTypeImpl<? extends T> type = (EntityTypeImpl<? extends T>) managedInstance.getType();

			return type.getJavaType();
		}

		return (Class<? extends T>) object.getClass();
	}
}

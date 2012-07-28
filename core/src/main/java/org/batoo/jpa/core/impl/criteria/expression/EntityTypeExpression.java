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

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.criteria.path.PluralAssociationPath;
import org.batoo.jpa.core.impl.criteria.path.SingularAssociationPath;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
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

	/**
	 * @param from
	 *            the from
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeExpression(AbstractPath<?> from) {
		super(from);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(CriteriaQueryImpl<?> query) {
		return "type(" + this.getPath().generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		final AbstractPath<?> path = this.getPath();

		EntityTypeImpl<?> entity = null;
		if (path instanceof AbstractFrom) {
			entity = ((AbstractFrom<?, ?>) path).getEntity();
		}
		else if (path instanceof SingularAssociationPath) {
			entity = ((SingularAssociationPath<?, ?>) path).getMapping().getType();
		}
		else {
			entity = ((PluralAssociationPath<?, ?>) path).getMapping().getType();
		}

		return new String[] { entity.getRootType().getDiscriminatorColumn().getName() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final Object object = this.getPath().handle(query, session, row);

		if (object == null) {
			return null;
		}

		if (object instanceof EnhancedInstance) {
			return (T) ((EnhancedInstance) object).__enhanced__$$__getManagedInstance().getType().getJavaType();
		}

		return (T) object.getClass();
	}
}

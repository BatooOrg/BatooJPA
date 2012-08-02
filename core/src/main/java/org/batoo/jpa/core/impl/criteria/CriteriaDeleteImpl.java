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

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * Implementation of {@link CriteriaDelete}.
 * 
 * @param <T>
 *            the entity type that is the target of the delete
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaDeleteImpl<T> extends CriteriaModify<T> implements CriteriaDelete<T> {

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaDeleteImpl(MetamodelImpl metamodel) {
		super(metamodel);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpql() {
		final RootImpl<T> root = this.getRoot();

		if (StringUtils.isBlank(root.getAlias())) {
			this.getRoot().alias("r");
		}

		String restriction = "";
		if (this.getRestriction() != null) {
			restriction = "\n" + this.getRestriction().generateJpqlRestriction(this);
		}

		return "delete " + this.getRoot().generateJpqlRestriction(this) + " as " + root.getAlias() + restriction;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSql() {
		final String sqlRestriction = this.generateSqlRestriction();

		return "DELETE FROM " + this.getRoot().generateSqlFrom(this) + (StringUtils.isNotBlank(sqlRestriction) ? "\nWHERE " + sqlRestriction : "");
	}

	/**
	 * Returns the restriction for the query.
	 * 
	 * @return the restriction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String generateSqlRestriction() {
		if ((this.getRestriction() == null) && (this.getRoot().getEntity().getRootType().getInheritanceType() == null)) {
			return null;
		}

		final String sqlRestriction = this.getRestriction().generateSqlRestriction(this);

		if (this.getRoot().getEntity().getRootType().getInheritanceType() == null) {
			return sqlRestriction;
		}

		return "(" + sqlRestriction + ") AND (" + this.getRoot().generateDiscrimination() + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaDeleteImpl<T> where(Expression<Boolean> restriction) {
		return (CriteriaDeleteImpl<T>) super.where(restriction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaDeleteImpl<T> where(Predicate... restrictions) {
		return (CriteriaDeleteImpl<T>) super.where(restrictions);
	}
}

/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.criteria;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * Implementation of CriteriaDelete.
 * 
 * @param <T>
 *            the entity type that is the target of the delete
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CriteriaDeleteImpl<T> extends CriteriaModify<T> {

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since 2.0.0
	 */
	public CriteriaDeleteImpl(MetamodelImpl metamodel) {
		super(metamodel);
	}

	/**
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param qlString
	 *            the JPQL query
	 * 
	 * @since $version
	 */
	public CriteriaDeleteImpl(MetamodelImpl metamodel, String qlString) {
		super(metamodel, qlString);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isQuery() {
		return false;
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

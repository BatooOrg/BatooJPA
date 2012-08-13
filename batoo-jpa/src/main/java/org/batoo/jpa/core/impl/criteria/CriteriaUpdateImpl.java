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

import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * Implementation of {@link CriteriaUpdate}.
 * 
 * @param <T>
 *            the entity type that is the target of the update
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaUpdateImpl<T> extends CriteriaModify<T> implements CriteriaUpdate<T> {

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaUpdateImpl(MetamodelImpl metamodel) {
		super(metamodel);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpql() {
		final StringBuilder builder = new StringBuilder();

		builder.append("update " + this.getRoot().getEntity().getName());

		if (this.getRestriction() != null) {
			builder.append("\nwhere\n\t").append(this.getRestriction().generateJpqlRestriction(this));
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSql() {
		final String sqlRestriction = this.generateSqlRestriction();
		final String updates = "";

		return "UPDATE " + this.getRoot().generateSqlFrom(this) + "\nSET " + updates
			+ (StringUtils.isNotBlank(sqlRestriction) ? "\nWHERE " + sqlRestriction : "");
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

		return "(" + sqlRestriction + ") AND (" + this.getRoot().generateDiscrimination(true) + ")";
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
	public <Y> CriteriaUpdate<T> set(Path<Y> attribute, Expression<? extends Y> value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y, X extends Y> CriteriaUpdate<T> set(Path<Y> attribute, X value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> attribute, Expression<? extends Y> value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y, X extends Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> attribute, X value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaUpdate<T> set(String attributeName, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaUpdateImpl<T> where(Expression<Boolean> restriction) {
		return (CriteriaUpdateImpl<T>) super.where(restriction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaUpdateImpl<T> where(Predicate... restrictions) {
		return (CriteriaUpdateImpl<T>) super.where(restrictions);
	}
}

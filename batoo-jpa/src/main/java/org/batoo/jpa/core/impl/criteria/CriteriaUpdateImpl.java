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

import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * Implementation of CriteriaUpdate.
 * 
 * @param <T>
 *            the entity type that is the target of the update
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CriteriaUpdateImpl<T> extends CriteriaModify<T> {

	private final Map<AbstractPath<?>, AbstractExpression<?>> updates = Maps.newHashMap();

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since 2.0.0
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
		final String update = Joiner.on(",").join(
			Collections2.transform(this.updates.entrySet(), new Function<Entry<AbstractPath<?>, AbstractExpression<?>>, String>() {

				@Override
				public String apply(Entry<AbstractPath<?>, AbstractExpression<?>> input) {
					return "\t" + input.getKey().getSqlRestrictionFragments(CriteriaUpdateImpl.this)[0] + " = "
						+ input.getValue().getSqlRestrictionFragments(CriteriaUpdateImpl.this)[0];
				}
			}));

		final String sqlRestriction = this.generateSqlRestriction();

		return "UPDATE " + this.getRoot().generateSqlFrom(this) + " SET\n" + update + //
			(StringUtils.isNotBlank(sqlRestriction) ? "\nWHERE " + sqlRestriction : "");
	}

	/**
	 * Returns the restriction for the query.
	 * 
	 * @return the restriction
	 * 
	 * @since 2.0.0
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
	 * Adds the set item.
	 * 
	 * @param attribute
	 *            the attribute
	 * @param value
	 *            the value
	 * @return the same update criteria
	 * @param <Y>
	 *            the typ of the attribute
	 * 
	 * @since 2.0.0
	 */
	public <Y> CriteriaUpdateImpl<T> set(Path<Y> attribute, Expression<? extends Y> value) {
		this.updates.put((AbstractPath<?>) attribute, (AbstractExpression<?>) value);

		return this;
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

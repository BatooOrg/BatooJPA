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
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * Base class for for the update and delete criterias.
 * 
 * @param <T>
 *            the type of the modify criteria.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class CriteriaModify<T> extends BaseQueryImpl<T> {

	private RootImpl<T> root;
	private PredicateImpl restriction;
	private String jpql;

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaModify(MetamodelImpl metamodel) {
		super(metamodel);
	}

	/**
	 * Create and add a query root corresponding to the entity that is the target of the delete. A CriteriaDelete object has a single root,
	 * the object that is being deleted.
	 * 
	 * @param entityClass
	 *            the entity class
	 * @return query root corresponding to the given entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public Root<T> from(Class<T> entityClass) {
		if (this.root != null) {
			throw new IllegalStateException("Root has already assigned");
		}

		return this.root = new RootImpl<T>((EntityTypeImpl<T>) this.getMetamodel().getEntity(entityClass));
	}

	/**
	 * Create and add a query root corresponding to the entity that is the target of the delete. A CriteriaDelete object has a single root,
	 * the object that is being deleted.
	 * 
	 * @param entity
	 *            metamodel entity representing the entity of type X
	 * @return query root corresponding to the given entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Root<T> from(EntityType<T> entity) {
		if (this.root != null) {
			throw new IllegalStateException("Root has already assigned");
		}

		return this.root = new RootImpl<T>((EntityTypeImpl<T>) entity);
	}

	/**
	 * Returns the JPQL for the query.
	 * 
	 * @return the the JPQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public String getJpql() {
		if (this.jpql != null) {
			return this.jpql;
		}

		synchronized (this) {
			if (this.jpql != null) {
				return this.jpql;
			}

			return this.jpql = this.generateJpql();
		}
	}

	/**
	 * Return the predicate that corresponds to the where clause restriction(s), or null if no restrictions have been specified.
	 * 
	 * @return where clause predicate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PredicateImpl getRestriction() {
		return this.restriction;
	}

	/**
	 * Return the query root.
	 * 
	 * @return the query root
	 */
	public RootImpl<T> getRoot() {
		return this.root;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isInternal() {
		return false;
	}

	/**
	 * Create a subquery of the query.
	 * 
	 * @param type
	 *            the subquery result type
	 * @param <U>
	 *            type of the sub query
	 * @return subquery
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public <U> SubqueryImpl<U> subquery(Class<U> type) {
		return new SubqueryImpl<U>(this.getMetamodel(), this, type);
	}

	/**
	 * Modify the query to restrict the target of the deletion according to the specified boolean expression. Replaces the previously added
	 * restriction(s), if any.
	 * 
	 * @param restriction
	 *            a simple or compound boolean expression
	 * @return the modified query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaModify<T> where(Expression<Boolean> restriction) {
		this.restriction = new PredicateImpl((AbstractExpression<Boolean>) restriction);

		return this;
	}

	/**
	 * Modify the query to restrict the target of the deletion according to the conjunction of the specified restriction predicates.
	 * Replaces the previously added restriction(s), if any. If no restrictions are specified, any previously added restrictions are simply
	 * removed.
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return the modified query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaModify<T> where(Predicate... restrictions) {
		this.restriction = new PredicateImpl(this.restriction);

		return this;
	}
}

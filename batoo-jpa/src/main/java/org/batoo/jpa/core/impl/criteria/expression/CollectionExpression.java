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
package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.path.ParentPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;

/**
 * 
 * @param <C>
 *            the type of the collection
 * @param <E>
 *            the type of the element
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CollectionExpression<C extends Collection<E>, E> extends AbstractExpression<C> {

	private final ParentPath<?, ?> parentPath;
	private final AbstractMapping<?, Collection<E>, E> abstractMapping;

	/**
	 * @param parentPath
	 *            the parent path
	 * @param mapping
	 *            the mapping
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public CollectionExpression(ParentPath<?, ?> parentPath, AbstractMapping<?, Collection<E>, E> mapping) {
		super((Class<C>) mapping.getJavaType());

		this.parentPath = parentPath;
		this.abstractMapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return this.parentPath.generateJpqlRestriction(query) + "." + this.abstractMapping.getAttribute().getName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection paths cannot be selected: " + this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		throw new IllegalArgumentException("Collection paths cannot be selected: " + this);
	}

	/**
	 * Returns the abstractMapping of the CollectionExpression.
	 * 
	 * @return the abstractMapping of the CollectionExpression
	 * 
	 * @since 2.0.0
	 */
	public AbstractMapping<?, Collection<E>, E> getMapping() {
		return this.abstractMapping;
	}

	/**
	 * Returns the parentPath of the CollectionExpression.
	 * 
	 * @return the parentPath of the CollectionExpression
	 * 
	 * @since 2.0.0
	 */
	public ParentPath<?, ?> getParentPath() {
		return this.parentPath;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		throw new PersistenceException("Collection paths cannot be restricted: " + this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public C handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "CollectionExpression [parentPath=" + this.parentPath + ", abstractMapping=" + this.abstractMapping + "]";
	}
}

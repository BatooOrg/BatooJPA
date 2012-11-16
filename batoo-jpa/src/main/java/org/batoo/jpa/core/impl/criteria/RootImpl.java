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

import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * A root type in the from clause. Query roots always reference entities.
 * 
 * @param <X>
 *            the entity type referenced by the root
 * @author hceylan
 * @since 2.0.0
 */
public class RootImpl<X> extends AbstractFrom<X, X> implements Root<X> {

	private final EntityTypeImpl<X> entity;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public RootImpl(EntityTypeImpl<X> entity) {
		super(entity);

		this.entity = entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		super.generateJpqlSelect(query, selected);

		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.getAlias();
		}

		return this.getModel().getName();
	}

	/**
	 * Returns the generated from SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated from SQL fragment
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public String generateSqlFrom(BaseQueryImpl<?> query) {
		final EntityTable primaryTable = this.entity.getRootType().getPrimaryTable();

		if (query.isQuery()) {
			return primaryTable.getQName() + " " + this.getFetchRoot().getTableAlias(query, primaryTable);
		}

		return primaryTable.getQName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <C, Y> Mapping<? super X, C, Y> getMapping(String name) {
		final Mapping<? super X, ?, ?> child = this.entity.getRootMapping().getChild(name);

		if (child == null) {
			throw this.cannotDereference(name);
		}

		return (Mapping<? super X, C, Y>) child;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityType<X> getModel() {
		return this.entity;
	}
}

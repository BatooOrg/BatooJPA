/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.path.ParentPath;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.jdbc.DiscriminatorColumn;

/**
 * The type expression for entities.
 * 
 * @param <T>
 *            the type of the expression
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class EntityTypeExpression<T> extends AbstractTypeExpression<T> {

	private final DiscriminatorColumn discriminatorColumn;
	private EntityTypeImpl<?> entity;

	/**
	 * @param path
	 *            the from
	 * @param discriminatorColumn
	 *            the discriminator column
	 * 
	 * @since 2.0.0
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
		if (this.entity == null) {
			this.entity = session.getEntityManager().getMetamodel().entity(this.getPath().getJavaType()).getRootType();
		}

		final String discriminatorValue = this.handle(row).toString();
		return (Class<? extends T>) this.entity.getChildType(discriminatorValue).getJavaType();
	}
}

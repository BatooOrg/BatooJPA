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

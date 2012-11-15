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
package org.batoo.jpa.core.impl.criteria.path;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;

/**
 * Physical Attribute implementation of {@link Path}.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public class BasicPath<X> extends AbstractPath<X> {

	private final BasicMapping<?, X> mapping;
	private String fieldAlias;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the physical mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicPath(ParentPath<?, ?> parent, BasicMapping<?, X> mapping) {
		super(parent, mapping.getJavaType());

		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlRestriction(query));

		builder.append(".").append(this.mapping.getAttribute().getName());

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean seleselectedcted) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlSelect(query, false));

		builder.append(".").append(this.mapping.getAttribute().getName());
		if (StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final BasicColumn column = this.mapping.getColumn();

		final Joinable rootPath = this.getRootPath();
		final String tableAlias = rootPath.getTableAlias(query, column.getTable());

		this.fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);

		if (selected) {
			return tableAlias + "." + column.getName() + " AS " + this.fieldAlias;
		}
		else {
			return tableAlias + "." + column.getName();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicAttribute<?, X> getModel() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final BasicColumn column = this.mapping.getColumn();
		if (query.isQuery()) {
			String columnAlias = this.getParentPath().getColumnAlias(query, column);
			if (columnAlias == null) {
				// force join
				this.getParentPath().getFetchRoot();

				columnAlias = this.getRootPath().getTableAlias(query, column.getTable()) + "." + column.getName();
			}

			return new String[] { columnAlias };
		}

		return new String[] { column.getName() };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final X value = (X) this.mapping.getColumn().convertValue(row.getStatement().getConnection(), row.getObject(this.fieldAlias));

		return (X) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		return new StaticTypeExpression<X>(this, this.mapping.getJavaType());
	}
}

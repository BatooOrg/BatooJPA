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

import org.batoo.jpa.core.impl.criteria.expression.AbstractParameterExpressionImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * 
 * @param <T>
 *            the type of the sub query.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SubQueryStub<T> extends AbstractCriteriaQueryImpl<T> {

	private final BaseQueryImpl<?> parent;

	/**
	 * @param parent
	 *            the parent query
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since 2.0.0
	 */
	public SubQueryStub(BaseQueryImpl<?> parent, MetamodelImpl metamodel, Class<T> resultType) {
		super(metamodel, resultType);

		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateTableAlias(boolean entity) {
		return this.parent.generateTableAlias(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getAlias(AbstractParameterExpressionImpl<?> parameter) {
		return this.parent.getAlias(parameter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias(AbstractSelection<?> selection) {
		return this.parent.getAlias(selection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getFieldAlias(String tableAlias, AbstractColumn column) {
		return this.parent.getFieldAlias(tableAlias, column);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isQuery() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int setNextSqlParam(AbstractParameterExpressionImpl<?> parameter) {
		return this.parent.setNextSqlParam(parameter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getJpql();
	}
}

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

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.ParameterExpression;

import org.batoo.jpa.core.impl.criteria.expression.AbstractParameterExpressionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;

/**
 * 
 * @param <T>
 *            type of the query
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface BaseQuery<T> {

	/**
	 * Generates the JPQL for the query.
	 * 
	 * @return the generated JPQL
	 * 
	 * @since 2.0.0
	 */
	String generateJpql();

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since 2.0.0
	 */
	String generateSql();

	/**
	 * Returns the generated entity alias.
	 * 
	 * @param entity
	 *            true if the table is an entity table, false for element collections
	 * @return the generated entity alias
	 * 
	 * @since 2.0.0
	 */
	String generateTableAlias(boolean entity);

	/**
	 * Returns the generated alias for the parameter.
	 * 
	 * @param parameter
	 *            the parameter
	 * @return the alias
	 * 
	 * @since 2.0.0
	 */
	Integer getAlias(AbstractParameterExpressionImpl<?> parameter);

	/**
	 * Returns the generated alias for the selection.
	 * 
	 * @param selection
	 *            the selection
	 * @return the alias
	 * 
	 * @since 2.0.0
	 */
	String getAlias(AbstractSelection<?> selection);

	/**
	 * @param tableAlias
	 *            the alias of the table
	 * @param column
	 *            the column
	 * @return the field alias
	 * 
	 * @since 2.0.0
	 */
	String getFieldAlias(String tableAlias, AbstractColumn column);

	/**
	 * Returns the JDBC Adaptor.
	 * 
	 * @return the JDBC Adaptor
	 * 
	 * @since 2.0.0
	 */
	JdbcAdaptor getJdbcAdaptor();

	/**
	 * Returns the JPQL for the query.
	 * 
	 * @return the the JPQL
	 * 
	 * @since 2.0.0
	 */
	String getJpql();

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since 2.0.0
	 */
	MetamodelImpl getMetamodel();

	/**
	 * Returns the parameter at position.
	 * 
	 * @param position
	 *            the position
	 * @return the parameter at position
	 * 
	 * @since 2.0.0
	 */
	AbstractParameterExpressionImpl<?> getParameter(int position);

	/**
	 * Returns the parameters of the query. Returns empty set if there are no parameters.
	 * <p>
	 * Modifications to the set do not affect the query.
	 * 
	 * @return the query parameters
	 * 
	 * @since 2.0.0
	 */
	Set<ParameterExpression<?>> getParameters();

	/**
	 * Returns the SQL for the query.
	 * 
	 * @return the the SQL
	 * 
	 * @since 2.0.0
	 */
	String getSql();

	/**
	 * Returns the SQL parameters of the query.
	 * 
	 * @return the SQL Parameters of the query
	 * 
	 * @since 2.0.0
	 */
	List<AbstractParameterExpressionImpl<?>> getSqlParameters();

	/**
	 * Returns if the query is internal.
	 * 
	 * @return true if the query is internal, false otherwise
	 * 
	 * @since 2.0.0
	 */
	boolean isInternal();

	/**
	 * Adds the parameter to the SQL parameters queue.
	 * 
	 * @param parameter
	 *            the parameter to add
	 * @return the positional number of the parameter
	 * 
	 * @since 2.0.0
	 */
	int setNextSqlParam(AbstractParameterExpressionImpl<?> parameter);

	/**
	 * Create a subquery of the query.
	 * 
	 * @param type
	 *            the subquery result type
	 * @param <U>
	 *            The type of the represented object
	 * @return subquery
	 * 
	 * @since 2.0.0
	 */
	<U> SubqueryImpl<U> subquery(Class<U> type);
}

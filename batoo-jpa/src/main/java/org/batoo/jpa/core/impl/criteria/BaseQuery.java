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

import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * 
 * @param <T>
 *            type of the query
 * 
 * @author hceylan
 * @since $version
 */
public interface BaseQuery<T> {

	/**
	 * Generates the JPQL for the query.
	 * 
	 * @return the generated JPQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String generateJpql();

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String generateSql();

	/**
	 * Returns the generated entity alias.
	 * 
	 * @param entity
	 *            true if the table is an entity table, false for element collections
	 * @return the generated entity alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String generateTableAlias(boolean entity);

	/**
	 * Returns the generated alias for the selection.
	 * 
	 * @param selection
	 *            the selection
	 * @return the alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getAlias(AbstractSelection<?> selection);

	/**
	 * Returns the generated alias for the parameter.
	 * 
	 * @param parameter
	 *            the parameter
	 * @return the alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Integer getAlias(ParameterExpressionImpl<?> parameter);

	/**
	 * @param tableAlias
	 *            the alias of the table
	 * @param column
	 *            the column
	 * @return the field alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getFieldAlias(String tableAlias, AbstractColumn column);

	/**
	 * Returns the JPQL for the query.
	 * 
	 * @return the the JPQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getJpql();

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since $version
	 */
	MetamodelImpl getMetamodel();

	/**
	 * Returns the parameter at position.
	 * 
	 * @param position
	 *            the position
	 * @return the parameter at position
	 * 
	 * @since $version
	 * @author hceylan
	 */
	ParameterExpressionImpl<?> getParameter(int position);

	/**
	 * Returns the parameters of the query. Returns empty set if there are no parameters.
	 * <p>
	 * Modifications to the set do not affect the query.
	 * 
	 * @return the query parameters
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Set<ParameterExpression<?>> getParameters();

	/**
	 * Returns the SQL for the query.
	 * 
	 * @return the the SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getSql();

	/**
	 * Returns the SQL parameters of the query.
	 * 
	 * @return the SQL Parameters of the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<ParameterExpressionImpl<?>> getSqlParameters();

	/**
	 * Adds the parameter to the SQL parameters queue.
	 * 
	 * @param parameter
	 *            the parameter to add
	 * @return the positional number of the parameter
	 * 
	 * @since $version
	 * @author hceylan
	 */
	int setNextSqlParam(ParameterExpressionImpl<?> parameter);

	/**
	 * Create a subquery of the query.
	 * 
	 * @param type
	 *            the subquery result type
	 * @param <U>
	 *            The type of the represented object
	 * @return subquery
	 * 
	 * @since $version
	 * @author hceylan
	 */
	<U> SubqueryImpl<U> subquery(Class<U> type);

}

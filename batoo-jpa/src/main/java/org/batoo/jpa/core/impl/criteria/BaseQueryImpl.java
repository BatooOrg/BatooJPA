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

package org.batoo.jpa.core.impl.criteria;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Selection;

import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.core.impl.criteria.expression.AbstractParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Commons super class for criteria queries.
 * 
 * @param <T>
 *            the type of the query
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class BaseQueryImpl<T> implements BaseQuery<T> {

	private final MetamodelImpl metamodel;

	private int nextEntityAlias;
	private int nextSelection;
	private int nextparam;

	private final HashMap<Selection<?>, String> selections = Maps.newHashMap();
	private final HashBiMap<AbstractParameterExpressionImpl<?>, Integer> parameters = HashBiMap.create();
	private final HashMap<String, List<AbstractColumn>> fields = Maps.newHashMap();

	private FinalWrapper<String> sql;
	private FinalWrapper<String> jpql;

	private final List<AbstractParameterExpressionImpl<?>> sqlParameters = Lists.newArrayList();

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since 2.0.0
	 */
	public BaseQueryImpl(MetamodelImpl metamodel) {
		this.metamodel = metamodel;
	}

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param qlString
	 *            the JPQL query
	 * 
	 * @since 2.0.0
	 */
	public BaseQueryImpl(MetamodelImpl metamodel, String qlString) {
		this.metamodel = metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateTableAlias(boolean entity) {
		return "E" + (!entity ? "C" : "") + this.nextEntityAlias++;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getAlias(AbstractParameterExpressionImpl<?> parameter) {
		Integer alias = this.parameters.get(parameter);
		if (alias == null) {
			if (parameter.getAlias() == null) {
				alias = ++this.nextparam;
			}
			else {
				try {
					alias = Integer.parseInt(parameter.getAlias());
				}
				catch (final Exception e) {
					alias = ++this.nextparam;
				}
			}

			this.parameters.put(parameter, alias);
		}

		return alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias(AbstractSelection<?> selection) {
		String alias = this.selections.get(selection);
		if (alias == null) {
			alias = "S" + this.nextSelection++;
			this.selections.put(selection, alias);
		}

		return alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getFieldAlias(String tableAlias, AbstractColumn column) {
		List<AbstractColumn> fields = this.fields.get(tableAlias);
		if (fields == null) {
			fields = Lists.newArrayList();
			this.fields.put(tableAlias, fields);
		}

		final int i = fields.indexOf(column);
		if (i >= 0) {
			return Integer.toString(i);
		}

		fields.add(column);
		return Integer.toString(fields.size() - 1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JdbcAdaptor getJdbcAdaptor() {
		return this.metamodel.getJdbcAdaptor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getJpql() {
		FinalWrapper<String> wrapper = this.jpql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.jpql == null) {
					this.jpql = new FinalWrapper<String>(this.generateJpql());
				}

				wrapper = this.jpql;
			}
		}

		return wrapper.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractParameterExpressionImpl<?> getParameter(int position) {
		return this.parameters.inverse().get(position);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ParameterExpression<?>> getParameters() {
		final Set<ParameterExpression<?>> parameters = Sets.newHashSet();
		for (final AbstractParameterExpressionImpl<?> parameter : this.parameters.keySet()) {
			if (parameter instanceof ParameterExpressionImpl) {
				parameters.add((ParameterExpression<?>) parameter);
			}
		}

		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSql() {
		FinalWrapper<String> wrapper = this.sql;

		if (wrapper == null) {
			synchronized (this) {
				if (this.sql == null) {
					try {
						this.sql = new FinalWrapper<String>(this.generateSql());
					}
					catch (final Exception e) {
						String jpql = null;

						try {
							jpql = this.getJpql();

							throw new PersistenceException("Cannot generate query for: " + jpql, e);
						}
						catch (final Exception e2) {}

						if (e instanceof RuntimeException) {
							throw (RuntimeException) e;
						}

						throw new PersistenceException("Cannot generate SQL for query", e);
					}
				}

				wrapper = this.sql;
			}
		}

		return wrapper.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AbstractParameterExpressionImpl<?>> getSqlParameters() {
		return this.sqlParameters;
	}

	/**
	 * Returns if the query is a select query.
	 * 
	 * @return true if the query is a select query, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public abstract boolean isQuery();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int setNextSqlParam(AbstractParameterExpressionImpl<?> parameter) {
		if (parameter instanceof ParameterExpressionImpl) {
			final Integer position = ((ParameterExpressionImpl<?>) parameter).getPosition();
			if (position != null) {
				this.sqlParameters.add(parameter);

				return position;
			}
		}

		this.sqlParameters.add(parameter);

		return this.sqlParameters.size() - 1;
	}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.criteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.BatooUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * the definition of the functionality that is common to both top-level queries and subqueries.
 * <p>
 * It is not intended to be used directly in query construction.
 * 
 * <p>
 * All queries must have a set of root entities (which may in turn own joins).
 * <p>
 * All queries may have a conjunction of restrictions.
 * 
 * @param <T>
 *            the type of the result
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("unchecked")
public abstract class AbstractQueryImpl<T> implements AbstractQuery<T> {

	private static final BLogger LOG = BLoggerFactory.getLogger(AbstractQueryImpl.class);

	private final MetamodelImpl metamodel;
	private Class<T> resultType;
	private boolean internal;
	private final HashMap<String, List<AbstractColumn>> fields = Maps.newHashMap();
	private final Set<RootImpl<?>> roots = Sets.newHashSet();
	private int nextEntityAlias;
	private final List<ParameterExpressionImpl<?>> sqlParameters = Lists.newArrayList();
	private final HashMap<Selection<?>, String> selections = Maps.newHashMap();
	private AbstractSelection<T> selection;
	private PredicateImpl restriction;
	private PredicateImpl groupRestriction;
	private boolean distinct;
	private final List<AbstractExpression<?>> groupList = Lists.newArrayList();
	private String sql;
	private String jpql;
	private int nextSelection;

	private int nextparam;
	private final HashBiMap<ParameterExpressionImpl<?>, Integer> parameters = HashBiMap.create();
	private final List<ParameterExpressionImpl<?>> parameterOrder = Lists.newArrayList();

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractQueryImpl(MetamodelImpl metamodel, Class<T> resultType) {
		super();

		this.metamodel = metamodel;
		this.resultType = resultType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> distinct(boolean distinct) {
		this.distinct = distinct;

		return this;
	}

	/**
	 * Ensures that there is a valid selection.
	 * 
	 * @return the selection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AbstractSelection<T> ensureSelection() {
		if (this.selection == null) {
			if (this.getRoots().size() == 1) {
				return this.selection = (AbstractSelection<T>) this.getRoots().iterator().next();
			}
			else {
				throw new PersistenceException("Selection is not specified");
			}
		}

		return this.selection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> RootImpl<X> from(Class<X> entityClass) {
		final EntityTypeImpl<X> entity = this.getMetamodel().entity(entityClass);

		return this.from(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> RootImpl<X> from(EntityType<X> entity) {
		final RootImpl<X> r = new RootImpl<X>((EntityTypeImpl<X>) entity);

		this.roots.add(r);

		return r;
	}

	/**
	 * Generates the JPQL for the query.
	 * 
	 * @return the generated JPQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String generateJpql() {
		final StringBuilder builder = new StringBuilder();

		this.ensureSelection();

		builder.append("select ");

		// append distinct if necessary * @param selected

		if (this.isDistinct()) {
			builder.append("distinct ");
		}

		builder.append(this.selection.generateJpqlSelect(this, true));

		final Collection<String> roots = Collections2.transform(this.getRoots(), new Function<Root<?>, String>() {

			@Override
			public String apply(Root<?> input) {
				final RootImpl<?> root = (RootImpl<?>) input;

				final StringBuilder builder = new StringBuilder(input.getModel().getName());

				if (StringUtils.isNotBlank(input.getAlias())) {
					builder.append(" as ").append(input.getAlias());
				}

				final String joins = root.generateJpqlJoins(AbstractQueryImpl.this);

				if (StringUtils.isNotBlank(joins)) {
					builder.append("\n").append(BatooUtils.indent(joins));
				}
				return builder.toString();
			}
		});
		builder.append("\nfrom ").append(Joiner.on(", ").join(roots));

		if (this.getRestriction() != null) {
			builder.append("\nwhere\n\t").append(this.getRestriction().generateJpqlRestriction(this));
		}

		if (this.getGroupList().size() > 0) {
			final String groupBy = Joiner.on(", ").join(Lists.transform(this.getGroupList(), new Function<Expression<?>, String>() {

				@Override
				public String apply(Expression<?> input) {
					return ((AbstractExpression<?>) input).generateJpqlRestriction(AbstractQueryImpl.this);
				}
			}));

			builder.append("\ngroup by\n\t").append(groupBy);
		}

		if (this.getGroupRestriction() != null) {
			builder.append("\nhaving\n\t").append(this.getGroupRestriction().generateJpqlRestriction(this));
		}

		return builder.toString();
	}

	/**
	 * Returns the generated SQL.
	 * 
	 * @return the generated SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String generateSql() {
		this.ensureSelection();

		AbstractQueryImpl.LOG.debug("Preparing SQL for {0}", AbstractQueryImpl.LOG.lazyBoxed(this));

		final Map<Joinable, String> joins = Maps.newLinkedHashMap();

		// generate the select chunk
		final StringBuilder select = new StringBuilder();
		select.append("SELECT");
		if (this.isDistinct() && !this.internal) {
			select.append(" DISTINCT");
		}
		select.append("\n");
		select.append(BatooUtils.indent(this.selection.generateSqlSelect(this, true)));

		// generate from chunk
		final List<String> froms = Lists.newArrayList();
		for (final Root<?> r : this.getRoots()) {
			final RootImpl<?> root = (RootImpl<?>) r;
			froms.add(root.generateSqlFrom(this));
		}

		for (final Root<?> root : this.getRoots()) {
			((RootImpl<?>) root).generateSqlJoins(this, joins);
		}

		final String where = this.generateSqlRestriction();
		final String groupBy = this.getGroupList().size() == 0 ? null : Joiner.on(", ").join(
			Lists.transform(this.getGroupList(), new Function<Expression<?>, String>() {

				@Override
				public String apply(Expression<?> input) {
					return ((AbstractExpression<?>) input).generateSqlSelect(AbstractQueryImpl.this, false);
				}
			}));

		final String having = this.getGroupRestriction() != null ? this.getGroupRestriction().generateSqlRestriction(this) : null;

		final String from = "FROM " + Joiner.on(",").join(froms);
		final String join = Joiner.on("\n").skipNulls().join(joins.values());

		return Joiner.on("\n").skipNulls().join(//
			select, //
			from, //
			StringUtils.isBlank(join) ? null : BatooUtils.indent(join), //
			StringUtils.isBlank(where) ? null : "WHERE\n\t" + where, //
			StringUtils.isBlank(groupBy) ? null : "GROUP BY\n\t" + groupBy, //
			StringUtils.isBlank(having) ? null : "HAVING\n\t" + having);
	}

	/**
	 * Returns the restriction for the query.
	 * 
	 * @return the restriction
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private String generateSqlRestriction() {
		final String[] restrictions = new String[this.getRoots().size() + 1];

		if (this.getRestriction() != null) {
			restrictions[0] = this.getRestriction().generateSqlRestriction(this);
		}

		int i = 0;
		final Iterator<Root<?>> j = this.getRoots().iterator();
		while (j.hasNext()) {
			final Root<?> root = j.next();
			restrictions[++i] = ((RootImpl<?>) root).generateDiscrimination();

		}

		return Joiner.on(" AND ").skipNulls().join(restrictions);
	}

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
	public String generateTableAlias(boolean entity) {
		return "E" + (!entity ? "C" : "") + this.nextEntityAlias++;
	}

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
	public String getAlias(AbstractSelection<?> selection) {
		String alias = this.selections.get(selection);
		if (alias == null) {
			alias = "S" + this.nextSelection++;
			this.selections.put(selection, alias);
		}

		return alias;
	}

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
	public Integer getAlias(ParameterExpressionImpl<?> parameter) {
		Integer alias = this.parameters.get(parameter);
		if (alias == null) {
			alias = this.nextparam++;
			this.parameters.put(parameter, alias);
		}

		return alias;
	}

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
	public List<Expression<?>> getGroupList() {
		final List<Expression<?>> groupList = Lists.newArrayList();
		groupList.addAll(this.groupList);

		return groupList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl getGroupRestriction() {
		return this.groupRestriction;
	}

	/**
	 * Returns the JPQL for the query.
	 * 
	 * @return the the JPQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
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
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since $version
	 */
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

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
	public ParameterExpressionImpl<?> getParameter(int position) {
		return this.parameters.inverse().get(position);
	}

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
	public Set<ParameterExpression<?>> getParameters() {
		final Set<ParameterExpression<?>> parameters = Sets.newHashSet();
		parameters.addAll(this.parameters.keySet());

		return parameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl getRestriction() {
		return this.restriction;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<T> getResultType() {
		return this.resultType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Root<?>> getRoots() {
		return Sets.<Root<?>> newHashSet(this.roots);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractSelection<T> getSelection() {
		return this.ensureSelection();
	}

	/**
	 * Returns the SQL for the query.
	 * 
	 * @return the the SQL
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSql() {
		if (this.sql != null) {
			return this.sql;
		}

		synchronized (this) {
			if (this.sql != null) {
				return this.sql;
			}

			return this.sql = this.generateSql();
		}
	}

	/**
	 * Returns the SQL parameters of the query.
	 * 
	 * @return the SQL Parameters of the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<ParameterExpressionImpl<?>> getSqlParameters() {
		return this.sqlParameters;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> groupBy(Expression<?>... grouping) {
		this.groupList.clear();

		for (final Expression<?> expression : grouping) {
			this.groupList.add((AbstractExpression<?>) expression);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> groupBy(List<Expression<?>> grouping) {
		this.groupList.clear();

		for (final Expression<?> expression : grouping) {
			this.groupList.add((AbstractExpression<?>) expression);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> having(Expression<Boolean> restriction) {
		if (restriction instanceof PredicateImpl) {
			this.groupRestriction = (PredicateImpl) restriction;
		}
		else {
			this.groupRestriction = new PredicateImpl((AbstractExpression<Boolean>) restriction);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> having(Predicate... restrictions) {
		this.groupRestriction = new PredicateImpl(false, BooleanOperator.AND, restrictions);

		return this;
	}

	/**
	 * Marks the query as internal entity query.
	 * 
	 * @return self
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractQueryImpl<T> internal() {
		this.internal = true;
		this.distinct(true);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isDistinct() {
		return this.distinct;
	}

	/**
	 * Registers the parameter as the nex SQL parameter
	 * 
	 * @param parameter
	 *            the parameter to register
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void registerParameter(ParameterExpressionImpl<?> parameter) {
		this.parameterOrder.add(parameter);
	}

	/**
	 * Sets the selection
	 * 
	 * @param selection
	 *            the selection
	 * @return the modified query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AbstractQueryImpl<T> select(Selection<? extends T> selection) {
		this.selection = (AbstractSelection<T>) selection;

		return this;
	}

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
	public int setNextSqlParam(ParameterExpressionImpl<?> parameter) {
		this.sqlParameters.add(parameter);

		return this.sqlParameters.size() - 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <U> SubqueryImpl<U> subquery(Class<U> type) {
		return new SubqueryImpl<U>(this.metamodel, this, type);
	}

	/**
	 * @param selections
	 *            the selections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void updateResultClass(List<Selection<?>> selections) {
		if (selections.size() == 1) {
			final Selection<?> selection = selections.get(0);
			this.resultType = (Class<T>) selection.getJavaType();
		}
		else {
			this.resultType = (Class<T>) Object[].class;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> where(Expression<Boolean> restriction) {
		if (restriction instanceof PredicateImpl) {
			this.restriction = (PredicateImpl) restriction;
		}
		else {
			this.restriction = new PredicateImpl((AbstractExpression<Boolean>) restriction);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<T> where(Predicate... restrictions) {
		this.restriction = new PredicateImpl(false, BooleanOperator.AND, restrictions);

		return this;
	}
}

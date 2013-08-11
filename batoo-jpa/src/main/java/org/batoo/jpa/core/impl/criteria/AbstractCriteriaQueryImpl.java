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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
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
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCriteriaQueryImpl<T> extends BaseQueryImpl<T> implements AbstractQuery<T> {

	private static final BLogger LOG = BLoggerFactory.getLogger(AbstractCriteriaQueryImpl.class);

	private Class<T> resultType;
	private boolean internal;
	private final Set<RootImpl<?>> roots = Sets.newHashSet();

	private AbstractSelection<T> selection;
	private PredicateImpl restriction;
	private PredicateImpl groupRestriction;
	private boolean distinct;
	private final List<AbstractExpression<?>> groupList = Lists.newArrayList();

	private final List<ParameterExpressionImpl<?>> parameterOrder = Lists.newArrayList();

	/**
	 * 
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since 2.0.1
	 */
	public AbstractCriteriaQueryImpl(MetamodelImpl metamodel) {
		super(metamodel);
	}

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param resultType
	 *            the result type
	 * 
	 * @since 2.0.0
	 */
	public AbstractCriteriaQueryImpl(MetamodelImpl metamodel, Class<T> resultType) {
		super(metamodel);

		this.resultType = resultType;
	}

	/**
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param qlString
	 *            the JPQL query
	 * 
	 * @since 2.0.1
	 */
	public AbstractCriteriaQueryImpl(MetamodelImpl metamodel, String qlString) {
		super(metamodel, qlString);
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
	 * @since 2.0.0
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpql() {
		final StringBuilder builder = new StringBuilder();

		this.ensureSelection();

		builder.append("select ");

		// append distinct if necessary
		if (this.distinct) {
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

				final String joins = root.generateJpqlJoins(AbstractCriteriaQueryImpl.this);

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
					return ((AbstractExpression<?>) input).generateJpqlRestriction(AbstractCriteriaQueryImpl.this);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSql() {
		this.ensureSelection();

		AbstractCriteriaQueryImpl.LOG.debug("Preparing SQL for {0}", AbstractCriteriaQueryImpl.LOG.lazyBoxed(this));

		final Map<Joinable, String> joins = Maps.newLinkedHashMap();

		// generate the select chunk
		final StringBuilder select = new StringBuilder();
		select.append("SELECT");
		if (this.distinct && !this.internal) {
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

		final String where = this.generateSqlRestriction();
		final String groupBy = this.getGroupList().size() == 0 ? null : Joiner.on(", ").join(
			Lists.transform(this.getGroupList(), new Function<Expression<?>, String>() {

				@Override
				public String apply(Expression<?> input) {
					return ((AbstractExpression<?>) input).generateSqlSelect(AbstractCriteriaQueryImpl.this, false);
				}
			}));

		final String having = this.getGroupRestriction() != null ? this.getGroupRestriction().generateSqlRestriction(this) : null;

		for (final Root<?> root : this.getRoots()) {
			((RootImpl<?>) root).generateSqlJoins(this, joins);
		}

		final String from = "FROM " + Joiner.on(", ").join(froms);
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
	 * @since 2.0.0
	 */
	private String generateSqlRestriction() {
		final String[] restrictions = new String[this.getRoots().size() + 1];

		if (this.getRestriction() != null) {
			restrictions[0] = this.restriction.generateSqlRestriction(this);
		}

		int i = 0;
		final Iterator<Root<?>> j = this.getRoots().iterator();
		while (j.hasNext()) {
			final Root<?> root = j.next();
			restrictions[++i] = ((RootImpl<?>) root).generateDiscrimination(false);

		}

		final String restriction = Joiner.on(") AND (").skipNulls().join(restrictions);
		if (StringUtils.isBlank(restriction)) {
			return null;
		}

		return "(" + restriction + ")";
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

		for (int i = 0; i < grouping.size(); i++) {
			this.groupList.add((AbstractExpression<?>) grouping.get(i));
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
	 * @since 2.0.0
	 */
	public AbstractCriteriaQueryImpl<T> internal() {
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isInternal() {
		return this.internal;
	}

	/**
	 * Registers the parameter as the nex SQL parameter
	 * 
	 * @param parameter
	 *            the parameter to register
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	protected AbstractCriteriaQueryImpl<T> select(Selection<? extends T> selection) {
		this.selection = (AbstractSelection<T>) selection;

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <U> SubqueryImpl<U> subquery(Class<U> type) {
		return new SubqueryImpl<U>(this.getMetamodel(), this, type);
	}

	/**
	 * @param selections
	 *            the selections
	 * 
	 * @since 2.0.0
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
			final PredicateImpl predicate = (PredicateImpl) restriction;
			if (predicate.getExpressions().size() > 0) {
				this.restriction = predicate;
			}
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

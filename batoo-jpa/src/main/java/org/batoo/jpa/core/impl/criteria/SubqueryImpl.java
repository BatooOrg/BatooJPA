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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;

import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.AbstractParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.adapter.JdbcAdaptor;

import com.google.common.collect.Sets;

/**
 * The implementation of sub query.
 * 
 * @param <T>
 *            the type of the sub query.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SubqueryImpl<T> extends AbstractExpression<T> implements Subquery<T>, BaseQuery<T> {

	private final SubQueryStub<T> query;
	private final BaseQueryImpl<?> parent;
	private final Set<AbstractFrom<?, ?>> correlatedJoins = Sets.newHashSet();

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent query
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	public SubqueryImpl(MetamodelImpl metamodel, BaseQueryImpl<?> parent, Class<T> javaType) {
		super(javaType);

		this.parent = parent;
		this.query = new SubQueryStub<T>(parent, metamodel, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> CollectionJoin<X, Y> correlate(CollectionJoin<X, Y> parentCollection) {
		if (!this.correlatedJoins.contains(parentCollection)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentCollection);
		}

		return parentCollection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> Join<X, Y> correlate(Join<X, Y> parentJoin) {
		if (!this.correlatedJoins.contains(parentJoin)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentJoin);
		}

		return parentJoin;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> parentList) {
		if (!this.correlatedJoins.contains(parentList)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentList);
		}

		return parentList;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> parentMap) {
		if (!this.correlatedJoins.contains(parentMap)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentMap);
		}

		return parentMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Root<Y> correlate(Root<Y> parentRoot) {
		if (!this.correlatedJoins.contains(parentRoot)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentRoot);
		}

		return parentRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> parentSet) {
		if (!this.correlatedJoins.contains(parentSet)) {
			this.correlatedJoins.add((AbstractFrom<?, ?>) parentSet);
		}

		return parentSet;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> distinct(boolean distinct) {
		this.query.distinct(distinct);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> Root<X> from(Class<X> entityClass) {
		return this.query.from(entityClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X> RootImpl<X> from(EntityType<X> entity) {
		return this.query.from(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpql() {
		return this.query.generateJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return "(\n" + BatooUtils.indent(BatooUtils.indent(this.query.generateJpql())) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSql() {
		return this.query.generateSql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return null; // N/A;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateTableAlias(boolean entity) {
		return this.query.generateTableAlias(entity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Integer getAlias(AbstractParameterExpressionImpl<?> parameter) {
		return this.query.getAlias(parameter);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getAlias(AbstractSelection<?> selection) {
		return this.query.getAlias(selection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Join<?, ?>> getCorrelatedJoins() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getFieldAlias(String tableAlias, AbstractColumn column) {
		return this.query.getFieldAlias(tableAlias, column);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<Expression<?>> getGroupList() {
		return this.query.getGroupList();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate getGroupRestriction() {
		return this.query.getGroupRestriction();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JdbcAdaptor getJdbcAdaptor() {
		return this.parent.getJdbcAdaptor();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getJpql() {
		return this.query.getJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MetamodelImpl getMetamodel() {
		return this.query.getMetamodel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractParameterExpressionImpl<?> getParameter(int position) {
		return this.query.getParameter(position);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ParameterExpression<?>> getParameters() {
		return this.query.getParameters();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractQuery<?> getParent() {
		return (AbstractQuery<?>) this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate getRestriction() {
		return this.query.getRestriction();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<T> getResultType() {
		return this.query.getResultType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Root<?>> getRoots() {
		return this.query.getRoots();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Expression<T> getSelection() {
		return (Expression<T>) this.query.getSelection();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getSql() {
		return this.query.getSql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AbstractParameterExpressionImpl<?>> getSqlParameters() {
		return this.query.getSqlParameters();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return new String[] { "(\n" + BatooUtils.indent(BatooUtils.indent(this.query.getSql())) + ")" };
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> groupBy(Expression<?>... grouping) {
		this.query.groupBy(grouping);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> groupBy(List<Expression<?>> grouping) {
		this.query.groupBy(grouping);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return null; // N/A
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> having(Expression<Boolean> restriction) {
		this.query.having(restriction);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SubqueryImpl<T> having(Predicate... restrictions) {
		this.query.having(restrictions);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isDistinct() {
		return this.query.isDistinct();
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
	public Subquery<T> select(Expression<T> expression) {
		this.query.select(expression);

		return this;
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
	public <U> SubqueryImpl<U> subquery(Class<U> type) {
		return this.query.subquery(type);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.query.getJpql();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Subquery<T> where(Expression<Boolean> restriction) {
		this.query.where(restriction);

		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Subquery<T> where(Predicate... restrictions) {
		this.query.where(restrictions);

		return this;
	}
}

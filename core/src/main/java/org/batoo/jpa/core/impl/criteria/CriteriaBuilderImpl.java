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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ArithmeticExression;
import org.batoo.jpa.core.impl.criteria.expression.ArithmeticExression.ArithmeticOperation;
import org.batoo.jpa.core.impl.criteria.expression.Comparison;
import org.batoo.jpa.core.impl.criteria.expression.ComparisonExpression;
import org.batoo.jpa.core.impl.criteria.expression.ConstantExpression;
import org.batoo.jpa.core.impl.criteria.expression.ExpressionConverter;
import org.batoo.jpa.core.impl.criteria.expression.NegationExpression;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * Used to construct criteria queries, compound selections, expressions, predicates, orderings.
 * 
 * <p>
 * Note that <code>Predicate</code> is used instead of <code>Expression&#060;Boolean&#062;</code> in this API in order to work around the
 * fact that Java generics are not compatible with varags.
 * 
 * @author hceylan
 * @since $version
 */
public class CriteriaBuilderImpl implements CriteriaBuilder {

	private final MetamodelImpl metamodel;

	/**
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaBuilderImpl(MetamodelImpl metamodel) {
		this.metamodel = metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> abs(Expression<N> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> all(Subquery<Y> subquery) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate and(Expression<Boolean> x, Expression<Boolean> y) {
		return new PredicateImpl(false, BooleanOperator.AND, x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate and(Predicate... restrictions) {
		return new PredicateImpl(false, BooleanOperator.AND, restrictions);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> any(Subquery<Y> subquery) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CompoundSelection<Object[]> array(Selection<?>... selections) {
		return new CompoundSelectionImpl<Object[]>(Object[].class, selections);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Order asc(Expression<?> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<Double> avg(Expression<N> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> v, Expression<? extends Y> x, Expression<? extends Y> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.BETWEEN, v, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> v, Y x, Y y) {
		final TypeImpl<Y> type = (TypeImpl<Y>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.BETWEEN, v, new ConstantExpression<Y>(type, x), new ConstantExpression<Y>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Coalesce<T> coalesce() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> concat(Expression<String> x, Expression<String> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> concat(Expression<String> x, String y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> concat(String x, Expression<String> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate conjunction() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> CompoundSelectionImpl<Y> construct(Class<Y> resultClass, Selection<?>... selections) {
		return new CompoundSelectionImpl<Y>(resultClass, selections);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Long> count(Expression<?> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Long> countDistinct(Expression<?> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> CriteriaDelete<T> createCriteriaDelete(Class<T> targetEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> CriteriaUpdate<T> createCriteriaUpdate(Class<T> targetEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<Object> createQuery() {
		return new CriteriaQueryImpl<Object>(this.metamodel, Object.class);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> CriteriaQueryImpl<T> createQuery(Class<T> resultClass) {
		return new CriteriaQueryImpl<T>(this.metamodel, resultClass);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaQuery<Tuple> createTupleQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Date> currentDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Time> currentTime() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Timestamp> currentTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Order desc(Expression<?> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> diff(Expression<? extends N> x, Expression<? extends N> y) {
		return new ArithmeticExression<N>(ArithmeticOperation.SUBTRACT, x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Number> Expression<N> diff(Expression<? extends N> x, N y) {
		final TypeImpl<N> type = (TypeImpl<N>) this.metamodel.type(y.getClass());

		return new ArithmeticExression<N>(ArithmeticOperation.SUBTRACT, x, new ConstantExpression<N>(type, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Number> Expression<N> diff(N x, Expression<? extends N> y) {
		final TypeImpl<N> type = (TypeImpl<N>) this.metamodel.type(x.getClass());

		return new ArithmeticExression<N>(ArithmeticOperation.SUBTRACT, new ConstantExpression<N>(type, x), y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate disjunction() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl equal(Expression<?> x, Expression<?> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.EQUAL, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Predicate equal(Expression<?> x, Object y) {
		final TypeImpl<?> type = this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.EQUAL, x, new ConstantExpression(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate exists(Subquery<?> subquery) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Expression<T> function(String name, Class<T> type, Expression<?>... args) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER_OR_EQUAL, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate ge(Expression<? extends Number> x, Number y) {
		final TypeImpl<Number> type = (TypeImpl<Number>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER_OR_EQUAL, x, new ConstantExpression<Number>(type, y)));
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y extends Comparable<? super Y>> PredicateImpl greaterThan(Expression<? extends Y> x, Expression<? extends Y> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y extends Comparable<? super Y>> PredicateImpl greaterThan(Expression<? extends Y> x, Y y) {
		final TypeImpl<Y> type = (TypeImpl<Y>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER, x, new ConstantExpression<Y>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y extends Comparable<? super Y>> PredicateImpl greaterThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER_OR_EQUAL, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y extends Comparable<? super Y>> PredicateImpl greaterThanOrEqualTo(Expression<? extends Y> x, Y y) {
		final TypeImpl<Y> type = (TypeImpl<Y>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER_OR_EQUAL, x, new ConstantExpression<Y>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X extends Comparable<? super X>> Expression<X> greatest(Expression<X> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl gt(Expression<? extends Number> x, Expression<? extends Number> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.GREATER, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PredicateImpl gt(Expression<? extends Number> x, Number y) {
		final TypeImpl<Number> type = (TypeImpl<Number>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LESS, x, new ConstantExpression<Number>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> In<T> in(Expression<? extends T> expression) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <C extends Collection<?>> Predicate isEmpty(Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isFalse(Expression<Boolean> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Predicate isMember(E elem, Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Predicate isMember(Expression<E> elem, Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Predicate isNotMember(E elem, Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> elem, Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isNotNull(Expression<?> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isNull(Expression<?> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate isTrue(Expression<Boolean> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M map) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl le(Expression<? extends Number> x, Expression<? extends Number> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.LESS_OR_EQUAL, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PredicateImpl le(Expression<? extends Number> x, Number y) {
		final TypeImpl<Number> type = (TypeImpl<Number>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LESS_OR_EQUAL, x, new ConstantExpression<Number>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X extends Comparable<? super X>> Expression<X> least(Expression<X> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> length(Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y extends Comparable<? super Y>> PredicateImpl lessThan(Expression<? extends Y> x, Expression<? extends Y> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.LESS, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y extends Comparable<? super Y>> PredicateImpl lessThan(Expression<? extends Y> x, Y y) {
		final TypeImpl<Y> type = (TypeImpl<Y>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LESS, x, new ConstantExpression<Y>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y extends Comparable<? super Y>> PredicateImpl lessThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.LESS_OR_EQUAL, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Y y) {
		final TypeImpl<Y> type = (TypeImpl<Y>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LESS_OR_EQUAL, x, new ConstantExpression<Y>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate like(Expression<String> x, Expression<String> pattern) {
		return new PredicateImpl(new ComparisonExpression(Comparison.LIKE, x, pattern));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate like(Expression<String> x, Expression<String> pattern, char escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate like(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate like(Expression<String> x, String pattern) {
		final TypeImpl<String> type = (TypeImpl<String>) this.metamodel.type(pattern.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LIKE, x, new ConstantExpression<String>(type, pattern)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate like(Expression<String> x, String pattern, char escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate like(Expression<String> x, String pattern, Expression<Character> escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Expression<T> literal(T value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> locate(Expression<String> x, Expression<String> pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> locate(Expression<String> x, Expression<String> pattern, Expression<Integer> from) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> locate(Expression<String> x, String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> locate(Expression<String> x, String pattern, int from) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> lower(Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.LESS, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate lt(Expression<? extends Number> x, Number y) {
		final TypeImpl<Number> type = (TypeImpl<Number>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LESS, x, new ConstantExpression<Number>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> max(Expression<N> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> min(Expression<N> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> mod(Expression<Integer> x, Expression<Integer> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> mod(Expression<Integer> x, Integer y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> mod(Integer x, Expression<Integer> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> neg(Expression<N> x) {
		return new NegationExpression<N>(x);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PredicateImpl not(Expression<Boolean> restriction) {
		return new PredicateImpl(true, BooleanOperator.AND, restriction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl notEqual(Expression<?> x, Expression<?> y) {
		return new PredicateImpl(new ComparisonExpression(Comparison.NOT_EQUAL, x, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PredicateImpl notEqual(Expression<?> x, Object y) {
		final TypeImpl<Object> type = (TypeImpl<Object>) this.metamodel.type(y.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.NOT_EQUAL, x, new ConstantExpression<Object>(type, y)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PredicateImpl notLike(Expression<String> x, Expression<String> pattern) {
		return new PredicateImpl(new ComparisonExpression(Comparison.LIKE, x, pattern)).not();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate notLike(Expression<String> x, Expression<String> pattern, char escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate notLike(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public PredicateImpl notLike(Expression<String> x, String pattern) {
		final TypeImpl<String> type = (TypeImpl<String>) this.metamodel.type(pattern.getClass());

		return new PredicateImpl(new ComparisonExpression(Comparison.LIKE, x, new ConstantExpression<String>(type, pattern))).not();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate notLike(Expression<String> x, String pattern, char escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate notLike(Expression<String> x, String pattern, Expression<Character> escapeChar) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> nullif(Expression<Y> x, Expression<?> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> nullif(Expression<Y> x, Y y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> Expression<T> nullLiteral(Class<T> resultClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Predicate or(Expression<Boolean> x, Expression<Boolean> y) {
		return new PredicateImpl(false, BooleanOperator.OR, x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Predicate or(Predicate... restrictions) {
		return new PredicateImpl(false, BooleanOperator.OR, restrictions);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> ParameterExpressionImpl<T> parameter(Class<T> paramClass) {
		return this.parameter(paramClass, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> ParameterExpressionImpl<T> parameter(Class<T> paramClass, String name) {
		return new ParameterExpressionImpl<T>(this.metamodel.type(paramClass), paramClass, name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> prod(Expression<? extends N> x, Expression<? extends N> y) {
		return new ArithmeticExression<N>(ArithmeticOperation.MULTIPLY, x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Number> Expression<N> prod(Expression<? extends N> x, N y) {
		final TypeImpl<N> type = (TypeImpl<N>) this.metamodel.type(y.getClass());

		return new ArithmeticExression<N>(ArithmeticOperation.MULTIPLY, x, new ConstantExpression<N>(type, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Number> Expression<N> prod(N x, Expression<? extends N> y) {
		final TypeImpl<N> type = (TypeImpl<N>) this.metamodel.type(x.getClass());

		return new ArithmeticExression<N>(ArithmeticOperation.MULTIPLY, new ConstantExpression<N>(type, x), y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Number> quot(Expression<? extends Number> x, Expression<? extends Number> y) {
		return new ArithmeticExression<Number>(ArithmeticOperation.DIVIDE, x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Expression<Number> quot(Expression<? extends Number> x, Number y) {
		final TypeImpl<Number> type = (TypeImpl<Number>) this.metamodel.type(y.getClass());

		return new ArithmeticExression<Number>(ArithmeticOperation.SUBTRACT, x, new ConstantExpression<Number>(type, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Number> quot(Number x, Expression<? extends Number> y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <R> Case<R> selectCase() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> expression) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <C extends Collection<?>> Expression<Integer> size(C collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <C extends Collection<?>> Expression<Integer> size(Expression<C> collection) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> Expression<Y> some(Subquery<Y> subquery) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Double> sqrt(Expression<? extends Number> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> substring(Expression<String> x, Expression<Integer> from) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> substring(Expression<String> x, Expression<Integer> from, Expression<Integer> len) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> substring(Expression<String> x, int from) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> substring(Expression<String> x, int from, int len) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> sum(Expression<? extends N> x, Expression<? extends N> y) {
		return new ArithmeticExression<N>(ArithmeticOperation.ADD, x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Number> Expression<N> sum(Expression<? extends N> x, N y) {
		final TypeImpl<N> type = (TypeImpl<N>) this.metamodel.type(y.getClass());

		return new ArithmeticExression<N>(ArithmeticOperation.ADD, x, new ConstantExpression<N>(type, y));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <N extends Number> Expression<N> sum(Expression<N> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Number> Expression<N> sum(N x, Expression<? extends N> y) {
		final TypeImpl<N> type = (TypeImpl<N>) this.metamodel.type(x.getClass());

		return new ArithmeticExression<N>(ArithmeticOperation.ADD, new ConstantExpression<N>(type, x), y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Double> sumAsDouble(Expression<Float> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Long> sumAsLong(Expression<Integer> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<BigDecimal> toBigDecimal(Expression<? extends Number> number) {
		return ((AbstractExpression<? extends Number>) number).setConverter(ExpressionConverter.BIG_DECIMAL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<BigInteger> toBigInteger(Expression<? extends Number> number) {
		return ((AbstractExpression<? extends Number>) number).setConverter(ExpressionConverter.BIG_INTEGER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Double> toDouble(Expression<? extends Number> number) {
		return ((AbstractExpression<? extends Number>) number).setConverter(ExpressionConverter.DOUBLE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Float> toFloat(Expression<? extends Number> number) {
		return ((AbstractExpression<? extends Number>) number).setConverter(ExpressionConverter.FLOAT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Integer> toInteger(Expression<? extends Number> number) {
		return ((AbstractExpression<? extends Number>) number).setConverter(ExpressionConverter.INTEGER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Long> toLong(Expression<? extends Number> number) {
		return ((AbstractExpression<? extends Number>) number).setConverter(ExpressionConverter.LONG);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> toString(Expression<Character> character) {
		return ((AbstractExpression<Character>) character).setConverter(ExpressionConverter.STRING);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, T, E extends T> CollectionJoin<X, E> treat(CollectionJoin<X, T> join, Class<E> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, T, V extends T> Join<X, V> treat(Join<X, T> join, Class<V> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, T, E extends T> ListJoin<X, E> treat(ListJoin<X, T> join, Class<E> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, K, T, V extends T> MapJoin<X, K, V> treat(MapJoin<X, K, T> join, Class<V> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, T extends X> Path<T> treat(Path<X> path, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, T extends X> Root<T> treat(Root<X> root, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <X, T, E extends T> SetJoin<X, E> treat(SetJoin<X, T> join, Class<E> type) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> trim(char t, Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> trim(Expression<Character> t, Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> trim(Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> trim(Trimspec ts, char t, Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> trim(Trimspec ts, Expression<Character> t, Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> trim(Trimspec ts, Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CompoundSelection<Tuple> tuple(Selection<?>... selections) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<String> upper(Expression<String> x) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <V, M extends Map<?, V>> Expression<Collection<V>> values(M map) {
		// TODO Auto-generated method stub
		return null;
	}
}

package javax.persistence.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;

/**
 * Used to construct criteria queries, compound selections, expressions, predicates, orderings. Note that Predicate is used instead of
 * Expression<Boolean> in this API in order to work around the fact that Java generics are not compatible with varags.
 */
public interface CriteriaBuilder {

	/**
	 * Interface used to build general case expressions. Case conditions are evaluated in the order in which they are specified.
	 * 
	 * @param <R>
	 *            the type of the case
	 */
	public static interface Case<R> extends Expression<R> {

		/**
		 * Add an "else" clause to the case expression.
		 * 
		 * @param result
		 *            "else" result expression
		 * @return expression
		 */
		Expression<R> otherwise(Expression<? extends R> result);

		/**
		 * Add an "else" clause to the case expression.
		 * 
		 * @param result
		 *            "else" result
		 * @return expression
		 */
		Expression<R> otherwise(R result);

		/**
		 * Add a when/then clause to the case expression.
		 * 
		 * @param condition
		 *            "when" condition
		 * @param result
		 *            "then" result expression
		 * @return general case expression
		 */
		Case<R> when(Expression<Boolean> condition, Expression<? extends R> result);

		/**
		 * Add a when/then clause to the case expression.
		 * 
		 * @param condition
		 *            "when" condition
		 * @param result
		 *            "then" result value
		 * @return general case expression
		 */
		Case<R> when(Expression<Boolean> condition, R result);
	}

	/**
	 * Interface used to build coalesce expressions.
	 * 
	 * A coalesce expression is equivalent to a case expression that returns null if all its arguments evaluate to null, and the value of
	 * its first non-null argument otherwise.
	 * 
	 * @param <T>
	 *            the type of the coalesce
	 */
	public static interface Coalesce<T> extends Expression<T> {
		/**
		 * Add an argument to the coalesce expression.
		 * 
		 * @param value
		 *            expression
		 * @return coalesce expression
		 */
		Coalesce<T> value(Expression<? extends T> value);

		/**
		 * Add an argument to the coalesce expression.
		 * 
		 * @param value
		 *            value
		 * @return coalesce expression
		 */
		Coalesce<T> value(T value);
	}

	/**
	 * Interface used to build in predicates.
	 * 
	 * @param <T>
	 *            the type of the in
	 */
	public static interface In<T> extends Predicate {
		/**
		 * Return the expression to be tested against the list of values.
		 * 
		 * @return expression
		 */
		Expression<T> getExpression();

		/**
		 * Add to list of values to be tested against.
		 * 
		 * @param value
		 *            expression
		 * @return in predicate
		 */
		In<T> value(Expression<? extends T> value);

		/**
		 * Add to list of values to be tested against.
		 * 
		 * @param value
		 *            value
		 * @return in predicate
		 */
		In<T> value(T value);
	}

	/**
	 * Interface used to build simple case expressions. Case conditions are evaluated in the order in which they are specified.
	 * 
	 * @param <C>
	 *            the type of the case
	 * @param <R>
	 *            the type of the result
	 */
	public static interface SimpleCase<C, R> extends Expression<R> {
		/**
		 * Return the expression to be tested against the conditions.
		 * 
		 * @return expression
		 */
		Expression<C> getExpression();

		/**
		 * Add an "else" clause to the case expression.
		 * 
		 * @param result
		 *            "else" result expression
		 * @return expression
		 */
		Expression<R> otherwise(Expression<? extends R> result);

		/**
		 * Add an "else" clause to the case expression.
		 * 
		 * @param result
		 *            "else" result
		 * @return expression
		 */
		Expression<R> otherwise(R result);

		/**
		 * Add a when/then clause to the case expression.
		 * 
		 * @param condition
		 *            "when" condition
		 * @param result
		 *            "then" result expression
		 * @return simple case expression
		 */
		SimpleCase<C, R> when(C condition, Expression<? extends R> result);

		/**
		 * Add a when/then clause to the case expression.
		 * 
		 * @param condition
		 *            "when" condition
		 * @param result
		 *            "then" result value
		 * @return simple case expression
		 */
		SimpleCase<C, R> when(C condition, R result);
	}

	/**
	 * Types for Trimspec.
	 * 
	 */
	public static enum Trimspec {
		/**
		 * Trim from leading end.
		 */
		LEADING,
		/**
		 * Trim from trailing end.
		 */
		TRAILING,
		/**
		 * Trim from both ends.
		 */
		BOTH
	}

	/**
	 * Create an expression that returns the absolute value of its argument.
	 * 
	 * @param x
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return absolute value
	 */
	<N extends Number> Expression<N> abs(Expression<N> x);

	/**
	 * Create an all expression over the subquery results.
	 * 
	 * @param subquery
	 * @param <Y>
	 *            the type of the expression
	 * @return all expression
	 */
	<Y> Expression<Y> all(Subquery<Y> subquery);

	/**
	 * Create a conjunction of the given boolean expressions.
	 * 
	 * @param x
	 *            boolean expression
	 * @param y
	 *            boolean expression
	 * @return and predicate
	 */
	Predicate and(Expression<Boolean> x, Expression<Boolean> y);

	/**
	 * Create a conjunction of the given restriction predicates. A conjunction of zero predicates is true.
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return and predicate
	 */
	Predicate and(Predicate... restrictions);

	/**
	 * Create an any expression over the subquery results. This expression is equivalent to a some expression.
	 * 
	 * @param subquery
	 * @param <Y>
	 *            the type of the expression
	 * @return any expression
	 */
	<Y> Expression<Y> any(Subquery<Y> subquery);

	/**
	 * Create an array-valued selection item.
	 * 
	 * @param selections
	 *            selection items
	 * @return array-valued compound selection
	 * @throws IllegalArgumentException
	 *             if an argument is a tuple- or array-valued selection item
	 */
	CompoundSelection<Object[]> array(Selection<?>... selections);

	/**
	 * Create an ordering by the ascending value of the expression.
	 * 
	 * @param x
	 *            expression used to define the ordering
	 * @return ascending ordering corresponding to the expression
	 */
	Order asc(Expression<?> x);

	/**
	 * Create an aggregate expression applying the avg operation.
	 * 
	 * @param x
	 *            expression representing input value to avg operation
	 * @param <N>
	 *            the type of the expression
	 * @return avg expression
	 */
	<N extends Number> Expression<Double> avg(Expression<N> x);

	/**
	 * Create a predicate for testing whether the first argument is between the second and third arguments in value.
	 * 
	 * @param v
	 *            expression
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return between predicate
	 */
	<Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> v, Expression<? extends Y> x, Expression<? extends Y> y);

	/**
	 * Create a predicate for testing whether the first argument is between the second and third arguments in value.
	 * 
	 * @param v
	 *            expression
	 * @param x
	 *            value
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return between predicate
	 */
	<Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> v, Y x, Y y);

	/**
	 * Create a coalesce expression.
	 * 
	 * @param <T>
	 *            the type of the coalesce
	 * @return coalesce expression
	 */
	<T> Coalesce<T> coalesce();

	/**
	 * Create an expression that returns null if all its arguments evaluate to null, and the value of the first non-null argument otherwise.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return coalesce expression
	 */
	<Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y);

	/**
	 * Create an expression that returns null if all its arguments evaluate to null, and the value of the first non-null argument otherwise.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return coalesce expression
	 */
	<Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y);

	/**
	 * Create an expression for string concatenation.
	 * 
	 * @param x
	 *            string expression
	 * @param y
	 *            string expression
	 * @return expression corresponding to concatenation
	 */
	Expression<String> concat(Expression<String> x, Expression<String> y);

	/**
	 * Create an expression for string concatenation.
	 * 
	 * @param x
	 *            string expression
	 * @param y
	 *            string
	 * @return expression corresponding to concatenation
	 */
	Expression<String> concat(Expression<String> x, String y);

	/**
	 * Create an expression for string concatenation.
	 * 
	 * @param x
	 *            string
	 * @param y
	 *            string expression
	 * @return expression corresponding to concatenation
	 */
	Expression<String> concat(String x, Expression<String> y);

	/**
	 * Create a conjunction (with zero conjuncts). A conjunction with zero conjuncts is true.
	 * 
	 * @return and predicate
	 */
	Predicate conjunction();

	/**
	 * Create a selection item corresponding to a constructor. This method is used to specify a constructor that will be applied to the
	 * results of the query execution. If the constructor is for an entity class, the resulting entities will be in the new state after the
	 * query is executed.
	 * 
	 * @param resultClass
	 *            class whose instance is to be constructed
	 * @param selections
	 *            arguments to the constructor
	 * @param <Y>
	 *            the type of the selection
	 * @return compound selection item
	 * @throws IllegalArgumentException
	 *             if an argument is a tuple- or array-valued selection item
	 */
	<Y> CompoundSelection<Y> construct(Class<Y> resultClass, Selection<?>... selections);

	/**
	 * Create an aggregate expression applying the count operation.
	 * 
	 * @param x
	 *            expression representing input value to count operation
	 * @return count expression
	 */
	Expression<Long> count(Expression<?> x);

	/**
	 * Create an aggregate expression applying the count distinct operation.
	 * 
	 * @param x
	 *            expression representing input value to count distinct operation
	 * @return count distinct expression
	 */
	Expression<Long> countDistinct(Expression<?> x);

	/**
	 * Create a CriteriaQuery object.
	 * 
	 * @return criteria query object
	 */
	CriteriaQuery<Object> createQuery();

	/**
	 * Create a CriteriaQuery object with the specified result type.
	 * 
	 * @param resultClass
	 *            type of the query result
	 * @param <T>
	 *            the type of the criteria query
	 * @return criteria query object
	 */
	<T> CriteriaQuery<T> createQuery(Class<T> resultClass);

	/**
	 * Create a CriteriaQuery object that returns a tuple of objects as its result.
	 * 
	 * @return criteria query object
	 */
	CriteriaQuery<Tuple> createTupleQuery();

	/**
	 * Create expression to return current date.
	 * 
	 * @return expression for current date
	 */
	Expression<java.sql.Date> currentDate();

	/**
	 * Create expression to return current time.
	 * 
	 * @return expression for current time
	 */
	Expression<java.sql.Time> currentTime();

	/**
	 * Create expression to return current timestamp.
	 * 
	 * @return expression for current timestamp
	 */
	Expression<java.sql.Timestamp> currentTimestamp();

	/**
	 * Create an ordering by the descending value of the expression.
	 * 
	 * @param x
	 *            expression used to define the ordering
	 * @return descending ordering corresponding to the expression
	 */
	Order desc(Expression<?> x);

	/**
	 * Create an expression that returns the difference between its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return difference
	 */
	<N extends Number> Expression<N> diff(Expression<? extends N> x, Expression<? extends N> y);

	/**
	 * Create an expression that returns the difference between its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <N>
	 *            the type of the expression
	 * @return difference
	 */
	<N extends Number> Expression<N> diff(Expression<? extends N> x, N y);

	/**
	 * Create an expression that returns the difference between its arguments.
	 * 
	 * @param x
	 *            value
	 * @param y
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return difference
	 */
	<N extends Number> Expression<N> diff(N x, Expression<? extends N> y);

	/**
	 * Create a disjunction (with zero disjuncts). A disjunction with zero disjuncts is false.
	 * 
	 * @return or predicate
	 */
	Predicate disjunction();

	/**
	 * Create a predicate for testing the arguments for equality.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return equality predicate
	 */
	Predicate equal(Expression<?> x, Expression<?> y);

	/**
	 * Create a predicate for testing the arguments for equality.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            object
	 * @return equality predicate
	 */
	Predicate equal(Expression<?> x, Object y);

	/**
	 * Create a predicate testing the existence of a subquery result.
	 * 
	 * @param subquery
	 *            subquery whose result is to be tested
	 * @return exists predicate
	 */
	Predicate exists(Subquery<?> subquery);

	/**
	 * Create an expression for the execution of a database function.
	 * 
	 * @param name
	 *            function name
	 * @param type
	 *            expected result type
	 * @param args
	 *            function arguments
	 * @param <T>
	 *            the type of the expression
	 * @return expression
	 */
	<T> Expression<T> function(String name, Class<T> type, Expression<?>... args);

	/**
	 * Create a predicate for testing whether the first argument is greater than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return greater-than-or-equal predicate
	 */
	Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y);

	/**
	 * Create a predicate for testing whether the first argument is greater than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @return greater-than-or-equal predicate
	 */
	Predicate ge(Expression<? extends Number> x, Number y);

	/**
	 * Create a predicate for testing whether the first argument is greater than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return greater-than predicate
	 */
	<Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Expression<? extends Y> y);

	/**
	 * Create a predicate for testing whether the first argument is greater than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return greater-than predicate
	 */
	<Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Y y);

	/**
	 * Create a predicate for testing whether the first argument is greater than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return greater-than-or-equal predicate
	 */
	<Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y);

	/**
	 * Create a predicate for testing whether the first argument is greater than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return greater-than-or-equal predicate
	 */
	<Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Y y);

	/**
	 * Create an aggregate expression for finding the greatest of the values (strings, dates, etc).
	 * 
	 * @param x
	 *            expression representing input value to greatest operation
	 * @param <X>
	 *            the type of the expression
	 * @return greatest expression
	 */
	<X extends Comparable<? super X>> Expression<X> greatest(Expression<X> x);

	/**
	 * Create a predicate for testing whether the first argument is greater than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return greater-than predicate
	 */
	Predicate gt(Expression<? extends Number> x, Expression<? extends Number> y);

	/**
	 * Create a predicate for testing whether the first argument is greater than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @return greater-than predicate
	 */
	Predicate gt(Expression<? extends Number> x, Number y);

	/**
	 * Create predicate to test whether given expression is contained in a list of values.
	 * 
	 * @param expression
	 *            to be tested against list of values
	 * @param <T>
	 *            the type of the expression
	 * @return in predicate
	 */
	<T> In<T> in(Expression<? extends T> expression);

	/**
	 * Create a predicate that tests whether a collection is empty.
	 * 
	 * @param collection
	 *            expression
	 * @param <C>
	 *            the type of the expression
	 * @return is-empty predicate
	 */
	<C extends Collection<?>> Predicate isEmpty(Expression<C> collection);

	/**
	 * Create a predicate testing for a false value.
	 * 
	 * @param x
	 *            expression to be tested
	 * @return predicate
	 */
	Predicate isFalse(Expression<Boolean> x);

	/**
	 * Create a predicate that tests whether an element is a member of a collection. If the collection is empty, the predicate will be
	 * false.
	 * 
	 * @param elem
	 *            element
	 * @param collection
	 *            expression
	 * @param <E>
	 *            the type of the element
	 * @param <C>
	 *            the type of the collection
	 * @return is-member predicate
	 */
	<E, C extends Collection<E>> Predicate isMember(E elem, Expression<C> collection);

	/**
	 * Create a predicate that tests whether an element is a member of a collection. If the collection is empty, the predicate will be
	 * false.
	 * 
	 * @param elem
	 *            element expression
	 * @param collection
	 *            expression
	 * @param <E>
	 *            the type of the element
	 * @param <C>
	 *            the type of the collection
	 * @return is-member predicate
	 */
	<E, C extends Collection<E>> Predicate isMember(Expression<E> elem, Expression<C> collection);

	/**
	 * Create a predicate that tests whether a collection is not empty.
	 * 
	 * @param collection
	 *            expression
	 * @param <C>
	 *            the type of the collection
	 * @return is-not-empty predicate
	 */
	<C extends Collection<?>> Predicate isNotEmpty(Expression<C> collection);

	/**
	 * Create a predicate that tests whether an element is not a member of a collection. If the collection is empty, the predicate will be
	 * true.
	 * 
	 * @param elem
	 *            element
	 * @param collection
	 *            expression
	 * @param <E>
	 *            the type of the element
	 * @param <C>
	 *            the type of the collection
	 * @return is-not-member predicate
	 */
	<E, C extends Collection<E>> Predicate isNotMember(E elem, Expression<C> collection);

	/**
	 * Create a predicate that tests whether an element is not a member of a collection. If the collection is empty, the predicate will be
	 * true.
	 * 
	 * @param elem
	 *            element expression
	 * @param collection
	 *            expression
	 * @param <E>
	 *            the type of the element
	 * @param <C>
	 *            the type of the collection
	 * @return is-not-member predicate
	 */
	<E, C extends Collection<E>> Predicate isNotMember(Expression<E> elem, Expression<C> collection);

	/**
	 * Create a predicate to test whether the expression is not null.
	 * 
	 * @param x
	 *            expression
	 * @return is-not-null predicate
	 */
	Predicate isNotNull(Expression<?> x);

	/**
	 * Create a predicate to test whether the expression is null.
	 * 
	 * @param x
	 *            expression
	 * @return is-null predicate
	 */
	Predicate isNull(Expression<?> x);

	/**
	 * Create a predicate testing for a true value.
	 * 
	 * @param x
	 *            expression to be tested
	 * @return predicate
	 */
	Predicate isTrue(Expression<Boolean> x);

	/**
	 * Create an expression that returns the keys of a map.
	 * 
	 * @param map
	 *            map
	 * @param <K>
	 *            the type of the key
	 * @param <M>
	 *            the type of the map
	 * @return set expression
	 */
	<K, M extends Map<K, ?>> Expression<Set<K>> keys(M map);

	/**
	 * Create a predicate for testing whether the first argument is less than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return less-than-or-equal predicate
	 */
	Predicate le(Expression<? extends Number> x, Expression<? extends Number> y);

	/**
	 * Create a predicate for testing whether the first argument is less than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @return less-than-or-equal predicate
	 */
	Predicate le(Expression<? extends Number> x, Number y);

	/**
	 * Create an aggregate expression for finding the least of the values (strings, dates, etc).
	 * 
	 * @param x
	 *            expression representing input value to least operation
	 * @param <X>
	 *            the type of the expression
	 * @return least expression
	 */
	<X extends Comparable<? super X>> Expression<X> least(Expression<X> x);

	/**
	 * Create expression to return length of a string.
	 * 
	 * @param x
	 *            string expression
	 * @return length expression
	 */
	Expression<Integer> length(Expression<String> x);

	/**
	 * Create a predicate for testing whether the first argument is less than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return less-than predicate
	 */
	<Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> x, Expression<? extends Y> y);

	/**
	 * Create a predicate for testing whether the first argument is less than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return less-than predicate
	 */
	<Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> x, Y y);

	/**
	 * Create a predicate for testing whether the first argument is less than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return less-than-or-equal predicate
	 */
	<Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y);

	/**
	 * Create a predicate for testing whether the first argument is less than or equal to the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return less-than-or-equal predicate
	 */
	<Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Y y);

	/**
	 * Create a predicate for testing whether the expression satisfies the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string expression
	 * @return like predicate
	 */
	Predicate like(Expression<String> x, Expression<String> pattern);

	/**
	 * Create a predicate for testing whether the expression satisfies the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string expression
	 * @param escapeChar
	 *            escape character
	 * @return like predicate
	 */
	Predicate like(Expression<String> x, Expression<String> pattern, char escapeChar);

	/**
	 * Create a predicate for testing whether the expression satisfies the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string expression
	 * @param escapeChar
	 *            escape character expression
	 * @return like predicate
	 */
	Predicate like(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar);

	/**
	 * Create a predicate for testing whether the expression satisfies the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string
	 * @return like predicate
	 */
	Predicate like(Expression<String> x, String pattern);

	/**
	 * Create a predicate for testing whether the expression satisfies the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string
	 * @param escapeChar
	 *            escape character
	 * @return like predicate
	 */
	Predicate like(Expression<String> x, String pattern, char escapeChar);

	/**
	 * Create a predicate for testing whether the expression satisfies the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string
	 * @param escapeChar
	 *            escape character expression
	 * @return like predicate
	 */
	Predicate like(Expression<String> x, String pattern, Expression<Character> escapeChar);

	/**
	 * Create an expression for a literal.
	 * 
	 * @param value
	 *            value represented by the expression
	 * @return expression literal
	 * @param <T>
	 *            the type of the expression
	 * @throws IllegalArgumentException
	 *             if value is null
	 */
	<T> Expression<T> literal(T value);

	/**
	 * Create expression to locate the position of one string within another, returning position of first character if found. The first
	 * position in a string is denoted by 1. If the string to be located is not found, 0 is returned.
	 * 
	 * @param x
	 *            expression for string to be searched
	 * @param pattern
	 *            expression for string to be located
	 * @return expression corresponding to position
	 */
	Expression<Integer> locate(Expression<String> x, Expression<String> pattern);

	/**
	 * Create expression to locate the position of one string within another, returning position of first character if found. The first
	 * position in a string is denoted by 1. If the string to be located is not found, 0 is returned.
	 * 
	 * @param x
	 *            expression for string to be searched
	 * @param pattern
	 *            expression for string to be located
	 * @param from
	 *            expression for position at which to start search
	 * @return expression corresponding to position
	 */
	Expression<Integer> locate(Expression<String> x, Expression<String> pattern, Expression<Integer> from);

	/**
	 * Create expression to locate the position of one string within another, returning position of first character if found. The first
	 * position in a string is denoted by 1. If the string to be located is not found, 0 is returned.
	 * 
	 * @param x
	 *            expression for string to be searched
	 * @param pattern
	 *            string to be located
	 * @return expression corresponding to position
	 */
	Expression<Integer> locate(Expression<String> x, String pattern);

	/**
	 * Create expression to locate the position of one string within another, returning position of first character if found. The first
	 * position in a string is denoted by 1. If the string to be located is not found, 0 is returned.
	 * 
	 * @param x
	 *            expression for string to be searched
	 * @param pattern
	 *            string to be located
	 * @param from
	 *            position at which to start search
	 * @return expression corresponding to position
	 */
	Expression<Integer> locate(Expression<String> x, String pattern, int from);

	/**
	 * Create expression for converting a string to lowercase.
	 * 
	 * @param x
	 *            string expression
	 * @return expression to convert to lowercase
	 */
	Expression<String> lower(Expression<String> x);

	/**
	 * Create a predicate for testing whether the first argument is less than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return less-than predicate
	 */
	Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y);

	/**
	 * Create a predicate for testing whether the first argument is less than the second.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @return less-than predicate
	 */
	Predicate lt(Expression<? extends Number> x, Number y);

	/**
	 * Create an aggregate expression applying the numerical max operation.
	 * 
	 * @param x
	 *            expression representing input value to max operation
	 * @param <N>
	 *            the type of the expression
	 * @return max expression
	 */
	<N extends Number> Expression<N> max(Expression<N> x);

	/**
	 * Create an aggregate expression applying the numerical min operation.
	 * 
	 * @param x
	 *            expression representing input value to min operation
	 * @param <N>
	 *            the type of the expression
	 * @return min expression
	 */
	<N extends Number> Expression<N> min(Expression<N> x);

	/**
	 * Create an expression that returns the modulus of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return modulus
	 */
	Expression<Integer> mod(Expression<Integer> x, Expression<Integer> y);

	/**
	 * Create an expression that returns the modulus of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @return modulus
	 */
	Expression<Integer> mod(Expression<Integer> x, Integer y);

	/**
	 * Create an expression that returns the modulus of its arguments.
	 * 
	 * @param x
	 *            value
	 * @param y
	 *            expression
	 * @return modulus
	 */
	Expression<Integer> mod(Integer x, Expression<Integer> y);

	/**
	 * Create an expression that returns the arithmetic negation of its argument.
	 * 
	 * @param x
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return arithmetic negation
	 */
	<N extends Number> Expression<N> neg(Expression<N> x);

	/**
	 * Create a negation of the given restriction.
	 * 
	 * @param restriction
	 *            restriction expression
	 * @return not predicate
	 */
	Predicate not(Expression<Boolean> restriction);

	/**
	 * Create a predicate for testing the arguments for inequality.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return inequality predicate
	 */
	Predicate notEqual(Expression<?> x, Expression<?> y);

	/**
	 * Create a predicate for testing the arguments for inequality.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            object
	 * @return inequality predicate
	 */
	Predicate notEqual(Expression<?> x, Object y);

	/**
	 * Create a predicate for testing whether the expression does not satisfy the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string expression
	 * @return not-like predicate
	 */
	Predicate notLike(Expression<String> x, Expression<String> pattern);

	/**
	 * Create a predicate for testing whether the expression does not satisfy the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string expression
	 * @param escapeChar
	 *            escape character
	 * @return not-like predicate
	 */
	Predicate notLike(Expression<String> x, Expression<String> pattern, char escapeChar);

	/**
	 * Create a predicate for testing whether the expression does not satisfy the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string expression
	 * @param escapeChar
	 *            escape character expression
	 * @return not-like predicate
	 */
	Predicate notLike(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar);

	/**
	 * Create a predicate for testing whether the expression does not satisfy the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string
	 * @return not-like predicate
	 */
	Predicate notLike(Expression<String> x, String pattern);

	/**
	 * Create a predicate for testing whether the expression does not satisfy the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string
	 * @param escapeChar
	 *            escape character
	 * @return not-like predicate
	 */
	Predicate notLike(Expression<String> x, String pattern, char escapeChar);

	/**
	 * Create a predicate for testing whether the expression does not satisfy the given pattern.
	 * 
	 * @param x
	 *            string expression
	 * @param pattern
	 *            string
	 * @param escapeChar
	 *            escape character expression
	 * @return not-like predicate
	 */
	Predicate notLike(Expression<String> x, String pattern, Expression<Character> escapeChar);

	/**
	 * Create an expression that tests whether its argument are equal, returning null if they are and the value of the first expression if
	 * they are not.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <Y>
	 *            the type of the expression
	 * @return nullif expression
	 */
	<Y> Expression<Y> nullif(Expression<Y> x, Expression<?> y);

	/**
	 * Create an expression that tests whether its argument are equal, returning null if they are and the value of the first expression if
	 * they are not.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <Y>
	 *            the type of the expression
	 * @return nullif expression
	 */
	<Y> Expression<Y> nullif(Expression<Y> x, Y y);

	/**
	 * Create an expression for a null literal with the given type.
	 * 
	 * @param resultClass
	 *            type of the null literal
	 * @param <T>
	 *            the type of the expression
	 * @return null expression literal
	 */
	<T> Expression<T> nullLiteral(Class<T> resultClass);

	/**
	 * Create a disjunction of the given boolean expressions.
	 * 
	 * @param x
	 *            boolean expression
	 * @param y
	 *            boolean expression
	 * @return or predicate
	 */
	Predicate or(Expression<Boolean> x, Expression<Boolean> y);

	/**
	 * Create a disjunction of the given restriction predicates. A disjunction of zero predicates is false.
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return or predicate
	 */
	Predicate or(Predicate... restrictions);

	/**
	 * Create a parameter expression.
	 * 
	 * @param paramClass
	 *            parameter class
	 * @param <T>
	 *            the type of the expression
	 * @return parameter expression
	 */
	<T> ParameterExpression<T> parameter(Class<T> paramClass);

	/**
	 * Create a parameter expression with the given name.
	 * 
	 * @param paramClass
	 *            parameter class
	 * @param name
	 *            name that can be used to refer to the parameter
	 * @param <T>
	 *            the type of the expression
	 * @return parameter expression
	 */
	<T> ParameterExpression<T> parameter(Class<T> paramClass, String name);

	/**
	 * Create an expression that returns the product of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return product
	 */
	<N extends Number> Expression<N> prod(Expression<? extends N> x, Expression<? extends N> y);

	/**
	 * Create an expression that returns the product of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <N>
	 *            the type of the expression
	 * @return product
	 */
	<N extends Number> Expression<N> prod(Expression<? extends N> x, N y);

	/**
	 * Create an expression that returns the product of its arguments.
	 * 
	 * @param x
	 *            value
	 * @param y
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return product
	 */
	<N extends Number> Expression<N> prod(N x, Expression<? extends N> y);

	/**
	 * Create an expression that returns the quotient of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @return quotient
	 */
	Expression<Number> quot(Expression<? extends Number> x, Expression<? extends Number> y);

	/**
	 * Create an expression that returns the quotient of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @return quotient
	 */
	Expression<Number> quot(Expression<? extends Number> x, Number y);

	/**
	 * Create an expression that returns the quotient of its arguments.
	 * 
	 * @param x
	 *            value
	 * @param y
	 *            expression
	 * @return quotient
	 */
	Expression<Number> quot(Number x, Expression<? extends Number> y);

	/**
	 * Create a general case expression.
	 * 
	 * @param <R>
	 *            the type of the result
	 * @return general case expression
	 */
	<R> Case<R> selectCase();

	/**
	 * Create a simple case expression.
	 * 
	 * @param expression
	 *            to be tested against the case conditions
	 * @param <C>
	 *            the type of the case
	 * @param <R>
	 *            the type of the result
	 * @return simple case expression
	 */
	<C, R> SimpleCase<C, R> selectCase(Expression<? extends C> expression);

	/**
	 * Create an expression that tests the size of a collection.
	 * 
	 * @param collection
	 *            collection
	 * @param <C>
	 *            the type of the collection
	 * @return size expression
	 */
	<C extends Collection<?>> Expression<Integer> size(C collection);

	/**
	 * Create an expression that tests the size of a collection.
	 * 
	 * @param collection
	 *            expression
	 * @param <C>
	 *            the type of the collection
	 * @return size expression
	 */
	<C extends Collection<?>> Expression<Integer> size(Expression<C> collection);

	/**
	 * Create a some expression over the subquery results. This expression is equivalent to an any expression.
	 * 
	 * @param subquery
	 * @param <Y>
	 *            the type of the expression
	 * @return some expression
	 */
	<Y> Expression<Y> some(Subquery<Y> subquery);

	/**
	 * Create an expression that returns the square root of its argument.
	 * 
	 * @param x
	 *            expression
	 * @return square root
	 */
	Expression<Double> sqrt(Expression<? extends Number> x);

	/**
	 * Create an expression for substring extraction. Extracts a substring starting at the specified position through to end of the string.
	 * First position is 1.
	 * 
	 * @param x
	 *            string expression
	 * @param from
	 *            start position expression
	 * @return expression corresponding to substring extraction
	 */
	Expression<String> substring(Expression<String> x, Expression<Integer> from);

	/**
	 * Create an expression for substring extraction. Extracts a substring of given length starting at the specified position. First
	 * position is 1.
	 * 
	 * @param x
	 *            string expression
	 * @param from
	 *            start position expression
	 * @param len
	 *            length expression
	 * @return expression corresponding to substring extraction
	 */
	Expression<String> substring(Expression<String> x, Expression<Integer> from, Expression<Integer> len);

	/**
	 * Create an expression for substring extraction. Extracts a substring starting at the specified position through to end of the string.
	 * First position is 1.
	 * 
	 * @param x
	 *            string expression
	 * @param from
	 *            start position
	 * @return expression corresponding to substring extraction
	 */
	Expression<String> substring(Expression<String> x, int from);

	/**
	 * Create an expression for substring extraction. Extracts a substring of given length starting at the specified position. First
	 * position is 1.
	 * 
	 * @param x
	 *            string expression
	 * @param from
	 *            start position
	 * @param len
	 *            length
	 * @return expression corresponding to substring extraction
	 */
	Expression<String> substring(Expression<String> x, int from, int len);

	/**
	 * Create an expression that returns the sum of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return sum
	 */
	<N extends Number> Expression<N> sum(Expression<? extends N> x, Expression<? extends N> y);

	/**
	 * Create an expression that returns the sum of its arguments.
	 * 
	 * @param x
	 *            expression
	 * @param y
	 *            value
	 * @param <N>
	 *            the type of the expression
	 * @return sum
	 */
	<N extends Number> Expression<N> sum(Expression<? extends N> x, N y);

	/**
	 * Create an aggregate expression applying the sum operation.
	 * 
	 * @param x
	 *            expression representing input value to sum operation
	 * @param <N>
	 *            the type of the expression
	 * @return sum expression
	 */
	<N extends Number> Expression<N> sum(Expression<N> x);

	/**
	 * Create an expression that returns the sum of its arguments.
	 * 
	 * @param x
	 *            value
	 * @param y
	 *            expression
	 * @param <N>
	 *            the type of the expression
	 * @return sum
	 */
	<N extends Number> Expression<N> sum(N x, Expression<? extends N> y);

	/**
	 * Create an aggregate expression applying the sum operation to a Float-valued expression, returning a Double result.
	 * 
	 * @param x
	 *            expression representing input value to sum operation
	 * @return sum expression
	 */
	Expression<Double> sumAsDouble(Expression<Float> x);

	/**
	 * Create an aggregate expression applying the sum operation to an Integer-valued expression, returning a Long result.
	 * 
	 * @param x
	 *            expression representing input value to sum operation
	 * @return sum expression
	 */
	Expression<Long> sumAsLong(Expression<Integer> x);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param number
	 *            numeric expression
	 * @return Expression<BigDecimal>
	 */
	Expression<BigDecimal> toBigDecimal(Expression<? extends Number> number);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param number
	 *            numeric expression
	 * @return Expression<BigInteger>
	 */
	Expression<BigInteger> toBigInteger(Expression<? extends Number> number);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param number
	 *            numeric expression
	 * @return Expression<Double>
	 */
	Expression<Double> toDouble(Expression<? extends Number> number);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param number
	 *            numeric expression
	 * @return Expression<Float>
	 */
	Expression<Float> toFloat(Expression<? extends Number> number);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param number
	 *            numeric expression
	 * @return Expression<Integer>
	 */
	Expression<Integer> toInteger(Expression<? extends Number> number);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param number
	 *            numeric expression
	 * @return Expression<Long>
	 */
	Expression<Long> toLong(Expression<? extends Number> number);

	/**
	 * Typecast. Returns same expression object.
	 * 
	 * @param character
	 *            expression
	 * @return Expression<String>
	 */
	Expression<String> toString(Expression<Character> character);

	/**
	 * Create expression to trim character from both ends of a string.
	 * 
	 * @param t
	 *            character to be trimmed
	 * @param x
	 *            expression for string to trim
	 * @return trim expression
	 */
	Expression<String> trim(char t, Expression<String> x);

	/**
	 * Create expression to trim character from both ends of a string.
	 * 
	 * @param t
	 *            expression for character to be trimmed
	 * @param x
	 *            expression for string to trim
	 * @return trim expression
	 */
	Expression<String> trim(Expression<Character> t, Expression<String> x);

	/**
	 * Create expression to trim blanks from both ends of a string.
	 * 
	 * @param x
	 *            expression for string to trim
	 * @return trim expression
	 */
	Expression<String> trim(Expression<String> x);

	/**
	 * Create expression to trim character from a string.
	 * 
	 * @param ts
	 *            trim specification
	 * @param t
	 *            character to be trimmed
	 * @param x
	 *            expression for string to trim
	 * @return trim expression
	 */
	Expression<String> trim(Trimspec ts, char t, Expression<String> x);

	/**
	 * Create expression to trim character from a string.
	 * 
	 * @param ts
	 *            trim specification
	 * @param t
	 *            expression for character to be trimmed
	 * @param x
	 *            expression for string to trim
	 * @return trim expression
	 */
	Expression<String> trim(Trimspec ts, Expression<Character> t, Expression<String> x);

	/**
	 * Create expression to trim blanks from a string.
	 * 
	 * @param ts
	 *            trim specification
	 * @param x
	 *            expression for string to trim
	 * @return trim expression
	 */
	Expression<String> trim(Trimspec ts, Expression<String> x);

	/**
	 * Create a tuple-valued selection item.
	 * 
	 * @param selections
	 *            selection items
	 * @return tuple-valued compound selection
	 * @throws IllegalArgumentException
	 *             if an argument is a tuple- or array-valued selection item
	 */
	CompoundSelection<Tuple> tuple(Selection<?>... selections);

	/**
	 * Create expression for converting a string to uppercase.
	 * 
	 * @param x
	 *            string expression
	 * @return expression to convert to uppercase
	 */
	Expression<String> upper(Expression<String> x);

	/**
	 * Create an expression that returns the values of a map.
	 * 
	 * @param map
	 *            map
	 * @param <V>
	 *            type of the value
	 * @param <M>
	 *            type of the map
	 * @return collection expression
	 */
	<V, M extends Map<?, V>> Expression<Collection<V>> values(M map);
}

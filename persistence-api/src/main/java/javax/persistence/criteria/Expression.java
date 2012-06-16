package javax.persistence.criteria;

import java.util.Collection;

/**
 * Type for query expressions.
 * 
 * @param <T>
 *            the type of the expression
 */
public interface Expression<T> extends Selection<T> {
	/**
	 * Perform a typecast upon the expression, returning a new expression object. This method does not cause type conversion: the runtime
	 * type is not changed.
	 * <p>
	 * Warning: may result in a runtime failure.
	 * 
	 * @param type
	 *            intended type of the expression
	 * @param <X>
	 *            the type of the expression
	 * @return new expression of the given type
	 */
	<X> Expression<X> as(Class<X> type);

	/**
	 * Create a predicate to test whether the expression is a member of the collection.
	 * 
	 * @param values
	 *            collection of values to be tested against
	 * @return predicate testing for membership
	 */
	Predicate in(Collection<?> values);

	/**
	 * Create a predicate to test whether the expression is a member of the argument list.
	 * 
	 * @param values
	 *            expressions to be tested against
	 * @return predicate testing for membership
	 */
	Predicate in(Expression<?>... values);

	/**
	 * Create a predicate to test whether the expression is a member of the collection.
	 * 
	 * @param values
	 *            expression corresponding to collection to be tested against
	 * @return predicate testing for membership
	 */
	Predicate in(Expression<Collection<?>> values);

	/**
	 * Create a predicate to test whether the expression is a member of the argument list.
	 * 
	 * @param values
	 *            values to be tested against
	 * @return predicate testing for membership
	 */
	Predicate in(Object... values);

	/**
	 * Create a predicate to test whether the expression is not null.
	 * 
	 * @return predicate testing whether the expression is not null
	 */
	Predicate isNotNull();

	/**
	 * Create a predicate to test whether the expression is null.
	 * 
	 * @return predicate testing whether the expression is null
	 */
	Predicate isNull();
}

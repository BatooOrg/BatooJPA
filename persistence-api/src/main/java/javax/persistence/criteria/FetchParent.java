package javax.persistence.criteria;

import java.util.Set;

import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * Represents an element of the from clause which may function as the parent of Fetches.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 */
public interface FetchParent<Z, X> {

	/**
	 * Create a fetch join to the specified collection-valued attribute using an inner join.
	 * 
	 * @param attribute
	 *            target of the join
	 * @param <Y>
	 *            the target type of the fetch
	 * @return the resulting join
	 */
	<Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute);

	/**
	 * Create a fetch join to the specified collection-valued attribute using the given join type.
	 * 
	 * @param attribute
	 *            target of the join
	 * @param jt
	 *            join type
	 * @param <Y>
	 *            the target type of the fetch
	 * @return the resulting join
	 */
	<Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt);

	/**
	 * Create a fetch join to the specified single-valued attribute using an inner join.
	 * 
	 * @param attribute
	 *            target of the join
	 * @param <Y>
	 *            the target type of the fetch
	 * @return the resulting fetch join
	 */
	<Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute);

	/**
	 * Create a fetch join to the specified single-valued attribute using the given join type.
	 * 
	 * @param attribute
	 *            target of the join
	 * @param jt
	 *            join type
	 * @param <Y>
	 *            the target type of the fetch
	 * @return the resulting fetch join
	 */
	<Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt);

	/**
	 * Create a fetch join to the specified attribute using an inner join.
	 * 
	 * @param attributeName
	 *            name of the attribute for the target of the join
	 * @return the resulting fetch join
	 * @param <Y>
	 *            the target type of the fetch
	 * @throws IllegalArgumentException
	 *             if attribute of the given name does not exist
	 */
	<Y> Fetch<X, Y> fetch(String attributeName);

	/**
	 * Create a fetch join to the specified attribute using the given join type.
	 * 
	 * @param attributeName
	 *            name of the attribute for the target of the join
	 * @param jt
	 *            join type
	 * @return the resulting fetch join
	 * @param <Y>
	 *            the target type of the fetch
	 * @throws IllegalArgumentException
	 *             if attribute of the given name does not exist
	 */
	<Y> Fetch<X, Y> fetch(String attributeName, JoinType jt);

	/**
	 * Return the fetch joins that have been made from this type. Returns empty set if no fetch joins have been made from this type.
	 * Modifications to the set do not affect the query.
	 * 
	 * @return fetch joins made from this type
	 */
	Set<Fetch<X, ?>> getFetches();
}

package javax.persistence.criteria;

import javax.persistence.metamodel.Attribute;

/**
 * Represents a join-fetched association or attribute.
 * 
 * @param <Z>
 *            the source type of the fetch
 * @param <X>
 *            the target type of the fetch
 */
public interface Fetch<Z, X> extends FetchParent<Z, X> {
	/**
	 * Return the metamodel attribute corresponding to the fetch join.
	 * 
	 * @return metamodel attribute for the join
	 */
	Attribute<? super Z, ?> getAttribute();

	/**
	 * Return the join type used in the fetch join.
	 * 
	 * @return join type
	 */
	JoinType getJoinType();

	/**
	 * Return the predicate that corresponds to the ON restriction(s), or null if no ON condition has been specified.
	 * 
	 * @return the ON restriction predicate
	 */
	Predicate getOn();

	/**
	 * Return the parent of the fetched item.
	 * 
	 * @return fetch parent
	 */
	FetchParent<?, Z> getParent();

	/**
	 * Modify the fetch join to restrict the result according to the specified ON condition. Replaces the previous ON condition, if any.
	 * Return the fetch join object
	 * 
	 * @param restriction
	 *            a simple or compound boolean expression
	 * @return the modified fetch join object
	 */
	Fetch<Z, X> on(Expression<Boolean> restriction);

	/**
	 * Modify the fetch join to restrict the result according to the specified ON condition. Replaces the previous ON condition, if any.
	 * Return the fetch join object
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return the modified fetch join object
	 */
	Fetch<Z, X> on(Predicate... restrictions);
}

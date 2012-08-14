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
	 * Return the parent of the fetched item.
	 * 
	 * @return fetch parent
	 */
	FetchParent<?, Z> getParent();
}

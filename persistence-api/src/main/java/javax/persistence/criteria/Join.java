package javax.persistence.criteria;

import javax.persistence.metamodel.Attribute;

/**
 * A join to an entity, embeddable, or basic type.
 * 
 * @param <Z>
 *            the source type of the join
 * @param <X>
 *            the target type of the join
 */
public interface Join<Z, X> extends From<Z, X> {

	/**
	 * Return the metamodel attribute corresponding to the join.
	 * 
	 * @return metamodel attribute corresponding to the join
	 */
	Attribute<? super Z, ?> getAttribute();

	/**
	 * Return the join type.
	 * 
	 * @return join type
	 */
	JoinType getJoinType();

	/**
	 * Return the parent of the join.
	 * 
	 * @return join parent
	 */
	From<?, Z> getParent();
}

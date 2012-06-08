package javax.persistence.criteria;

import javax.persistence.metamodel.PluralAttribute;

/**
 * The PluralJoin interface defines functionality that is common to joins to all collection types. It is not intended to be used directly in
 * query construction.
 * 
 * @param <Z>
 *            the source type
 * @param <C>
 *            the collection type
 * @param <E>
 *            the element type of the collection
 */
public interface PluralJoin<Z, C, E> extends Join<Z, E> {
	/**
	 * Return the metamodel representation for the collection-valued attribute corresponding to the join.
	 * 
	 * @return metamodel collection-valued attribute corresponding to the target of the join
	 */
	@Override
	PluralAttribute<? super Z, C, E> getModel();
}

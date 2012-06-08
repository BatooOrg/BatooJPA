package javax.persistence.criteria;

import java.util.Collection;

import javax.persistence.metamodel.CollectionAttribute;

/**
 * The CollectionJoin interface is the type of the result of joining to a collection over an association or element collection that has been
 * specified as a java.util.Collection.
 * 
 * @param <Z>
 *            the source type of the join
 * @param <E>
 *            the element type of the target Collection
 */
public interface CollectionJoin<Z, E> extends PluralJoin<Z, Collection<E>, E> {
	/**
	 * Return the metamodel representation for the collection attribute.
	 * 
	 * @return metamodel type representing the Collection that is the target of the join
	 */
	@Override
	CollectionAttribute<? super Z, E> getModel();

	/**
	 * Modify the join to restrict the result according to the specified ON condition. Replaces the previous ON condition, if any. Return
	 * the join object
	 * 
	 * @param restriction
	 *            a simple or compound boolean expression
	 * @return the modified join object
	 */
	@Override
	CollectionJoin<Z, E> on(Expression<Boolean> restriction);

	/**
	 * Modify the join to restrict the result according to the specified ON condition. Replaces the previous ON condition, if any. Return
	 * the join object
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return the modified join object
	 */
	@Override
	CollectionJoin<Z, E> on(Predicate... restrictions);
}

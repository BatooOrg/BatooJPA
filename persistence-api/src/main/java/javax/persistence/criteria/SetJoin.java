package javax.persistence.criteria;

import java.util.Set;

import javax.persistence.metamodel.SetAttribute;

/**
 * The SetJoin interface is the type of the result of joining to a collection over an association or element collection that has been
 * specified as a java.util.Set.
 * 
 * @param <Z>
 *            the source type of the join
 * @param <E>
 *            the element type of the target Set
 */
public interface SetJoin<Z, E> extends PluralJoin<Z, Set<E>, E> {
	/**
	 * Return the metamodel representation for the set attribute.
	 * 
	 * @return metamodel type representing the Set that is the target of the join
	 */
	@Override
	SetAttribute<? super Z, E> getModel();

	/**
	 * Modify the join to restrict the result according to the specified ON condition. Replaces the previous ON condition, if any. Return
	 * the join object
	 * 
	 * @param restriction
	 *            a simple or compound boolean expression
	 * @return the modified join object
	 */
	@Override
	SetJoin<Z, E> on(Expression<Boolean> restriction);

	/**
	 * Modify the join to restrict the result according to the specified ON condition. Replaces the previous ON condition, if any. Return
	 * the join object
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return the modified join object
	 */
	@Override
	SetJoin<Z, E> on(Predicate... restrictions);
}

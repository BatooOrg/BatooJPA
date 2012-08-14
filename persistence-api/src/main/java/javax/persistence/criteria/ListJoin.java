package javax.persistence.criteria;

import java.util.List;

import javax.persistence.metamodel.ListAttribute;

/**
 * The ListJoin interface is the type of the result of joining to a collection over an association or element collection that has been
 * specified as a java.util.List.
 * 
 * @param <Z>
 *            the source type of the join
 * @param <E>
 *            the element type of the target List
 */
public interface ListJoin<Z, E> extends PluralJoin<Z, List<E>, E> {

	/**
	 * Return the metamodel representation for the list attribute.
	 * 
	 * @return metamodel type representing the List that is the target of the join
	 */
	@Override
	ListAttribute<? super Z, E> getModel();

	/**
	 * Create an expression that corresponds to the index of the object in the referenced association or element collection. This method
	 * must only be invoked upon an object that represents an association or element collection for which an order column has been defined.
	 * 
	 * @return expression denoting the index
	 */
	Expression<Integer> index();
}

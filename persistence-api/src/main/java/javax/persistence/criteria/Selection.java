package javax.persistence.criteria;

import java.util.List;

import javax.persistence.TupleElement;

/**
 * The Selection interface defines an item that is to be returned in a query result.
 * 
 * @param <X>
 *            the type of the selection item
 */
public interface Selection<X> extends TupleElement<X> {
	/**
	 * Assigns an alias to the selection item. Once assigned, an alias cannot be changed or reassigned. Returns the same selection item.
	 * 
	 * @param name
	 *            alias
	 * @return selection item
	 */
	Selection<X> alias(String name);

	/**
	 * Return the selection items composing a compound selection. Modifications to the list do not affect the query.
	 * 
	 * @return list of selection items
	 * @throws IllegalStateException
	 *             if selection is not a compound selection
	 */
	List<Selection<?>> getCompoundSelectionItems();

	/**
	 * Whether the selection item is a compound selection.
	 * 
	 * @return boolean indicating whether the selection is a compound selection
	 */
	boolean isCompoundSelection();
}

package javax.persistence.criteria;

import javax.persistence.metamodel.EntityType;

/**
 * The CriteriaDelete interface defines functionality for performing bulk delete operations using the Criteria API
 * 
 * Criteria API bulk delete operations map directly to database delete operations. The persistence context is not synchronized with the
 * result of the bulk delete.
 * 
 * A CriteriaDelete object must have a single root.
 * 
 * @param <T>
 *            the entity type that is the target of the delete
 * 
 * @since Java Persistence 2.1
 */
public interface CriteriaDelete<T> {
	/**
	 * Create and add a query root corresponding to the entity that is the target of the delete. A CriteriaDelete object has a single root,
	 * the object that is being deleted.
	 * 
	 * @param entityClass
	 *            the entity class
	 * @return query root corresponding to the given entity
	 */
	Root<T> from(Class<T> entityClass);

	/**
	 * Create and add a query root corresponding to the entity that is the target of the delete. A CriteriaDelete object has a single root,
	 * the object that is being deleted.
	 * 
	 * @param entity
	 *            metamodel entity representing the entity of type X
	 * @return query root corresponding to the given entity
	 */
	Root<T> from(EntityType<T> entity);

	/**
	 * Return the predicate that corresponds to the where clause restriction(s), or null if no restrictions have been specified.
	 * 
	 * @return where clause predicate
	 */
	Predicate getRestriction();

	/**
	 * Return the query root.
	 * 
	 * @return the query root
	 */
	Root<T> getRoot();

	/**
	 * Create a subquery of the query.
	 * 
	 * @param type
	 *            the subquery result type
	 * @param <U>
	 *            type of the sub query
	 * @return subquery
	 */
	<U> Subquery<U> subquery(Class<U> type);

	/**
	 * Modify the query to restrict the target of the deletion according to the specified boolean expression. Replaces the previously added
	 * restriction(s), if any.
	 * 
	 * @param restriction
	 *            a simple or compound boolean expression
	 * @return the modified query
	 */
	CriteriaDelete<T> where(Expression<Boolean> restriction);

	/**
	 * Modify the query to restrict the target of the deletion according to the conjunction of the specified restriction predicates.
	 * Replaces the previously added restriction(s), if any. If no restrictions are specified, any previously added restrictions are simply
	 * removed.
	 * 
	 * @param restrictions
	 *            zero or more restriction predicates
	 * @return the modified query
	 */
	CriteriaDelete<T> where(Predicate... restrictions);
}

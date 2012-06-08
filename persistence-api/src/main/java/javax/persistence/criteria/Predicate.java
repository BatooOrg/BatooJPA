package javax.persistence.criteria;

import java.util.List;

/**
 * The type of a simple or compound predicate: a conjunction or disjunction of restrictions. A simple predicate is considered to be a
 * conjunction with a single conjunct.
 */
public interface Predicate extends Expression<Boolean> {

	/**
	 * The operator for the predicates.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public static enum BooleanOperator {

		/**
		 * AND operator.
		 */
		AND,

		/**
		 * OR operator.
		 */
		OR
	}

	/**
	 * Return the top-level conjuncts or disjuncts of the predicate. Returns empty list if there are no top-level conjuncts or disjuncts of
	 * the predicate. Modifications to the list do not affect the query.
	 * 
	 * @return list of boolean expressions forming the predicate
	 */
	List<Expression<Boolean>> getExpressions();

	/**
	 * Return the boolean operator for the predicate. If the predicate is simple, this is AND.
	 * 
	 * @return boolean operator for the predicate
	 */
	BooleanOperator getOperator();

	/**
	 * Whether the predicate has been created from another predicate by applying the Predicate not() method or the CriteriaBuilder not()
	 * method.
	 * 
	 * @return boolean indicating if the predicate is a negated predicate
	 */
	boolean isNegated();

	/**
	 * Create a negation of the predicate.
	 * 
	 * @return negated predicate
	 */
	Predicate not();
}

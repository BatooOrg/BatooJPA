package javax.persistence.criteria;

/**
 * Defines the three types of joins.
 * 
 * Right outer joins and right outer fetch joins are not required to be supported in Java Persistence 2.1. Applications that use RIGHT join
 * types will not be portable.
 */
public enum JoinType {

	/**
	 * Inner join.
	 */
	INNER,
	/**
	 * Left outer join.
	 */
	LEFT,
	/**
	 * Right outer join.
	 */
	RIGHT
}

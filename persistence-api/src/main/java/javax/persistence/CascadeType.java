package javax.persistence;

/**
 * Defines the set of cascadable operations that are propagated to the associated entity. The value <code>cascade=ALL</code> is equivalent
 * to <code>cascade={PERSIST, MERGE, REMOVE, REFRESH, DETACH}</code>.
 * 
 * @since Java Persistence 1.0
 */
public enum CascadeType {

	/**
	 * Cascade all operations
	 */
	ALL,

	/**
	 * Cascade persist operation
	 */
	PERSIST,

	/**
	 * Cascade merge operation
	 */
	MERGE,

	/**
	 * Cascade remove operation
	 */
	REMOVE,

	/**
	 * Cascade refresh operation
	 */
	REFRESH,

	/**
	 * Cascade detach operation
	 * 
	 * @since Java Persistence 2.0
	 * 
	 */
	DETACH
}

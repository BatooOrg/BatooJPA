package javax.persistence;

/**
 * Defines strategies for fetching data from the database. The <code>EAGER</code> strategy is a requirement on the persistence provider
 * runtime that data must be eagerly fetched. The <code>LAZY</code> strategy is a hint to the persistence provider runtime that data should
 * be fetched lazily when it is first accessed. The implementation is permitted to eagerly fetch data for which the <code>LAZY</code>
 * strategy hint has been specified.
 * 
 * <pre>
 *   Example:
 *   &#064;Basic(fetch=LAZY)
 *   protected String getName() { return name; }
 * </pre>
 * 
 * @see Basic
 * @see ElementCollection
 * @see ManyToMany
 * @see OneToMany
 * @see ManyToOne
 * @see OneToOne
 * @since Java Persistence 1.0
 */
public enum FetchType {

	/**
	 * Defines that data can be lazily fetched.
	 */
	LAZY,

	/**
	 * Defines that data must be eagerly fetched.
	 */
	EAGER
}

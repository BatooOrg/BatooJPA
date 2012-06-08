package javax.persistence.spi;

/**
 * Loaded states.
 * 
 * @author hceylan
 * @since $version
 */
public enum LoadState {

	/**
	 * the state of the element is known to have been loaded
	 */
	LOADED,

	/**
	 * the state of the element is known not to have been loaded
	 */
	NOT_LOADED,

	/**
	 * the load state of the element cannot be determined
	 */
	UNKNOWN
}

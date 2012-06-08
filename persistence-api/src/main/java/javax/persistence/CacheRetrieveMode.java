package javax.persistence;

/**
 * Used as the value of the <code>javax.persistence.cache.retrieveMode</code> property to specify the behavior when data is retrieved by the
 * <code>find</code> methods and by queries.
 * 
 * @since Java Persistence 2.0
 */
public enum CacheRetrieveMode {

	/**
	 * Read entity data from the cache: this is the default behavior.
	 */
	USE,

	/**
	 * Bypass the cache: get data directly from the database.
	 */
	BYPASS
}

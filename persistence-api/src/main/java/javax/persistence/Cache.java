package javax.persistence;

/**
 * Interface used to interact with the second-level cache. If a cache is not in use, the methods of this interface have no effect, except
 * for contains, which returns false.
 */
public interface Cache {

	/**
	 * Whether the cache contains data for the given entity.
	 * 
	 * @param cls
	 *            entity class
	 * @param primaryKey
	 *            primary key
	 * @return boolean indicating whether the entity is in the cache
	 */
	public boolean contains(Class<?> cls, Object primaryKey);

	/**
	 * Remove the data for entities of the specified class (and its subclasses) from the cache.
	 * 
	 * @param cls
	 *            entity class
	 */
	public void evict(Class<?> cls);

	/**
	 * Remove the data for the given entity from the cache.
	 * 
	 * @param cls
	 *            entity class
	 * @param primaryKey
	 *            primary key
	 */
	public void evict(Class<?> cls, Object primaryKey);

	/**
	 * Clear the cache.
	 */
	public void evictAll();

	/**
	 * Return an object of the specified type to allow access to the provider-specific API. If the provider's Cache implementation does not
	 * support the specified class, the PersistenceException is thrown.
	 * 
	 * @param cls
	 *            the class of the object to be returned. This is normally either the underlying Cache implementation class or an interface
	 *            that it implements.
	 * @param <T>
	 *            type of the class
	 * @return an instance of the specified class
	 * @throws PersistenceException
	 *             if the provider does not support the call
	 */
	public <T> T unwrap(Class<T> cls);
}

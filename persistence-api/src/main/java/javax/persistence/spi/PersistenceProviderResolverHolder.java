package javax.persistence.spi;

/**
 * Holds the global PersistenceProviderResolver instance. If no PersistenceProviderResolver is set by the environment, the default
 * PersistenceProviderResolver is used.
 * 
 * This implementation is thread safe.
 */
public class PersistenceProviderResolverHolder {
	/**
	 * Returns the current persistence provider resolver.
	 * 
	 * @return persistence provider resolver in use
	 */
	public static PersistenceProviderResolver getPersistenceProviderResolver() {
		// TODO implement
		return null;
	}

	/**
	 * Defines the persistence provider resolver used.
	 * 
	 * @param resolver
	 *            PersistenceProviderResolver to be used
	 */
	public static void setPersistenceProviderResolver(PersistenceProviderResolver resolver) {
		// TODO implement
	}
}

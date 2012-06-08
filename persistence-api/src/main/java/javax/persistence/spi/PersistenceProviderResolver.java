package javax.persistence.spi;

import java.util.List;

/**
 * Determine the list of persistence providers available in the runtime environment.
 * 
 * Implementations must be thread-safe.
 * 
 * Note that the getPersistenceProviders method can potentially be called many times: it is recommended that the implementation of this
 * method make use of caching.
 */
public interface PersistenceProviderResolver {

	/**
	 * Clear cache of providers.
	 */
	void clearCachedProviders();

	/**
	 * Returns a list of the PersistenceProvider implementations available in the runtime environment.
	 * 
	 * @return list of the persistence providers available in the environment
	 */
	List<PersistenceProvider> getPersistenceProviders();
}

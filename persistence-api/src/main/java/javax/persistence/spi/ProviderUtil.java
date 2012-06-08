package javax.persistence.spi;

/**
 * Utility interface implemented by the persistence provider. This interface is invoked by the PersistenceUtil implementation to determine
 * the load status of an entity or entity attribute.
 */
public interface ProviderUtil {

	/**
	 * If the provider determines that the entity has been provided by itself and that the state of all attributes for which FetchType EAGER
	 * has been specified have been loaded, this method returns LoadState.LOADED. If the provider determines that the entity has been
	 * provided by itself and that not all attributes with FetchType EAGER have been loaded, this method returns LoadState.NOT_LOADED. If
	 * the provider cannot determine if the entity has been provided by itself, this method returns LoadState.UNKNOWN. The provider's
	 * implementation of this method must not obtain a reference to any attribute value, as this could trigger the loading of entity state
	 * if the entity has been provided by a different provider.
	 * 
	 * @param entity
	 *            whose loaded status is to be determined
	 * @return load status of the entity
	 */
	public LoadState isLoaded(Object entity);

	/**
	 * If the provider determines that the entity has been provided by itself and that the state of the specified attribute has been loaded,
	 * this method returns LoadState.LOADED. If the provider determines that the entity has been provided by itself and that either entity
	 * attributes with FetchType EAGER have not been loaded or that the state of the specified attribute has not been loaded, this methods
	 * returns LoadState.NOT_LOADED. If a provider cannot determine the load state, this method returns LoadState.UNKNOWN. The provider's
	 * implementation of this method must not obtain a reference to an attribute value, as this could trigger the loading of entity state if
	 * the entity has been provided by a different provider.
	 * 
	 * @param entity
	 *            the entity instance
	 * @param attributeName
	 *            name of attribute whose load status is to be determined
	 * @return load status of the attribute
	 */
	public LoadState isLoadedWithoutReference(Object entity, String attributeName);

	/**
	 * If the provider determines that the entity has been provided by itself and that the state of the specified attribute has been loaded,
	 * this method returns LoadState.LOADED. If a provider determines that the entity has been provided by itself and that either the entity
	 * attributes with FetchType EAGER have not been loaded or that the state of the specified attribute has not been loaded, this method
	 * returns return LoadState.NOT_LOADED. If the provider cannot determine the load state, this method returns LoadState.UNKNOWN. The
	 * provider's implementation of this method is permitted to obtain a reference to the attribute value. (This access is safe because
	 * providers which might trigger the loading of the attribute state will have already been determined by isLoadedWithoutReference. )
	 * 
	 * @param entity
	 *            the entity instance
	 * @param attributeName
	 *            name of attribute whose load status is to be determined
	 * @return load status of the attribute
	 */
	public LoadState isLoadedWithReference(Object entity, String attributeName);
}

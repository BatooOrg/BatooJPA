package javax.persistence.spi;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * Interface implemented by the persistence provider.
 * 
 * It is invoked by the container in Java EE environments and by the Persistence class in Java SE environments to create an
 * EntityManagerFactory.
 */
public interface PersistenceProvider {
	/**
	 * Called by the container when an EntityManagerFactory is to be created.
	 * 
	 * @param info
	 *            metadata for use by the persistence provider
	 * @param map
	 *            a Map of integration-level properties for use by the persistence provider (may be null if no properties are specified). If
	 *            a Bean Validation provider is present in the classpath, the container must pass the ValidatorFactory instance in the map
	 *            with the key "javax.persistence.validation.factory". If the containing archive is a bean archive, the container must pass
	 *            the BeanManager instance in the map with the key "javax.persistence.bean.manager".
	 * @return EntityManagerFactory for the persistence unit specified by the metadata
	 */
	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map<?, ?> map);

	/**
	 * Called by Persistence class when an EntityManagerFactory is to be created.
	 * 
	 * @param emName
	 *            the name of the persistence unit
	 * @param map
	 *            a Map of properties for use by the persistence provider. These properties may be used to override the values of the
	 *            corresponding elements in the persistence.xml file or specify values for properties not specified in the persistence.xml
	 *            (and may be null if no properties are specified).
	 * @return EntityManagerFactory for the persistence unit, or null if the provider is not the right provider
	 */
	public EntityManagerFactory createEntityManagerFactory(String emName, Map<?, ?> map);

	/**
	 * Return the utility interface implemented by the persistence provider.
	 * 
	 * @return ProviderUtil interface
	 */
	public ProviderUtil getProviderUtil();
}

package javax.persistence;

import java.util.Map;

/**
 * Provider-independent class
 * 
 * Class that is used to obtain an EntityManagerFactory in Java SE environments.
 * 
 * Class that is used to obtain an instance of PersistenceUtil in Java EE and Java SE environments.
 */
public class Persistence {

	/**
	 * Create and return an EntityManagerFactory for the named persistence unit.
	 * 
	 * Use of this method is not required to be supported in Java EE container environments.
	 * 
	 * @param persistenceUnitName
	 *            the name of the persistence unit
	 * @return the factory that creates EntityManagers configured according to the specified persistence unit
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
		return null;
	}

	/**
	 * Create and return an EntityManagerFactory for the named persistence unit using the given properties.
	 * 
	 * Use of this method is not required to be supported in Java EE container environments.
	 * 
	 * @param persistenceUnitName
	 *            the name of the persistence unit
	 * @param properties
	 *            additional properties to use when creating the factory. The values of these properties override any values that may have
	 *            been configured elsewhere.
	 * @return the factory that creates EntityManagers configured according to the specified persistence unit
	 */
	public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map<?, ?> properties) {
		return null;
	}

	/**
	 * Returns the {@link PersistenceUtil} instance.
	 * 
	 * @return the {@link PersistenceUtil} instance
	 * 
	 */
	public static PersistenceUtil getPersistenceUtil() {
		return null;
	}
}

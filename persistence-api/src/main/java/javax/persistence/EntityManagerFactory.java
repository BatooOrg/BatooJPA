package javax.persistence;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

/**
 * Interface used to interact with the entity manager factory for the persistence unit.
 */
public interface EntityManagerFactory {

	/**
	 * Close the factory, releasing any resources that it holds. After a factory instance has been closed, all methods invoked on it will
	 * throw the IllegalStateException, except for isOpen, which will return false. Once an EntityManagerFactory has been closed, all its
	 * entity managers are considered to be in the closed state.
	 * 
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public void close();

	/**
	 * Create a new application-managed EntityManager. This method returns a new EntityManager instance each time it is invoked. The isOpen
	 * method will return true on the returned instance.
	 * 
	 * @return entity manager instance
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public EntityManager createEntityManager();

	/**
	 * Create a new application-managed EntityManager with the specified Map of properties. This method returns a new EntityManager instance
	 * each time it is invoked. The isOpen method will return true on the returned instance.
	 * 
	 * @param map
	 *            properties for entity manager
	 * @return entity manager instance
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public EntityManager createEntityManager(Map<String, Object> map);

	/**
	 * Access the cache that is associated with the entity manager factory (the "second level cache").
	 * 
	 * @return instance of the Cache interface
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public Cache getCache();

	/**
	 * Return an instance of CriteriaBuilder for the creation of CriteriaQuery objects.
	 * 
	 * @return CriteriaBuilder instance
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public CriteriaBuilder getCriteriaBuilder();

	/**
	 * Return an instance of Metamodel interface for access to the metamodel of the persistence unit.
	 * 
	 * @return Metamodel instance
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public Metamodel getMetamodel();

	/**
	 * Return interface providing access to utility methods for the persistence unit.
	 * 
	 * @return PersistenceUnitUtil interface
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public PersistenceUnitUtil getPersistenceUnitUtil();

	/**
	 * Get the properties and associated values that are in effect for the entity manager factory. Changing the contents of the map does not
	 * change the configuration in effect.
	 * 
	 * @return properties
	 * @throws IllegalStateException
	 *             if the entity manager factory has been closed
	 */
	public Map<String, Object> getProperties();

	/**
	 * Indicates whether the factory is open. Returns true until the factory has been closed.
	 * 
	 * @return boolean indicating whether the factory is open
	 */
	public boolean isOpen();

	/**
	 * Return an object of the specified type to allow access to the provider-specific API. If the provider's EntityManagerFactory
	 * implementation does not support the specified class, the PersistenceException is thrown.
	 * 
	 * @param cls
	 *            the class of the object to be returned. This is normally either the underlying EntityManagerFactory implementation class
	 *            or an interface that it implements.
	 * @param <T>
	 *            the type of the class
	 * @return an instance of the specified class
	 * @throws PersistenceException
	 *             if the provider does not support the call
	 */
	public <T> T unwrap(Class<T> cls);
}

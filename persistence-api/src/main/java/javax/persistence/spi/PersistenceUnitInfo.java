package javax.persistence.spi;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

/**
 * Interface implemented by the container and used by the persistence provider when creating an EntityManagerFactory.
 * 
 */
public interface PersistenceUnitInfo {

	/**
	 * Add a transformer supplied by the provider that will be called for every new class definition or class redefinition that gets loaded
	 * by the loader returned by the PersistenceUnitInfo.getClassLoader method. The transformer has no effect on the result returned by the
	 * PersistenceUnitInfo.getNewTempClassLoader method. Classes are only transformed once within the same classloading scope, regardless of
	 * how many persistence units they may be a part of.
	 * 
	 * @param transformer
	 *            provider-supplied transformer that the container invokes at class-(re)definition time
	 */
	public void addTransformer(ClassTransformer transformer);

	/**
	 * Returns whether classes in the root of the persistence unit that have not been explicitly listed are to be included in the set of
	 * managed classes. This value corresponds to the exclude-unlisted-classes element in the persistence.xml file.
	 * 
	 * @return whether classes in the root of the persistence unit that have not been explicitly listed are to be included in the set of
	 *         managed classes
	 */
	public boolean excludeUnlistedClasses();

	/**
	 * Returns ClassLoader that the provider may use to load any classes, resources, or open URLs.
	 * 
	 * @return ClassLoader that the provider may use to load any classes, resources, or open URLs
	 */
	public ClassLoader getClassLoader();

	/**
	 * Returns a list of URLs for the jar files or exploded jar file directories that the persistence provider must examine for managed
	 * classes of the persistence unit. Each URL corresponds to a jar-file element in the persistence.xml file. A URL will either be a file:
	 * URL referring to a jar file or referring to a directory that contains an exploded jar file, or some other URL from which an
	 * InputStream in jar format can be obtained.
	 * 
	 * @return a list of URL objects referring to jar files or directories
	 */
	public List<URL> getJarFileUrls();

	/**
	 * Returns the JTA-enabled data source to be used by the persistence provider. The data source corresponds to the jta-data-source
	 * element in the persistence.xml file or is provided at deployment or by the container.
	 * 
	 * @return the JTA-enabled data source to be used by the persistence provider
	 */
	public DataSource getJtaDataSource();

	/**
	 * Returns the list of the names of the classes that the persistence provider must add to its set of managed classes. Each name
	 * corresponds to a named class element in the persistence.xml file.
	 * 
	 * @return the list of the names of the classes that the persistence provider must add to its set of managed classes
	 */
	public List<String> getManagedClassNames();

	/**
	 * Returns the list of the names of the mapping files that the persistence provider must load to determine the mappings for the entity
	 * classes. The mapping files must be in the standard XML mapping format, be uniquely named and be resource-loadable from the
	 * application classpath. Each mapping file name corresponds to a mapping-file element in the persistence.xml file.
	 * 
	 * @return the list of mapping file names that the persistence provider must load to determine the mappings for the entity classes
	 */
	public List<String> getMappingFileNames();

	/**
	 * Return a new instance of a ClassLoader that the provider may use to temporarily load any classes, resources, or open URLs. The scope
	 * and classpath of this loader is exactly the same as that of the loader returned by PersistenceUnitInfo.getClassLoader. None of the
	 * classes loaded by this class loader will be visible to application components. The provider may only use this ClassLoader within the
	 * scope of the createContainerEntityManagerFactory call.
	 * 
	 * @return temporary ClassLoader with same visibility as current loader
	 */
	public ClassLoader getNewTempClassLoader();

	/**
	 * Returns the non-JTA-enabled data source to be used by the persistence provider for accessing data outside a JTA transaction. The data
	 * source corresponds to the named non-jta-data-source element in the persistence.xml file or provided at deployment or by the
	 * container.
	 * 
	 * @return the non-JTA-enabled data source to be used by the persistence provider for accessing data outside a JTA transaction
	 */
	public DataSource getNonJtaDataSource();

	/**
	 * Returns the fully qualified name of the persistence provider implementation class. Corresponds to the provider element in the
	 * persistence.xml file.
	 * 
	 * @return the fully qualified name of the persistence provider implementation class
	 */
	public String getPersistenceProviderClassName();

	/**
	 * Returns the name of the persistence unit. Corresponds to the name attribute in the persistence.xml file.
	 * 
	 * @return the name of the persistence unit
	 */
	public String getPersistenceUnitName();

	/**
	 * Returns the URL for the jar file or directory that is the root of the persistence unit. (If the persistence unit is rooted in the
	 * WEB-INF/classes directory, this will be the URL of that directory.) The URL will either be a file: URL referring to a jar file or
	 * referring to a directory that contains an exploded jar file, or some other URL from which an InputStream in jar format can be
	 * obtained.
	 * 
	 * @return a URL referring to a jar file or directory
	 */
	public URL getPersistenceUnitRootUrl();

	/**
	 * Returns the schema version of the persistence.xml file.
	 * 
	 * @return persistence.xml schema version
	 */
	public String getPersistenceXMLSchemaVersion();

	/**
	 * Returns a properties object. Each property corresponds to a property element in the persistence.xml file or to a property set by the
	 * container.
	 * 
	 * @return Properties object
	 */
	public Properties getProperties();

	/**
	 * Returns the specification of how the provider must use a second-level cache for the persistence unit. The result of this method
	 * corresponds to the shared-cache-mode element in the persistence.xml file.
	 * 
	 * @return the second-level cache mode that must be used by the provider for the persistence unit
	 */
	public SharedCacheMode getSharedCacheMode();

	/**
	 * Returns the transaction type of the entity managers created by the EntityManagerFactory. The transaction type corresponds to the
	 * transaction-type attribute in the persistence.xml file.
	 * 
	 * @return transaction type of the entity managers created by the EntityManagerFactory
	 */
	public PersistenceUnitTransactionType getTransactionType();

	/**
	 * Returns the validation mode to be used by the persistence provider for the persistence unit. The validation mode corresponds to the
	 * validation-mode element in the persistence.xml file.
	 * 
	 * @return the validation mode to be used by the persistence provider for the persistence unit
	 */
	public ValidationMode getValidationMode();
}

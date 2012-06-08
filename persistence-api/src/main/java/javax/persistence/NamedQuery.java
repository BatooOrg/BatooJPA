package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.LockModeType.NONE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies a static, named query in the Java Persistence query language. Query names are scoped to the persistence unit. The
 * <code>NamedQuery</code> annotation can be applied to an entity or mapped superclass.
 * 
 * <p>
 * The following is an example of the definition of a named query in the Java Persistence query language:
 * 
 * <pre>
 *    &#064;NamedQuery(
 *            name="findAllCustomersWithName",
 *            query="SELECT c FROM Customer c WHERE c.name LIKE :custName"
 *    )
 * </pre>
 * 
 * <p>
 * The following is an example of the use of a named query:
 * 
 * <pre>
 *    &#064;PersistenceContext
 *    public EntityManager em;
 *    ...
 *    customers = em.createNamedQuery("findAllCustomersWithName")
 *            .setParameter("custName", "Smith")
 *            .getResultList();
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface NamedQuery {

	/**
	 * (Optional) Query properties and hints. May include vendor-specific query hints.
	 */
	QueryHint[] hints() default {};

	/**
	 * (Optional) The lock mode type to use in query execution. If a <code>lockMode</code> other than <code>LockModeType.NONE</code> is
	 * specified, the query must be executed in a transaction.
	 * 
	 * @since Java Persistence 2.0
	 */
	LockModeType lockMode() default NONE;

	/**
	 * (Required) The name used to refer to the query with the {@link EntityManager} methods that create query objects.
	 */
	String name();

	/**
	 * (Required) The query string in the Java Persistence query language.
	 */
	String query();
}

package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies a named native SQL query. Query names are scoped to the persistence unit. The <code>NamedNativeQuery</code> annotation can be
 * applied to an entity or mapped superclass.
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface NamedNativeQuery {

	/**
	 * Query properties and hints. (May include vendor-specific query hints.)
	 * */
	QueryHint[] hints() default {};

	/**
	 * The name used to refer to the query with the {@link EntityManager} methods that create query objects.
	 */
	String name();

	/**
	 * The SQL query string.
	 */
	String query();

	/**
	 * The class of the result.
	 */
	Class<?> resultClass() default void.class;

	/**
	 * The name of a {@link SqlResultSetMapping}, as defined in metadata.
	 */
	String resultSetMapping() default "";
}

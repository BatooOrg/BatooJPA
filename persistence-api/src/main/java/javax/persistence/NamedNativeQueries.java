package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to specify multiple native SQL named queries. Query names are scoped to the persistence unit. The <code>NamedNativeQueries</code>
 * annotation can be applied to an entity or mapped superclass.
 * 
 * @see NamedNativeQuery
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface NamedNativeQueries {

	/**
	 * (Required) Array of <code>NamedNativeQuery</code> annotations.
	 */
	NamedNativeQuery[] value();
}

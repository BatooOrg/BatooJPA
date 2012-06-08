package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies multiple named Java Persistence query language queries. Query names are scoped to the persistence unit. The
 * <code>NamedQueries</code> annotation can be applied to an entity or mapped superclass.
 * 
 * @see NamedQuery
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface NamedQueries {

	/**
	 * (Required) An array of <code>NamedQuery</code> annotations.
	 */
	NamedQuery[] value();
}

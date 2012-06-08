package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Expresses a dependency on an {@link EntityManagerFactory} and its associated persistence unit.
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface PersistenceUnit {
	/**
	 * (Optional) The name by which the entity manager factory is to be accessed in the environment referencing context; not needed when
	 * dependency injection is used.
	 */
	String name() default "";

	/**
	 * (Optional) The name of the persistence unit as defined in the <code>persistence.xml</code> file. If specified, the persistence unit
	 * for the entity manager factory that is accessible in JNDI must have the same name.
	 */
	String unitName() default "";
}

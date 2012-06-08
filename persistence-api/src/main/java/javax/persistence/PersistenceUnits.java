package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares one or more {@link PersistenceUnit} annotations.
 * 
 * @since Java Persistence 1.0
 */

@Target({ TYPE })
@Retention(RUNTIME)
public @interface PersistenceUnits {
	/**
	 * (Required) One or more {@link PersistenceUnit} annotations.
	 */
	PersistenceUnit[] value();
}

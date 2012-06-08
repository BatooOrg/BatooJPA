package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the callback listener classes to be used for an entity or mapped superclass. This annotation may be applied to an entity class
 * or mapped superclass.
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface EntityListeners {

	/** The callback listener classes */
	Class<?>[] value();
}

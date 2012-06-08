package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares one or more {@link PersistenceContext} annotations. It is used to express a dependency on container-managed entity manager
 * persistence contexts.
 * 
 * @see PersistenceContext
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface PersistenceContexts {

	/** (Required) One or more <code>PersistenceContext</code> annotations. */
	PersistenceContext[] value();

}

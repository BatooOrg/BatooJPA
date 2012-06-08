package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines the inheritance strategy to be used for an entity class hierarchy. It is specified on the entity class that is the root of the
 * entity class hierarchy. If the <code>Inheritance</code> annotation is not specified or if no inheritance type is specified for an entity
 * class hierarchy, the <code>SINGLE_TABLE<code> mapping strategy is used.
 * 
 * <pre>
 * 
 *   Example:
 * 
 *   &#064;Entity
 *   &#064;Inheritance(strategy=JOINED)
 *   public class Customer { ... }
 * 
 *   &#064;Entity
 *   public class ValuedCustomer extends Customer { ... }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Inheritance {

	/**
	 * The strategy to be used for the entity inheritance hierarchy.
	 * 
	 */
	InheritanceType strategy() default SINGLE_TABLE;
}

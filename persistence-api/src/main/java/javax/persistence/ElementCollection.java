package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.FetchType.LAZY;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a collection of instances of a basic type or embeddable class. Must be specified if the collection is to be mapped by means of a
 * collection table.
 * 
 * <pre>
 *    Example:
 * 
 *    &#064;Entity public class Person {
 *       &#064;Id protected String ssn;
 *       protected String name;
 *       ...
 *       &#064;ElementCollection
 *       protected Set&#060;String&#062; nickNames = new HashSet();
 *         ...
 *    }
 * </pre>
 * 
 * @since Java Persistence 2.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ElementCollection {

	/**
	 * (Optional) Whether the collection should be lazily loaded or must be eagerly fetched. The EAGER strategy is a requirement on the
	 * persistence provider runtime that the collection elements must be eagerly fetched. The LAZY strategy is a hint to the persistence
	 * provider runtime.
	 */
	FetchType fetch() default LAZY;

	/**
	 * (Optional) The basic or embeddable class that is the element type of the collection. This element is optional only if the collection
	 * field or property is defined using Java generics, and must be specified otherwise. It defaults to the paramterized type of the
	 * collection when defined using generics.
	 */
	Class<?> targetClass() default void.class;
}

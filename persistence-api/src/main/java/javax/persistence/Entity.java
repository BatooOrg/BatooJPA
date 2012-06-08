package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the class is an entity. This annotation is applied to the entity class.
 * 
 * @since Java Persistence 1.0
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {

	/**
	 * (Optional) The entity name. Defaults to the unqualified name of the entity class. This name is used to refer to the entity in
	 * queries. The name must not be a reserved literal in the Java Persistence query language.
	 */
	String name() default "";
}

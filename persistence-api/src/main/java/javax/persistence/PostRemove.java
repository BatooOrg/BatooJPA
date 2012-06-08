package javax.persistence;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is used to specify callback methods for the corresponding lifecycle event. This annotation may be applied to methods of an entity class,
 * a mapped superclass, or a callback listener class.
 * 
 * @since Java Persistence 1.0
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface PostRemove {}

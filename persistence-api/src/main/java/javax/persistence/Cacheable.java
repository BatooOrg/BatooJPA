package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies whether an entity should be cached if caching is enabled when the value of the <code>persistence.xml</code> caching element is
 * <code>ENABLE_SELECTIVE</code> or <code>DISABLE_SELECTIVE</code>. The value of the <code>Cacheable</code> annotation is inherited by
 * subclasses; it can be overridden by specifying <code>Cacheable</code> on a subclass.
 * 
 * <p>
 * <code>Cacheable(false)</code> means that the entity and its state must not be cached by the provider.
 * 
 * @since Java Persistence 2.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Cacheable {

	/**
	 * (Optional) Whether or not the entity should be cached.
	 */
	boolean value() default true;
}

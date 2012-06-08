package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is used to define one or more {@link SqlResultSetMapping} annotations.
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface SqlResultSetMappings {

	/**
	 * One or more <code>SqlResultSetMapping</code> annotations.
	 */
	SqlResultSetMapping[] value();
}

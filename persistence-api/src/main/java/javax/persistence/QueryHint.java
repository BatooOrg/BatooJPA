package javax.persistence;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to supply a query property or hint to the {@link NamedQuery} or {@link NamedNativeQuery} annotation.
 * 
 * <p>
 * Vendor-specific hints that are not recognized by a provider are ignored.
 * 
 * @since Java Persistence 1.0
 */
@Target({})
@Retention(RUNTIME)
public @interface QueryHint {

	/**
	 * Name of the hint.
	 */
	String name();

	/**
	 * Value of the hint.
	 */
	String value();
}

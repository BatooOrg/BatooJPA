package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The <code>Converter</code> annotation specifies that the annotated class is a converter and defines its scope. A converter class must be
 * annotated with the Converter annotation or defined in the object/relational mapping descriptor as a converter.
 * 
 * @since Java Persistence 2.1
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Converter {

	/**
	 * (Optional) If the autoApply element is specified as true, the persistence provider must automatically apply the converter to all
	 * mapped attributes of the specified target type for all entities in the persistence unit except for attributes for which conversion is
	 * overridden by means of the Convert annotation (or XML equivalent).
	 */
	boolean autoApply() default false;
}

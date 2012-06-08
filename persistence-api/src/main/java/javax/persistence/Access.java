package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to specify an access type to be applied to an entity class, mapped superclass, or embeddable class, or to a specific attribute of
 * such a class.
 * 
 * @since Java Persistence 2.0
 */
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface Access {

	/**
	 * (Required) Specification of field- or property-based access.
	 */
	AccessType value();
}

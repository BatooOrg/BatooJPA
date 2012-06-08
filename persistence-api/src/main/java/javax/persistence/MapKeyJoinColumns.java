package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Supports composite map keys that reference entities.
 * <p>
 * The <code>MapKeyJoinColumns</code> annotation groups <code>MapKeyJoinColumn</code> annotations. When the <code>MapKeyJoinColumns</code>
 * annotation is used, both the <code>name</code> and the <code>referencedColumnName</code> elements must be specified in each of the
 * grouped <code>MapKeyJoinColumn</code> annotations.
 * 
 * @see MapKeyJoinColumn
 * 
 * @since Java Persistence 2.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface MapKeyJoinColumns {

	/**
	 * (Required) The map key join columns that are used to map to the entity that is the map key.
	 * 
	 */
	MapKeyJoinColumn[] value();
}

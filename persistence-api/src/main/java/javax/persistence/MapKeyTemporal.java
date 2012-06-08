package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation must be specified for persistent map keys of type {@link java.util.Date} and {@link java.util.Calendar}. It may only be
 * specified for map keys of these types.
 * 
 * <p>
 * The <code>MapKeyTemporal</code> annotation can be applied to an element collection or relationship of type <code>java.util.Map</code> in
 * conjunction with the <code>ElementCollection</code>, <code>OneToMany</code>, or <code>ManyToMany</code> annotation.
 * 
 * <pre>
 *     Example:
 * 
 *     &#064;OneToMany
 *     &#064;MapKeyTemporal(DATE)
 *     protected java.util.Map&#060;java.util.Date, Employee&#062; employees;
 * </pre>
 * 
 * @since Java Persistence 2.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface MapKeyTemporal {

	/**
	 * (Required) The type used in mapping <code>java.util.Date</code> or <code>java.util.Calendar</code>.
	 */
	TemporalType value();
}

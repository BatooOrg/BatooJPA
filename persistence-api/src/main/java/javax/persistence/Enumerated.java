package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.EnumType.ORDINAL;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a persistent property or field should be persisted as a enumerated type. The <code>Enumerated</code> annotation may be
 * used in conjunction with the <code>Basic</code> annotation, or in conjunction with the <code>ElementCollection</code> annotation when the
 * element collection value is of basic type. If the enumerated type is not specified or the <code>Enumerated</code> annotation is not used,
 * the <code>EnumType</code> value is assumed to be <code>ORDINAL<code>.
 * 
 * <pre>
 *   Example:
 * 
 *   public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}
 * 
 *   public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}
 * 
 *   &#064;Entity public class Employee {
 *       public EmployeeStatus getStatus() {...}
 *       ...
 *       &#064;Enumerated(STRING)
 *       public SalaryRate getPayScale() {...}
 *       ...
 *   }
 * </pre>
 * 
 * @see Basic
 * @see ElementCollection
 * 
 * @since Java Persistence 1.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Enumerated {

	/**
	 * (Optional) The type used in mapping an enum type.
	 * 
	 */
	EnumType value() default ORDINAL;
}

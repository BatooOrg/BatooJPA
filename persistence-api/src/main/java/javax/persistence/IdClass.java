package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies a composite primary key class that is mapped to multiple fields or properties of the entity.
 * 
 * <p>
 * The names of the fields or properties in the primary key class and the primary key fields or properties of the entity must correspond and
 * their types must be the same.
 * 
 * <pre>
 * 
 *   Example:
 * 
 *   &#064;IdClass(com.acme.EmployeePK.class)
 *   &#064;Entity
 *   public class Employee {
 *      &#064;Id String empName;
 *      &#064;Id Date birthDay;
 *      ...
 *   }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface IdClass {

	/**
	 * Primary key class
	 */
	Class<?> value();
}

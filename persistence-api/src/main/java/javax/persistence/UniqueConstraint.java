package javax.persistence;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a unique constraint is to be included in the generated DDL for a primary or secondary table.
 * 
 * <pre>
 *    Example:
 *    &#064;Entity
 *    &#064;Table(
 *        name="EMPLOYEE",
 *        uniqueConstraints=
 *            &#064;UniqueConstraint(columnNames={"EMP_ID", "EMP_NAME"})
 *    )
 *    public class Employee { ... }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target({})
@Retention(RUNTIME)
public @interface UniqueConstraint {
	/**
	 * (Required) An array of the column names that make up the constraint.
	 */
	String[] columnNames();

	/**
	 * (Optional) Constraint name. A provider-chosen name will be chosen if a name is not specified.
	 * 
	 * @since Java Persistence 2.0
	 */
	String name() default "";
}

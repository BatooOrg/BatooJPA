package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to override mappings of multiple properties or fields.
 * 
 * <pre>
 * 
 *    Example:
 * 
 *    &#064;Embedded
 *    &#064;AttributeOverrides({
 *            &#064;AttributeOverride(name="startDate",
 *                               column=&#064;Column("EMP_START")),
 *            &#064;AttributeOverride(name="endDate",
 *                               column=&#064;Column("EMP_END"))
 *    })
 *    public EmploymentPeriod getEmploymentPeriod() { ... }
 * 
 * </pre>
 * 
 * 
 * @see AttributeOverride
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface AttributeOverrides {

	/**
	 * (Required) One or more field or property mapping overrides.
	 * */
	AttributeOverride[] value();
}

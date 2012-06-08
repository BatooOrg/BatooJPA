package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies a persistent field or property of an entity whose value is an instance of an embeddable class. The embeddable class must be
 * annotated as {@link Embeddable}.
 * 
 * <p>
 * The <code>AttributeOverride</code>, <code>AttributeOverrides</code>, <code>AssociationOverride</code>, and
 * <code>AssociationOverrides</code> annotations may be used to override mappings declared or defaulted by the embeddable class.
 * 
 * <pre>
 *   Example:
 * 
 *   &#064;Embedded
 *   &#064;AttributeOverrides({
 *       &#064;AttributeOverride(name="startDate", column=&#064;Column("EMP_START")),
 *       &#064;AttributeOverride(name="endDate", column=&#064;Column("EMP_END"))
 *   })
 *   public EmploymentPeriod getEmploymentPeriod() { ... }
 * </pre>
 * 
 * @see Embeddable
 * @see AttributeOverride
 * @see AttributeOverrides
 * @see AssociationOverride
 * @see AssociationOverrides
 * 
 * @since Java Persistence 1.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Embedded {}

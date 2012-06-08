package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Groups {@link PrimaryKeyJoinColumn} annotations. It is used to map composite foreign keys.
 * 
 * <pre>
 *    Example: ValuedCustomer subclass
 * 
 *    &#064;Entity
 *    &#064;Table(name="VCUST")
 *    &#064;DiscriminatorValue("VCUST")
 *    &#064;PrimaryKeyJoinColumns({
 *        &#064;PrimaryKeyJoinColumn(name="CUST_ID",
 *            referencedColumnName="ID"),
 *        &#064;PrimaryKeyJoinColumn(name="CUST_TYPE",
 *            referencedColumnName="TYPE")
 *    })
 *    public class ValuedCustomer extends Customer { ... }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface PrimaryKeyJoinColumns {
	/**
	 * One or more <code>PrimaryKeyJoinColumn</code> annotations.
	 */
	PrimaryKeyJoinColumn[] value();
}

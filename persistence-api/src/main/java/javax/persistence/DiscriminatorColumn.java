package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.DiscriminatorType.STRING;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the discriminator column for the <code>SINGLE_TABLE</code> and <code>JOINED</code> {@link Inheritance} mapping strategies.
 * 
 * <p>
 * The strategy and the discriminator column are only specified in the root of an entity class hierarchy or subhierarchy in which a
 * different inheritance strategy is applied
 * 
 * <p>
 * If the <code>DiscriminatorColumn</code> annotation is missing, and a discriminator column is required, the name of the discriminator
 * column defaults to <code>"DTYPE"</code> and the discriminator type to {@link DiscriminatorType#STRING DiscriminatorType.STRING}.
 * 
 * <pre>
 *     Example:
 * 
 *     &#064;Entity
 *     &#064;Table(name="CUST")
 *     &#064;Inheritance(strategy=SINGLE_TABLE)
 *     &#064;DiscriminatorColumn(name="DISC", discriminatorType=STRING, length=20)
 *     public class Customer { ... }
 * 
 *     &#064;Entity
 *     public class ValuedCustomer extends Customer { ... }
 * </pre>
 * 
 * @see DiscriminatorValue
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface DiscriminatorColumn {

	/**
	 * (Optional) The SQL fragment that is used when generating the DDL for the discriminator column.
	 * <p>
	 * Defaults to the provider-generated SQL to create a column of the specified discriminator type.
	 */
	String columnDefinition() default "";

	/**
	 * (Optional) The type of object/column to use as a class discriminator. Defaults to {@link DiscriminatorType#STRING
	 * DiscriminatorType.STRING}.
	 */
	DiscriminatorType discriminatorType() default STRING;

	/**
	 * (Optional) The column length for String-based discriminator types. Ignored for other discriminator types.
	 */
	int length() default 31;

	/**
	 * (Optional) The name of column to be used for the discriminator.
	 */
	String name() default "DTYPE";
}

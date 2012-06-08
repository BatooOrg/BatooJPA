package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.GenerationType.AUTO;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Provides for the specification of generation strategies for the values of primary keys.
 * 
 * <p>
 * The <code>GeneratedValue</code> annotation may be applied to a primary key property or field of an entity or mapped superclass in
 * conjunction with the {@link Id} annotation. The use of the <code>GeneratedValue</code> annotation is only required to be supported for
 * simple primary keys. Use of the <code>GeneratedValue</code> annotation is not supported for derived primary keys.
 * 
 * <pre>
 * 
 *     Example 1:
 * 
 *     &#064;Id
 *     &#064;GeneratedValue(strategy=SEQUENCE, generator="CUST_SEQ")
 *     &#064;Column(name="CUST_ID")
 *     public Long getId() { return id; }
 * 
 *     Example 2:
 * 
 *     &#064;Id
 *     &#064;GeneratedValue(strategy=TABLE, generator="CUST_GEN")
 *     &#064;Column(name="CUST_ID")
 *     Long id;
 * </pre>
 * 
 * @see Id
 * @see TableGenerator
 * @see SequenceGenerator
 * 
 * @since Java Persistence 1.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface GeneratedValue {

	/**
	 * (Optional) The name of the primary key generator to use as specified in the {@link SequenceGenerator} or {@link TableGenerator}
	 * annotation.
	 * <p>
	 * Defaults to the id generator supplied by persistence provider.
	 */
	String generator() default "";

	/**
	 * (Optional) The primary key generation strategy that the persistence provider must use to generate the annotated entity primary key.
	 * 
	 */
	GenerationType strategy() default AUTO;
}

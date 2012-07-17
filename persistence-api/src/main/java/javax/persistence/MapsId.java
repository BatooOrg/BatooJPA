package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Designates a <code>ManyToOne</code> or <code>OneToOne</code> relationship attribute that provides the mapping for an {@link EmbeddedId}
 * primary key, an attribute within an <code>EmbeddedId</code> primary key, or a simple primary key of the parent entity. The
 * <code>value</code> element specifies the attribute within a composite key to which the relationship attribute corresponds. If the
 * entity's primary key is of the same Java type as the primary key of the entity referenced by the relationship, the value attribute is not
 * specified.
 * 
 * <pre>
 *    Example:
 * 
 *    // parent entity has simple primary key
 * 
 *    &#064;Entity
 *    public class Employee {
 *       &#064;Id long empId;
 *       String name;
 *       ...
 *    }
 * 
 *    // dependent entity uses EmbeddedId for composite key
 * 
 *    &#064;Embeddable
 *    public class DependentId {
 *       String name;
 *       long empid;   // corresponds to primary key type of Employee
 *    }
 * 
 *    &#064;Entity
 *    public class Dependent {
 *       &#064;EmbeddedId DependentId id;
 *        ...
 *       &#064;MapsId("empid")  //  maps the empid attribute of embedded id
 *       &#064;ManyToOne Employee emp;
 *    }
 * </pre>
 * 
 * @since Java Persistence 2.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface MapsId {

	/**
	 * (Optional) The name of the attribute within the composite key to which the relationship attribute corresponds. If not supplied, the
	 * relationship maps the entity's primary key.
	 */
	String value() default "";
}

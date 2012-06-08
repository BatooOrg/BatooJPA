package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines mapping for composite foreign keys. This annotation groups <code>JoinColumn</code> annotations for the same relationship.
 * 
 * <p>
 * When the <code>JoinColumns</code> annotation is used, both the <code>name</code> and the <code>referencedColumnName</code> elements must
 * be specified in each such <code>JoinColumn</code> annotation.
 * 
 * <pre>
 * 
 *    Example:
 *    &#064;ManyToOne
 *    &#064;JoinColumns({
 *        &#064;JoinColumn(name="ADDR_ID", referencedColumnName="ID"),
 *        &#064;JoinColumn(name="ADDR_ZIP", referencedColumnName="ZIP")
 *    })
 *    public Address getAddress() { return address; }
 * </pre>
 * 
 * @see JoinColumn
 * 
 * @since Java Persistence 1.0
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface JoinColumns {

	/**
	 * The join columns that map the relationship.
	 */
	JoinColumn[] value();
}

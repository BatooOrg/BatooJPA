package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the primary table for the annotated entity. Additional tables may be specified using {@link SecondaryTable} or
 * {@link SecondaryTables} annotation.
 * <p/>
 * If no <code>Table</code> annotation is specified for an entity class, the default values apply.
 * 
 * <pre>
 *    Example:
 * 
 *    &#064;Entity
 *    &#064;Table(name="CUST", schema="RECORDS")
 *    public class Customer { ... }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {
	/**
	 * (Optional) The catalog of the table.
	 * <p/>
	 * Defaults to the default catalog.
	 */
	String catalog() default "";

	/**
	 * (Optional) The name of the table.
	 * <p/>
	 * Defaults to the entity name.
	 */
	String name() default "";

	/**
	 * (Optional) The schema of the table.
	 * <p/>
	 * Defaults to the default schema for user.
	 */
	String schema() default "";

	/**
	 * (Optional) Unique constraints that are to be placed on the table. These are only used if table generation is in effect. These
	 * constraints apply in addition to any constraints specified by the <code>Column</code> and <code>JoinColumn</code> annotations and
	 * constraints entailed by primary key mappings.
	 * <p/>
	 * Defaults to no additional constraints.
	 */
	UniqueConstraint[] uniqueConstraints() default {};
}

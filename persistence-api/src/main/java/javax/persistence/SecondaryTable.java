package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies a secondary table for the annotated entity class. Specifying one or more secondary tables indicates that the data for the
 * entity class is stored across multiple tables.
 * 
 * <p>
 * If no <code>SecondaryTable</code> annotation is specified, it is assumed that all persistent fields or properties of the entity are
 * mapped to the primary table. If no primary key join columns are specified, the join columns are assumed to reference the primary key
 * columns of the primary table, and have the same names and types as the referenced primary key columns of the primary table.
 * 
 * <pre>
 *    Example 1: Single secondary table with a single primary key column.
 * 
 *    &#064;Entity
 *    &#064;Table(name="CUSTOMER")
 *    &#064;SecondaryTable(name="CUST_DETAIL",
 *        pkJoinColumns=&#064;PrimaryKeyJoinColumn(name="CUST_ID"))
 *    public class Customer { ... }
 * 
 * 
 *    Example 2: Single secondary table with multiple primary key columns.
 * 
 *    &#064;Entity
 *    &#064;Table(name="CUSTOMER")
 *    &#064;SecondaryTable(name="CUST_DETAIL",
 *        pkJoinColumns={
 *            &#064;PrimaryKeyJoinColumn(name="CUST_ID"),
 *            &#064;PrimaryKeyJoinColumn(name="CUST_TYPE")})
 *    public class Customer { ... }
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface SecondaryTable {
	/**
	 * (Optional) The catalog of the table.
	 * <p>
	 * Defaults to the default catalog.
	 */
	String catalog() default "";

	/**
	 * (Required) The name of the table.
	 */
	String name();

	/**
	 * (Optional) The columns that are used to join with the primary table.
	 * <p>
	 * Defaults to the column(s) of the same name(s) as the primary key column(s) in the primary table.
	 */
	PrimaryKeyJoinColumn[] pkJoinColumns() default {};

	/**
	 * (Optional) The schema of the table.
	 * <p>
	 * Defaults to the default schema for user.
	 */
	String schema() default "";

	/**
	 * (Optional) Unique constraints that are to be placed on the table. These are typically only used if table generation is in effect.
	 * These constraints apply in addition to any constraints specified by the <code>Column</code> and <code>JoinColumn</code> annotations
	 * and constraints entailed by primary key mappings.
	 * <p>
	 * Defaults to no additional constraints.
	 */
	UniqueConstraint[] uniqueConstraints() default {};
}

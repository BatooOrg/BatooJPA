package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the mapping of the result of a native SQL query.
 * 
 * <pre>
 *    Example:
 * 
 *    Query q = em.createNativeQuery(
 *        "SELECT o.id AS order_id, " +
 *            "o.quantity AS order_quantity, " +
 *            "o.item AS order_item, " +
 *            "i.name AS item_name, " +
 *        "FROM Order o, Item i " +
 *        "WHERE (order_quantity > 25) AND (order_item = i.id)",
 *    "OrderResults");
 * 
 *    &#064;SqlResultSetMapping(name="OrderResults",
 *        entities={
 *            &#064;EntityResult(entityClass=com.acme.Order.class, fields={
 *                &#064;FieldResult(name="id", column="order_id"),
 *                &#064;FieldResult(name="quantity", column="order_quantity"),
 *                &#064;FieldResult(name="item", column="order_item")})},
 *        columns={
 *            &#064;ColumnResult(name="item_name")}
 *    )
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface SqlResultSetMapping {
	/**
	 * Specifies the result set mapping to scalar values.
	 */
	ColumnResult[] columns() default {};

	/**
	 * Specifies the result set mapping to entities.
	 */
	EntityResult[] entities() default {};

	/**
	 * The name given to the result set mapping, and used to refer to it in the methods of the {@link Query} API.
	 */
	String name();
}

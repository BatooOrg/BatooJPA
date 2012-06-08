package javax.persistence;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * References name of a column in the SELECT clause of a SQL query - i.e., column alias, if applicable. Scalar result types can be included
 * in the query result by specifying this annotation in the metadata.
 * 
 * <pre>
 * 
 * Example:
 *   Query q = em.createNativeQuery(
 *       "SELECT o.id AS order_id, " +
 *           "o.quantity AS order_quantity, " +
 *           "o.item AS order_item, " +
 *           "i.name AS item_name, " +
 *         "FROM Order o, Item i " +
 *         "WHERE (order_quantity > 25) AND (order_item = i.id)",
 *       "OrderResults");
 * 
 *   &#064;SqlResultSetMapping(name="OrderResults",
 *       entities={
 *           &#064;EntityResult(entityClass=com.acme.Order.class, fields={
 *               &#064;FieldResult(name="id", column="order_id"),
 *               &#064;FieldResult(name="quantity", column="order_quantity"),
 *               &#064;FieldResult(name="item", column="order_item")})},
 *       columns={
 *           &#064;ColumnResult(name="item_name")}
 *       )
 * </pre>
 * 
 * @see SqlResultSetMapping
 * 
 * @since Java Persistence 1.0
 */
@Target({})
@Retention(RUNTIME)
public @interface ColumnResult {

	/**
	 * (Required) The name of a column in the SELECT clause of a SQL query
	 */
	String name();
}

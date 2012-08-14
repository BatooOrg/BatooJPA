package javax.persistence.criteria;

import java.util.Map;

import javax.persistence.metamodel.MapAttribute;

/**
 * The MapJoin interface is the type of the result of joining to a collection over an association or element collection that has been
 * specified as a java.util.Map.
 * 
 * @param <Z>
 *            the source type of the join
 * @param <K>
 *            the type of the target Map key
 * @param <V>
 *            the type of the target Map value
 */
public interface MapJoin<Z, K, V> extends PluralJoin<Z, Map<K, V>, V> {

	/**
	 * Create an expression that corresponds to the map entry.
	 * 
	 * @return expression corresponding to the map entry
	 */
	Expression<Map.Entry<K, V>> entry();

	/**
	 * Return the metamodel representation for the map attribute.
	 * 
	 * @return metamodel type representing the Map that is the target of the join
	 */
	@Override
	MapAttribute<? super Z, K, V> getModel();

	/**
	 * Create a path expression that corresponds to the map key.
	 * 
	 * @return path corresponding to map key
	 */
	Path<K> key();

	/**
	 * Create a path expression that corresponds to the map value. This method is for stylistic use only: it just returns this.
	 * 
	 * @return path corresponding to the map value
	 */
	Path<V> value();
}

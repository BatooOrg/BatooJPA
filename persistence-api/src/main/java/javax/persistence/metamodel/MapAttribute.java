package javax.persistence.metamodel;

/**
 * Instances of the type MapAttribute represent persistent java.util.Map-valued attributes.
 * 
 * @param <X>
 *            The type the represented Map belongs to
 * @param <K>
 *            The type of the key of the represented Map
 * @param <V>
 *            The type of the value of the represented Map
 */
public interface MapAttribute<X, K, V> extends PluralAttribute<X, java.util.Map<K, V>, V> {
	/**
	 * Return the Java type of the map key.
	 * 
	 * @return Java key type
	 */
	Class<K> getKeyJavaType();

	/**
	 * Return the type representing the key type of the map.
	 * 
	 * @return type representing key type
	 */
	Type<K> getKeyType();
}

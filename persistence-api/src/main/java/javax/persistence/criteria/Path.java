package javax.persistence.criteria;

import java.util.Collection;
import java.util.Map;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * Represents a simple or compound attribute path from a bound type or collection, and is a "primitive" expression.
 * 
 * @param <X>
 *            the type referenced by the path
 */
public interface Path<X> extends Expression<X> {

	/**
	 * Create a path corresponding to the referenced map-valued attribute.
	 * 
	 * @param map
	 *            map-valued attribute
	 * @param <K>
	 *            the type of the key
	 * @param <V>
	 *            the type of the value
	 * @param <M>
	 *            the type of the map
	 * @return expression corresponding to the referenced attribute
	 */
	<K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map);

	/**
	 * Create a path corresponding to the referenced collection-valued attribute.
	 * 
	 * @param collection
	 *            collection-valued attribute
	 * @param <E>
	 *            the type of the element
	 * @param <C>
	 *            the type of the collection
	 * @return expression corresponding to the referenced attribute
	 */
	<E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection);

	/**
	 * Create a path corresponding to the referenced single-valued attribute.
	 * 
	 * @param attribute
	 *            single-valued attribute
	 * @param <Y>
	 *            the type of the path
	 * @return path corresponding to the referenced attribute
	 */
	<Y> Path<Y> get(SingularAttribute<? super X, Y> attribute);

	/**
	 * Create a path corresponding to the referenced attribute.
	 * 
	 * Note: Applications using the string-based API may need to specify the type resulting from the get operation in order to avoid the use
	 * of Path variables.
	 * 
	 * <pre>
	 *     For example:
	 *     
	 *     CriteriaQuery&lt;Person&gt; q = cb.createQuery(Person.class);
	 *     Root&lt;Person&gt; p = q.from(Person.class);
	 *     q.select(p)
	 * 	    .where(cb.isMember("joe",
	 * 	                       p.&lt;Set&lt;String&gt;&gt;get("nicknames")));
	 *     
	 *     rather than: 
	 * 
	 *     CriteriaQuery&lt;Person&gt; q = cb.createQuery(Person.class);
	 *     Root&lt;Person&gt; p = q.from(Person.class);
	 *     Path&lt;Set&lt;String&gt;&gt; nicknames = p.get("nicknames");
	 *     q.select(p)
	 * 	    .where(cb.isMember("joe", nicknames));
	 * </pre>
	 * 
	 * @param attributeName
	 *            name of the attribute
	 * @param <Y>
	 *            the type of the path
	 * @return path corresponding to the referenced attribute
	 * @throws IllegalStateException
	 *             if invoked on a path that corresponds to a basic type
	 * @throws IllegalArgumentException
	 *             if attribute of the given name does not otherwise exist
	 */
	<Y> Path<Y> get(String attributeName);

	/**
	 * Return the bindable object that corresponds to the path expression.
	 * 
	 * @return bindable object corresponding to the path
	 */
	Bindable<X> getModel();

	/**
	 * Return the parent "node" in the path or null if no parent.
	 * 
	 * @return parent
	 */
	Path<?> getParentPath();

	/**
	 * Create an expression corresponding to the type of the path.
	 * 
	 * @return expression corresponding to the type of the path
	 */
	Expression<Class<? extends X>> type();
}

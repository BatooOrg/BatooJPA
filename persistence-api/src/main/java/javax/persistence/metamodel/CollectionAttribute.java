package javax.persistence.metamodel;

/**
 * Instances of the type CollectionAttribute represent persistent javax.util.Collection-valued attributes.
 * 
 * @param <X>
 *            The type the represented Collection belongs to
 * @param <E>
 *            The element type of the represented Collection
 */
public interface CollectionAttribute<X, E> extends PluralAttribute<X, java.util.Collection<E>, E> {}

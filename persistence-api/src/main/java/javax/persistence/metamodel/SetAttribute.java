package javax.persistence.metamodel;

/**
 * Instances of the type SetAttribute represent persistent java.util.Set-valued attributes.
 * 
 * @param <X>
 *            The type the represented Set belongs to
 * @param <E>
 *            The element type of the represented Set
 */
public interface SetAttribute<X, E> extends PluralAttribute<X, java.util.Set<E>, E> {}

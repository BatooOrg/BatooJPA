package javax.persistence.metamodel;

/**
 * Instances of the type ListAttribute represent persistent java.util.List-valued attributes.
 * 
 * @param <X>
 *            The type the represented List belongs to
 * @param <E>
 *            The element type of the represented List
 */
public interface ListAttribute<X, E> extends PluralAttribute<X, java.util.List<E>, E> {}

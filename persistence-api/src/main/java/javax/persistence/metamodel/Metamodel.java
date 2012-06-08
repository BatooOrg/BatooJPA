package javax.persistence.metamodel;

import java.util.Set;

/**
 * Provides access to the metamodel of persistent entities in the persistence unit.
 */
public interface Metamodel {
	/**
	 * Return the metamodel embeddable type representing the embeddable class.
	 * 
	 * @param cls
	 *            the type of the represented embeddable class
	 * @param <X>
	 *            The type of the represented object
	 * @return the metamodel embeddable type
	 * @throws IllegalArgumentException
	 *             if not an embeddable class
	 */
	<X> EmbeddableType<X> embeddable(Class<X> cls);

	/**
	 * Return the metamodel entity type representing the entity.
	 * 
	 * @param cls
	 *            the type of the represented entity
	 * @param <X>
	 *            The type of the represented object
	 * @return the metamodel entity type
	 * @throws IllegalArgumentException
	 *             if not an entity
	 */
	<X> EntityType<X> entity(Class<X> cls);

	/**
	 * Return the metamodel embeddable types. Returns empty set if there are no embeddable types.
	 * 
	 * @return the metamodel embeddable types
	 */
	Set<EmbeddableType<?>> getEmbeddables();

	/**
	 * Return the metamodel entity types.
	 * 
	 * @return the metamodel entity types
	 */
	Set<EntityType<?>> getEntities();

	/**
	 * Return the metamodel managed types.
	 * 
	 * @return the metamodel managed types
	 */
	Set<ManagedType<?>> getManagedTypes();

	/**
	 * Return the metamodel managed type representing the entity, mapped superclass, or embeddable class.
	 * 
	 * @param cls
	 *            the type of the represented managed class
	 * @param <X>
	 *            The type of the represented object
	 * @return the metamodel managed type
	 * @throws IllegalArgumentException
	 *             if not a managed class
	 */
	<X> ManagedType<X> managedType(Class<X> cls);
}

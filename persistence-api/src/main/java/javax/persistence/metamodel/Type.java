package javax.persistence.metamodel;

/**
 * Instances of the type Type represent persistent object or attribute types.
 * 
 * @param <X>
 *            The type of the represented object or attribute
 */
public interface Type<X> {
	/**
	 * The persistence type
	 * 
	 */
	public static enum PersistenceType {
		/**
		 * Entity
		 */
		ENTITY,
		/**
		 * Embeddable
		 */
		EMBEDDABLE,
		/**
		 * Mapped super class
		 */
		MAPPED_SUPERCLASS,
		/**
		 * Basic
		 */
		BASIC
	}

	/**
	 * Return the persistence type.
	 * 
	 * @return persistence type
	 */
	PersistenceType getPersistenceType();
}

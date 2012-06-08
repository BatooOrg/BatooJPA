package javax.persistence.metamodel;

/**
 * Represents an attribute of a Java type.
 * 
 * @param <X>
 *            The represented type that contains the attribute
 * @param <Y>
 *            The type of the represented attribute
 */
public interface Attribute<X, Y> {

	/**
	 * Persistent attribute types.
	 * 
	 */
	public static enum PersistentAttributeType {
		/**
		 * Many-to-one association
		 */
		MANY_TO_ONE,

		/**
		 * One-to-one association
		 */
		ONE_TO_ONE,

		/**
		 * Basic attribute
		 */
		BASIC,

		/**
		 * Embeddable class attribute
		 */
		EMBEDDED,

		/**
		 * Many-to-many association
		 */
		MANY_TO_MANY,

		/**
		 * One-to-many association
		 */
		ONE_TO_MANY,

		/**
		 * Element collection
		 */
		ELEMENT_COLLECTION
	}

	/**
	 * Return the managed type representing the type in which the attribute was declared.
	 * 
	 * @return declaring type
	 */
	ManagedType<X> getDeclaringType();

	/**
	 * Return the java.lang.reflect.Member for the represented attribute.
	 * 
	 * @return corresponding java.lang.reflect.Member
	 */
	java.lang.reflect.Member getJavaMember();

	/**
	 * Return the Java type of the represented attribute.
	 * 
	 * @return Java type
	 */
	Class<Y> getJavaType();

	/**
	 * Return the name of the attribute.
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Return the persistent attribute type for the attribute.
	 * 
	 * @return persistent attribute type
	 */
	PersistentAttributeType getPersistentAttributeType();

	/**
	 * Is the attribute an association.
	 * 
	 * @return boolean indicating whether the attribute corresponds to an association
	 */
	boolean isAssociation();

	/**
	 * Is the attribute collection-valued (represents a Collection, Set, List, or Map).
	 * 
	 * @return boolean indicating whether the attribute is collection-valued
	 */
	boolean isCollection();
}

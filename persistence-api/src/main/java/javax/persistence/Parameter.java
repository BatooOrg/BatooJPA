package javax.persistence;

/**
 * Type for query parameter objects.
 * 
 * @param <T>
 *            the type of the parameter
 */
public interface Parameter<T> {
	/**
	 * Return the parameter name, or null if the parameter is not a named parameter or no name has been assigned.
	 * 
	 * @return parameter name
	 */
	String getName();

	/**
	 * Return the Java type of the parameter. Values bound to the parameter must be assignable to this type. This method is required to be
	 * supported for criteria queries only. Applications that use this method for Java Persistence query language queries and native queries
	 * will not be portable.
	 * 
	 * @return the Java type of the parameter
	 * @throws IllegalStateException
	 *             if invoked on a parameter obtained from a Java persistence query language query or native query when the implementation
	 *             does not support this use
	 */
	Class<T> getParameterType();

	/**
	 * Return the parameter position, or null if the parameter is not a positional parameter.
	 * 
	 * @return position of parameter
	 */
	Integer getPosition();
}

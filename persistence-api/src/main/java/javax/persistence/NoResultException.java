package javax.persistence;

/**
 * Thrown by the persistence provider when {@link Query#getSingleResult Query.getSingleResult()} or {@link TypedQuery#getSingleResult
 * TypedQuery.getSingleResult()}is executed on a query and there is no result to return. This exception will not cause the current
 * transaction, if one is active, to be marked for rollback.
 * 
 * @see Query#getSingleResult()
 * @see TypedQuery#getSingleResult()
 * 
 * @since Java Persistence 1.0
 */
public class NoResultException extends PersistenceException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new <code>NoResultException</code> exception with <code>null</code> as its detail message.
	 */
	public NoResultException() {
		super();
	}

	/**
	 * Constructs a new <code>NoResultException</code> exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public NoResultException(String message) {
		super(message);
	}
}

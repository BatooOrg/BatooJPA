package javax.persistence;

/**
 * Thrown by the persistence provider when an pessimistic locking conflict occurs that does not result in transaction rollback. This
 * exception may be thrown as part of an API call, at, flush or at commit time. The current transaction, if one is active, will be not be
 * marked for rollback.
 * 
 * @since Java Persistence 2.0
 */
public class LockTimeoutException extends PersistenceException {

	private static final long serialVersionUID = 1L;

	/** The object that caused the exception */
	Object entity;

	/**
	 * Constructs a new <code>LockTimeoutException</code> exception with <code>null</code> as its detail message.
	 */
	public LockTimeoutException() {
		super();
	}

	/**
	 * Constructs a new <code>LockTimeoutException</code> exception with the specified object.
	 * 
	 * @param entity
	 *            the entity.
	 */
	public LockTimeoutException(Object entity) {
		this.entity = entity;
	}

	/**
	 * Constructs a new <code>LockTimeoutException</code> exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public LockTimeoutException(String message) {
		super(message);
	}

	/**
	 * Constructs a new <code>LockTimeoutException</code> exception with the specified detail message and cause.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause.
	 */
	public LockTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new <code>LockTimeoutException</code> exception with the specified detail message, cause, and entity.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause.
	 * @param entity
	 *            the entity.
	 */
	public LockTimeoutException(String message, Throwable cause, Object entity) {
		super(message, cause);
		this.entity = entity;
	}

	/**
	 * Constructs a new <code>LockTimeoutException</code> exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public LockTimeoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * Returns the object that caused this exception.
	 * 
	 * @return the entity
	 */
	public Object getObject() {
		return this.entity;
	}
}

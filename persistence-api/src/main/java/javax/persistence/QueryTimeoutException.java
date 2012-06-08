package javax.persistence;

/**
 * Thrown by the persistence provider when a query times out and only the statement is rolled back. The current transaction, if one is
 * active, will be not be marked for rollback.
 * 
 * @since Java Persistence 2.0
 */
public class QueryTimeoutException extends PersistenceException {

	private static final long serialVersionUID = 1L;

	/**
	 * The query object that caused the exception
	 */
	Query query;

	/**
	 * Constructs a new <code>QueryTimeoutException</code> exception with <code>null</code> as its detail message.
	 */
	public QueryTimeoutException() {
		super();
	}

	/**
	 * Constructs a new <code>QueryTimeoutException</code> exception with the specified query.
	 * 
	 * @param query
	 *            the query.
	 */
	public QueryTimeoutException(Query query) {
		this.query = query;
	}

	/**
	 * Constructs a new <code>QueryTimeoutException</code> exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public QueryTimeoutException(String message) {
		super(message);
	}

	/**
	 * Constructs a new <code>QueryTimeoutException</code> exception with the specified detail message and cause.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause.
	 */
	public QueryTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new <code>QueryTimeoutException</code> exception with the specified detail message, cause, and query.
	 * 
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause.
	 * @param query
	 *            the query.
	 */
	public QueryTimeoutException(String message, Throwable cause, Query query) {
		super(message, cause);
		this.query = query;
	}

	/**
	 * Constructs a new <code>QueryTimeoutException</code> exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public QueryTimeoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * Returns the query that caused this exception.
	 * 
	 * @return the query.
	 */
	public Query getQuery() {
		return this.query;
	}
}

package javax.persistence;

/**
 * Interface used to control transactions on resource-local entity managers.
 */
public interface EntityTransaction {

	/**
	 * Start a resource transaction.
	 * 
	 * @throws IllegalStateException
	 *             if isActive() is true
	 */
	public void begin();

	/**
	 * Commit the current resource transaction, writing any unflushed changes to the database.
	 * 
	 * @throws IllegalStateException
	 *             if isActive() is false
	 * @throws RollbackException
	 *             if the commit fails
	 */
	public void commit();

	/**
	 * Determine whether the current resource transaction has been marked for rollback.
	 * 
	 * @return boolean indicating whether the transaction has been marked for rollback
	 * @throws IllegalStateException
	 *             if isActive() is false
	 */
	public boolean getRollbackOnly();

	/**
	 * Indicate whether a resource transaction is in progress.
	 * 
	 * @return boolean indicating whether transaction is in progress
	 * @throws PersistenceException
	 *             if an unexpected error condition is encountered
	 */
	public boolean isActive();

	/**
	 * Roll back the current resource transaction.
	 * 
	 * @throws IllegalStateException
	 *             if isActive() is false
	 * @throws PersistenceException
	 *             if an unexpected error condition is encountered
	 */
	public void rollback();

	/**
	 * Mark the current resource transaction so that the only possible outcome of the transaction is for the transaction to be rolled back.
	 * 
	 * @throws IllegalStateException
	 *             if isActive() is false
	 */
	public void setRollbackOnly();
}

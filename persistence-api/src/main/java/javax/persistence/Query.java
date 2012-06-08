package javax.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface used to control query execution.
 */
public interface Query {
	/**
	 * Execute an update or delete statement.
	 * 
	 * @return the number of entities updated or deleted
	 * @throws IllegalStateException
	 *             if called for a Java Persistence query language SELECT statement or for a criteria query
	 * @throws TransactionRequiredException
	 *             if there is no transaction or the persistence context has not been joined to the transaction
	 * @throws QueryTimeoutException
	 *             if the statement execution exceeds the query timeout value set and only the statement is rolled back
	 * @throws PersistenceException
	 *             if the query execution exceeds the query timeout value set and the transaction is rolled back
	 */
	int executeUpdate();

	/**
	 * The position of the first result the query object was set to retrieve. Returns 0 if setFirstResult was not applied to the query
	 * object.
	 * 
	 * @return position of the first result
	 */
	int getFirstResult();

	/**
	 * Get the flush mode in effect for the query execution. If a flush mode has not been set for the query object, returns the flush mode
	 * in effect for the entity manager.
	 * 
	 * @return flush mode
	 */
	FlushModeType getFlushMode();

	/**
	 * Get the properties and hints and associated values that are in effect for the query instance.
	 * 
	 * @return query properties and hints
	 */
	Map<String, Object> getHints();

	/**
	 * Get the current lock mode for the query. Returns null if a lock mode has not been set on the query object.
	 * 
	 * @return lock mode
	 * @throws IllegalStateException
	 *             if the query is found not to be a Java Persistence query language SELECT query or a Criteria API query
	 */
	LockModeType getLockMode();

	/**
	 * The maximum number of results the query object was set to retrieve. Returns Integer.MAX_VALUE if setMaxResults was not applied to the
	 * query object.
	 * 
	 * @return maximum number of results
	 */
	int getMaxResults();

	/**
	 * Get the parameter object corresponding to the declared positional parameter with the given position. This method is not required to
	 * be supported for native queries.
	 * 
	 * @param position
	 * @return parameter object
	 * @throws IllegalArgumentException
	 *             if the parameter with the specified position does not exist
	 * @throws IllegalStateException
	 *             if invoked on a native query when the implementation does not support this use
	 */
	Parameter<?> getParameter(int position);

	/**
	 * Get the parameter object corresponding to the declared positional parameter with the given position and type. This method is not
	 * required to be supported by the provider.
	 * 
	 * @param position
	 * @param type
	 * @param <T>
	 *            the type of the parameter
	 * @return parameter object
	 * @throws IllegalArgumentException
	 *             if the parameter with the specified position does not exist or is not assignable to the type
	 * @throws IllegalStateException
	 *             if invoked on a native query or Java Persistence query language query when the implementation does not support this use
	 */
	<T> Parameter<T> getParameter(int position, Class<T> type);

	/**
	 * Get the parameter object corresponding to the declared parameter of the given name. This method is not required to be supported for
	 * native queries.
	 * 
	 * @param name
	 * @return parameter object
	 * @throws IllegalArgumentException
	 *             if the parameter of the specified name does not exist
	 * @throws IllegalStateException
	 *             if invoked on a native query when the implementation does not support this use
	 */
	Parameter<?> getParameter(String name);

	/**
	 * Get the parameter object corresponding to the declared parameter of the given name and type. This method is required to be supported
	 * for criteria queries only.
	 * 
	 * @param name
	 *            parameter name
	 * @param type
	 * @return parameter object
	 * @param <T>
	 *            the type of the parameter
	 * @throws IllegalArgumentException
	 *             if the parameter of the specified name does not exist or is not assignable to the type
	 * @throws IllegalStateException
	 *             if invoked on a native query or Java Persistence query language query when the implementation does not support this use
	 */
	<T> Parameter<T> getParameter(String name, Class<T> type);

	/**
	 * Get the parameter objects corresponding to the declared parameters of the query. Returns empty set if the query has no parameters.
	 * This method is not required to be supported for native queries.
	 * 
	 * @return set of the parameter objects
	 * @throws IllegalStateException
	 *             if invoked on a native query when the implementation does not support this use
	 */
	Set<Parameter<?>> getParameters();

	/**
	 * Return the input value bound to the positional parameter. (Note that OUT parameters are unbound.)
	 * 
	 * @param position
	 * @return parameter value
	 * @throws IllegalStateException
	 *             if the parameter has not been been bound
	 * @throws IllegalArgumentException
	 *             if the parameter with the specified position does not exist
	 */
	Object getParameterValue(int position);

	/**
	 * Return the input value bound to the parameter. (Note that OUT parameters are unbound.)
	 * 
	 * @param param
	 *            parameter object
	 * @return parameter value
	 * @param <T>
	 *            the type of the parameter
	 * @throws IllegalArgumentException
	 *             if the parameter is not a parameter of the query
	 * @throws IllegalStateException
	 *             if the parameter has not been been bound
	 */
	<T> T getParameterValue(Parameter<T> param);

	/**
	 * Return the input value bound to the named parameter. (Note that OUT parameters are unbound.)
	 * 
	 * @param name
	 *            parameter name
	 * @return parameter value
	 * @throws IllegalStateException
	 *             if the parameter has not been been bound
	 * @throws IllegalArgumentException
	 *             if the parameter of the specified name does not exist
	 */
	Object getParameterValue(String name);

	/**
	 * Execute a SELECT query and return the query results as an untyped List.
	 * 
	 * @return a list of the results
	 * @throws IllegalStateException
	 *             if called for a Java Persistence query language UPDATE or DELETE statement
	 * @throws QueryTimeoutException
	 *             if the query execution exceeds the query timeout value set and only the statement is rolled back
	 * @throws TransactionRequiredException
	 *             if a lock mode other than NONE has been been set and there is no transaction or the persistence context has not been
	 *             joined to the transaction
	 * @throws PessimisticLockException
	 *             if pessimistic locking fails and the transaction is rolled back
	 * @throws LockTimeoutException
	 *             if pessimistic locking fails and only the statement is rolled back
	 * @throws PersistenceException
	 *             if the query execution exceeds the query timeout value set and the transaction is rolled back
	 */
	List<?> getResultList();

	/**
	 * Execute a SELECT query that returns a single untyped result.
	 * 
	 * @return the result
	 * @throws NoResultException
	 *             if there is no result
	 * @throws NonUniqueResultException
	 *             if more than one result
	 * @throws IllegalStateException
	 *             if called for a Java Persistence query language UPDATE or DELETE statement
	 * @throws QueryTimeoutException
	 *             if the query execution exceeds the query timeout value set and only the statement is rolled back
	 * @throws TransactionRequiredException
	 *             if a lock mode other than NONE has been been set and there is no transaction or the persistence context has not been
	 *             joined to the transaction
	 * @throws PessimisticLockException
	 *             if pessimistic locking fails and the transaction is rolled back
	 * @throws LockTimeoutException
	 *             if pessimistic locking fails and only the statement is rolled back
	 * @throws PersistenceException
	 *             if the query execution exceeds the query timeout value set and the transaction is rolled back
	 */
	Object getSingleResult();

	/**
	 * Return a boolean indicating whether a value has been bound to the parameter.
	 * 
	 * @param param
	 *            parameter object
	 * @return boolean indicating whether parameter has been bound
	 */
	boolean isBound(Parameter<?> param);

	/**
	 * Set the position of the first result to retrieve.
	 * 
	 * @param startPosition
	 *            position of the first result, numbered from 0
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the argument is negative
	 */
	Query setFirstResult(int startPosition);

	/**
	 * Set the flush mode type to be used for the query execution. The flush mode type applies to the query regardless of the flush mode
	 * type in use for the entity manager.
	 * 
	 * @param flushMode
	 * @return the same query instance
	 */
	Query setFlushMode(FlushModeType flushMode);

	/**
	 * Set a query property or hint. The hints elements may be used to specify query properties and hints. Properties defined by this
	 * specification must be observed by the provider. Vendor-specific hints that are not recognized by a provider must be silently ignored.
	 * Portable applications should not rely on the standard timeout hint. Depending on the database in use and the locking mechanisms used
	 * by the provider, this hint may or may not be observed.
	 * 
	 * @param hintName
	 *            name of the property or hint
	 * @param value
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the second argument is not valid for the implementation
	 */
	Query setHint(String hintName, Object value);

	/**
	 * Set the lock mode type to be used for the query execution.
	 * 
	 * @param lockMode
	 * @return the same query instance
	 * @throws IllegalStateException
	 *             if the query is found not to be a Java Persistence query language SELECT query or a Criteria API query
	 */
	Query setLockMode(LockModeType lockMode);

	/**
	 * Set the maximum number of results to retrieve.
	 * 
	 * @param maxResult
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the argument is negative
	 */
	Query setMaxResults(int maxResult);

	/**
	 * Bind an instance of java.util.Calendar to a positional parameter.
	 * 
	 * @param position
	 * @param value
	 *            parameter value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if position does not correspond to a positional parameter of the query or if the value argument is of incorrect type
	 */
	Query setParameter(int position, Calendar value, TemporalType temporalType);

	/**
	 * Bind an instance of java.util.Date to a positional parameter.
	 * 
	 * @param position
	 * @param value
	 *            parameter value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if position does not correspond to a positional parameter of the query or if the value argument is of incorrect type
	 */
	Query setParameter(int position, Date value, TemporalType temporalType);

	/**
	 * Bind an argument value to a positional parameter.
	 * 
	 * @param position
	 * @param value
	 *            parameter value
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if position does not correspond to a positional parameter of the query or if the argument is of incorrect type
	 */
	Query setParameter(int position, Object value);

	/**
	 * Bind an instance of java.util.Calendar to a Parameter object.
	 * 
	 * @param param
	 *            parameter object
	 * @param value
	 *            parameter value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the parameter does not correspond to a parameter of the query
	 */
	Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType);

	/**
	 * Bind an instance of java.util.Date to a Parameter object.
	 * 
	 * @param param
	 *            parameter object
	 * @param value
	 *            parameter value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the parameter does not correspond to a parameter of the query
	 */
	Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType);

	/**
	 * Bind the value of a Parameter object.
	 * 
	 * @param param
	 *            parameter object
	 * @param value
	 *            parameter value
	 * @param <T>
	 *            the type of the parameter
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the parameter does not correspond to a parameter of the query
	 */
	<T> Query setParameter(Parameter<T> param, T value);

	/**
	 * Bind an instance of java.util.Calendar to a named parameter.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            parameter value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the parameter name does not correspond to a parameter of the query or if the value argument is of incorrect type
	 */
	Query setParameter(String name, Calendar value, TemporalType temporalType);

	/**
	 * Bind an instance of java.util.Date to a named parameter.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            parameter value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the parameter name does not correspond to a parameter of the query or if the value argument is of incorrect type
	 */
	Query setParameter(String name, Date value, TemporalType temporalType);

	/**
	 * Bind an argument value to a named parameter.
	 * 
	 * @param name
	 *            parameter name
	 * @param value
	 *            parameter value
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if the parameter name does not correspond to a parameter of the query or if the argument is of incorrect type
	 */
	Query setParameter(String name, Object value);

	/**
	 * Return an object of the specified type to allow access to the provider-specific API. If the provider's query implementation does not
	 * support the specified class, the PersistenceException is thrown.
	 * 
	 * @param cls
	 *            the class of the object to be returned. This is normally either the underlying query implementation class or an interface
	 *            that it implements.
	 * @param <T>
	 *            the type of the class
	 * @return an instance of the specified class
	 * @throws PersistenceException
	 *             if the provider does not support the call
	 */
	<T> T unwrap(Class<T> cls);
}

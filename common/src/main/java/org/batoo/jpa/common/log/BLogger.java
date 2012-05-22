/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.common.log;

/**
 * @author hceylan
 * @since $version
 */
public interface BLogger {

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	void debug(String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void debug(String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */

	void debug(Throwable t, String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void debug(Throwable t, String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	void error(String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void error(String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */

	void error(Throwable t, String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void error(Throwable t, String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	void fatal(String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void fatal(String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */

	void fatal(Throwable t, String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void fatal(Throwable t, String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	void info(String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void info(String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */

	void info(Throwable t, String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void info(Throwable t, String message, Object... params);

	/**
	 * Returns if the logger instance enabled for the DEBUG level.
	 * 
	 * @return if the logger instance enabled for the DEBUG level
	 */

	boolean isDebugEnabled();

	/**
	 * Returns if the logger instance enabled for the ERROR level.
	 * 
	 * @return if the logger instance enabled for the ERROR level
	 */

	boolean isErrorEnabled();

	/**
	 * Returns if the logger instance enabled for the INFO level.
	 * 
	 * @return if the logger instance enabled for the INFO level
	 */

	boolean isInfoEnabled();

	/**
	 * Returns if the logger instance enabled for the TRACE level.
	 * 
	 * @return if the logger instance enabled for the TRACE level
	 */

	boolean isTraceEnabled();

	/**
	 * Returns if the logger instance enabled for the WARN level.
	 * 
	 * @return if the logger instance enabled for the WARN level
	 */

	boolean isWarnEnabled();

	/**
	 * Returns an object that lazily boxes the block.
	 * 
	 * @param block
	 *            the block to box
	 * @return the object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Object lazyBoxed(final String block);

	/**
	 * Returns an object that lazily boxes the block.
	 * 
	 * @param block
	 *            the block to box
	 * @param parameters
	 *            of parameters
	 * @return the object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Object lazyBoxed(final String block, final Object[] parameters);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	void trace(String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void trace(String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */

	void trace(Throwable t, String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void trace(Throwable t, String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	void warn(String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void warn(String message, Object... params);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */

	void warn(Throwable t, String message);

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	void warn(Throwable t, String message, Object... params);

}

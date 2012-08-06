/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.common.log;

/**
 * @author hceylan
 * @since $version
 */
public interface BLogger {

	/**
	 * Returns an object that boxes the block.
	 * 
	 * @param block
	 *            the block to box
	 * @return the object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Object boxed(String block);

	/**
	 * Returns an object that boxes the block.
	 * 
	 * @param block
	 *            the block to box
	 * @param parameters
	 *            the parameters
	 * @return the object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Object boxed(String block, Object[] parameters);

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
	Object lazyBoxed(final Object block);

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
	Object lazyBoxed(final Object block, final Object[] parameters);

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

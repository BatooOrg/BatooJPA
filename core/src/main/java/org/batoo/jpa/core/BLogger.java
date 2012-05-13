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
package org.batoo.jpa.core;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Standard logging facility for the Batoo
 * <p>
 * You should get an instance by calling
 * 
 * <pre>
 * public class MyClass {
 * 
 *     private static final BLogger LOG = BLogger.getLogger(MyClass.class);
 *     
 *     {...}
 * }
 * </pre>
 * 
 * <p>
 * You can alternatively give your log an arbitrary name
 * 
 * <pre>
 * public class MyClass {
 * 
 *     private static final BLogger LOG = BLogger.getLogger("MyCoolLogger");
 * 
 *     {...}
 * }
 * </pre>
 * 
 * <p>
 * If you would like to log to general application log use
 * 
 * <pre>
 * public class MyClass {
 * 
 *     private static final BLogger LOG = BLogger.getLogger();
 * 
 *     {...}
 * }
 * </pre>
 * 
 * @author hceylan
 * 
 */
public class BLogger {

	private static final String FATAL_PREFIX = "FATAL----> ";

	private static final Object[] NULL_ARRAY = new Object[] {};

	private static String boxed(String block, Object[] parameters) {
		try {
			if ((parameters != null) && (parameters.length > 0)) {
				block += "\n\n" + Arrays.toString(parameters);
			}

			block = block.replaceAll("\\t", "    ");
			final List<String> lines = IOUtils.readLines(new StringReader(block));
			int max = 0;
			for (final String line : lines) {
				max = Math.max(max, line.length());
			}

			max += 4;
			final StringBuffer boxed = new StringBuffer("\n");
			boxed.append(StringUtils.repeat("-", max));
			boxed.append("\n");

			for (final String line : lines) {
				boxed.append("| ");
				boxed.append(StringUtils.rightPad(line, max - 4));
				boxed.append(" |\n");
			}

			boxed.append(StringUtils.repeat("-", max));

			return boxed.toString();
		}
		catch (final Throwable e) {
			return block;
		}
	}

	/**
	 * Returns an instance of Logger for the class
	 * 
	 * @param clazz
	 *            the clazz of the log
	 * @return the logger - {@link BLogger}
	 */
	public static final BLogger getLogger(Class<?> clazz) {
		return new BLogger(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Returns an instance of Logger with the class
	 * 
	 * @param name
	 *            the name of the log
	 * @return the logger - {@link BLogger}
	 */
	public static final BLogger getLogger(String name) {
		return new BLogger(LoggerFactory.getLogger(name));
	}

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
	public static Object lazyBoxed(final String block) {
		return BLogger.lazyBoxed(block, null);
	}

	/**
	 * Returns an object that lazily boxes the block.
	 * 
	 * @param block
	 *            the block to box
	 * @param array
	 *            of parameters
	 * @return the object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Object lazyBoxed(final String block, final Object[] parameters) {
		return new Callable<String>() {

			@Override
			public String call() throws Exception {
				return BLogger.boxed(block, parameters);
			}

			@Override
			public String toString() {
				try {
					return this.call();
				}
				catch (final Throwable e) {
					return block;
				}
			}
		};
	}

	private final Marker fatalMarker;

	private final Logger logger;

	private BLogger(Logger logger) {
		this.logger = logger;

		this.fatalMarker = MarkerFactory.getMarker("FATAL");
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void debug(String message) {
		this.debug(null, message, BLogger.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */
	public void debug(String message, Object... params) {
		this.debug(null, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */
	public void debug(Throwable t, String message) {
		this.debug(t, message, BLogger.NULL_ARRAY);
	}

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
	public void debug(Throwable t, String message, Object... params) {
		if (this.logger.isDebugEnabled()) {
			if (t != null) {
				this.logger.debug(this.format(message, params), t);
			}
			else {
				this.logger.debug(this.format(message, params));
			}
		}
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void error(String message) {
		this.error(null, message, BLogger.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */
	public void error(String message, Object... params) {
		this.error(null, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */
	public void error(Throwable t, String message) {
		this.error(t, message, BLogger.NULL_ARRAY);
	}

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
	public void error(Throwable t, String message, Object... params) {
		if (this.logger.isErrorEnabled()) {
			if (t != null) {
				this.logger.error(this.format(message, params), t);
			}
			else {
				this.logger.error(this.format(message, params));
			}
		}
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void fatal(String message) {
		this.trace(null, message, BLogger.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */
	public void fatal(String message, Object... params) {
		this.fatal(null, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */
	public void fatal(Throwable t, String message) {
		this.fatal(t, message, BLogger.NULL_ARRAY);
	}

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
	public void fatal(Throwable t, String message, Object... params) {
		if (t != null) {
			this.logger.error(this.fatalMarker, BLogger.FATAL_PREFIX + this.format(message, params), t);
		}
		else {
			this.logger.error(this.fatalMarker, BLogger.FATAL_PREFIX + this.format(message, params));
		}
	}

	private String format(String message, Object... params) {
		if ((params == null) || (params.length == 0)) {
			return message;
		}

		return MessageFormat.format(message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void info(String message) {
		this.info(null, message, BLogger.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */
	public void info(String message, Object... params) {
		this.info(null, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */
	public void info(Throwable t, String message) {
		this.info(t, message, BLogger.NULL_ARRAY);
	}

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
	public void info(Throwable t, String message, Object... params) {
		if (this.logger.isInfoEnabled()) {
			if (t != null) {
				this.logger.info(this.format(message, params), t);
			}
			else {
				this.logger.info(this.format(message, params));
			}
		}
	}

	/**
	 * Returns if the logger instance enabled for the DEBUG level.
	 * 
	 * @return true if this Logger is enabled for the DEBUG level,
	 *         false otherwise.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the ERROR level.
	 * 
	 * @return true if this Logger is enabled for the ERROR level,
	 *         false otherwise.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isErrorEnabled() {
		return this.logger.isErrorEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the INFO level.
	 * 
	 * @return true if this Logger is enabled for the INFO level,
	 *         false otherwise.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the TRACE level.
	 * 
	 * @return true if this Logger is enabled for the TRACE level,
	 *         false otherwise.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isTraceEnabled() {
		return this.logger.isTraceEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the WARN level.
	 * 
	 * @return true if this Logger is enabled for the WARN level,
	 *         false otherwise.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isWarnEnabled() {
		return this.logger.isWarnEnabled();
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void trace(String message) {
		this.trace(null, message, BLogger.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */
	public void trace(String message, Object... params) {
		this.trace(null, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */
	public void trace(Throwable t, String message) {
		this.trace(t, message, BLogger.NULL_ARRAY);
	}

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
	public void trace(Throwable t, String message, Object... params) {
		if (this.logger.isTraceEnabled()) {
			if (t != null) {
				this.logger.trace(this.format(message, params), t);
			}
			else {
				this.logger.trace(this.format(message, params));
			}
		}
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */
	public void warn(String message) {
		this.warn(null, message, BLogger.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */
	public void warn(String message, Object... params) {
		this.warn(null, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param t
	 *            the {@link Throwable} applicable to the log
	 * @param message
	 *            the message
	 */
	public void warn(Throwable t, String message) {
		this.warn(t, message, BLogger.NULL_ARRAY);
	}

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
	public void warn(Throwable t, String message, Object... params) {
		if (this.logger.isWarnEnabled()) {
			if (t != null) {
				this.logger.warn(this.format(message, params), t);
			}
			else {
				this.logger.warn(this.format(message, params));
			}
		}
	}
}

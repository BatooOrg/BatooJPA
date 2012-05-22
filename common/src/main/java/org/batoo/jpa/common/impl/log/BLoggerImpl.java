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
package org.batoo.jpa.common.impl.log;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.ToStringBuilder;
import org.batoo.jpa.common.log.ToStringBuilder.DetailLevel;
import org.slf4j.Logger;
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
 *     private static final BLogger LOG = BLoggerFactory.getLogger(MyClass.class);
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
 *     private static final BLogger LOG = BLoggerFactory.getLogger("MyCoolLogger");
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
 *     private static final BLogger LOG = BLoggerFactory.getLogger();
 * 
 *     {...}
 * }
 * </pre>
 * 
 * @author hceylan
 * 
 */
public class BLoggerImpl implements BLogger {

	private static final String FATAL_PREFIX = "FATAL----> ";

	private static final Object[] NULL_ARRAY = new Object[] {};

	private final Marker fatalMarker;

	private final Logger logger;

	/**
	 * @param logger
	 *            the wrapped logger
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BLoggerImpl(Logger logger) {
		this.logger = logger;

		this.fatalMarker = MarkerFactory.getMarker("FATAL");
	}

	private String boxed(String block, Object[] parameters) {
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
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	@Override
	public void debug(String message) {
		this.debug(null, message, BLoggerImpl.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	@Override
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

	@Override
	public void debug(Throwable t, String message) {
		this.debug(t, message, BLoggerImpl.NULL_ARRAY);
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

	@Override
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

	@Override
	public void error(String message) {
		this.error(null, message, BLoggerImpl.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	@Override
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

	@Override
	public void error(Throwable t, String message) {
		this.error(t, message, BLoggerImpl.NULL_ARRAY);
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

	@Override
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

	@Override
	public void fatal(String message) {
		this.trace(null, message, BLoggerImpl.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	@Override
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

	@Override
	public void fatal(Throwable t, String message) {
		this.fatal(t, message, BLoggerImpl.NULL_ARRAY);
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

	@Override
	public void fatal(Throwable t, String message, Object... params) {
		if (t != null) {
			this.logger.error(this.fatalMarker, BLoggerImpl.FATAL_PREFIX + this.format(message, params), t);
		}
		else {
			this.logger.error(this.fatalMarker, BLoggerImpl.FATAL_PREFIX + this.format(message, params));
		}
	}

	private String format(DetailLevel level, String message, Object... params) {
		if ((params == null) || (params.length == 0)) {
			return message;
		}

		ToStringBuilder.setDetailLevel(level);
		try {
			return MessageFormat.format(message, params);
		}
		finally {
			ToStringBuilder.setDetailLevel(null);
		}
	}

	private String format(String message, Object... params) {
		return this.format(DetailLevel.LONG, message, params);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	@Override
	public void info(String message) {
		this.info(null, message, BLoggerImpl.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	@Override
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

	@Override
	public void info(Throwable t, String message) {
		this.info(t, message, BLoggerImpl.NULL_ARRAY);
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

	@Override
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
	 * @return if the logger instance enabled for the DEBUG level
	 */

	@Override
	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the ERROR level.
	 * 
	 * @return if the logger instance enabled for the ERROR level
	 */

	@Override
	public boolean isErrorEnabled() {
		return this.logger.isErrorEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the INFO level.
	 * 
	 * @return if the logger instance enabled for the INFO level
	 */

	@Override
	public boolean isInfoEnabled() {
		return this.logger.isInfoEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the TRACE level.
	 * 
	 * @return if the logger instance enabled for the TRACE level
	 */

	@Override
	public boolean isTraceEnabled() {
		return this.logger.isTraceEnabled();
	}

	/**
	 * Returns if the logger instance enabled for the WARN level.
	 * 
	 * @return if the logger instance enabled for the WARN level
	 */

	@Override
	public boolean isWarnEnabled() {
		return this.logger.isWarnEnabled();
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
	@Override
	public Object lazyBoxed(final String block) {
		return this.lazyBoxed(block, null);
	}

	/**
	 * Returns an object that lazily boxes the block.
	 * 
	 * @param block
	 *            the block to box
	 * @param parameters
	 *            the array of parameters
	 * @return the object
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public Object lazyBoxed(final String block, final Object[] parameters) {
		return new Callable<String>() {

			@Override
			public String call() throws Exception {
				return BLoggerImpl.this.boxed(block, parameters);
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

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the message
	 */

	@Override
	public void trace(String message) {
		this.trace(null, message, BLoggerImpl.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	@Override
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

	@Override
	public void trace(Throwable t, String message) {
		this.trace(t, message, BLoggerImpl.NULL_ARRAY);
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

	@Override
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

	@Override
	public void warn(String message) {
		this.warn(null, message, BLoggerImpl.NULL_ARRAY);
	}

	/**
	 * Convenience method to log a message
	 * 
	 * @param message
	 *            the format message
	 * @param params
	 *            the params to the message
	 */

	@Override
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

	@Override
	public void warn(Throwable t, String message) {
		this.warn(t, message, BLoggerImpl.NULL_ARRAY);
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

	@Override
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

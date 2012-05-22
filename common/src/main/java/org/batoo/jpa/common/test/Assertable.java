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
package org.batoo.jpa.common.test;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;

/**
 * Assert instrumentation.
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("javadoc")
public abstract class Assertable {

	/**
	 * Default constructor.
	 */
	protected Assertable() {
		super();
	}

	/**
	 * Asserts that two booleans are equal.
	 */
	public void assertEquals(boolean expected, boolean actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two bytes are equal.
	 */
	public void assertEquals(byte expected, byte actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two chars are equal.
	 */
	public void assertEquals(char expected, char actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two doubles are equal concerning a delta. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	public void assertEquals(double expected, double actual, double delta) {
		Assert.assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that two floats are equal concerning a delta. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	public void assertEquals(float expected, float actual, float delta) {
		Assert.assertEquals(null, expected, actual, delta);
	}

	/**
	 * Asserts that two ints are equal.
	 */
	public void assertEquals(int expected, int actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two longs are equal.
	 */
	public void assertEquals(long expected, long actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two objects are equal. If they are not
	 * an AssertionFailedError is thrown.
	 */
	public void assertEquals(Object expected, Object actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two shorts are equal.
	 */
	public void assertEquals(short expected, short actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two booleans are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, boolean expected, boolean actual) {
		Assert.assertEquals(message, Boolean.valueOf(expected), Boolean.valueOf(actual));
	}

	/**
	 * Asserts that two bytes are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, byte expected, byte actual) {
		Assert.assertEquals(message, new Byte(expected), new Byte(actual));
	}

	/**
	 * Asserts that two chars are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, char expected, char actual) {
		Assert.assertEquals(message, new Character(expected), new Character(actual));
	}

	/**
	 * Asserts that two doubles are equal concerning a delta. If they are not
	 * an AssertionFailedError is thrown with the given message. If the expected
	 * value is infinity then the delta value is ignored.
	 */
	public void assertEquals(String message, double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0) {
			return;
		}
		if (!(Math.abs(expected - actual) <= delta)) {
			Assert.failNotEquals(message, new Double(expected), new Double(actual));
		}
	}

	/**
	 * Asserts that two floats are equal concerning a positive delta. If they
	 * are not an AssertionFailedError is thrown with the given message. If the
	 * expected value is infinity then the delta value is ignored.
	 */
	public void assertEquals(String message, float expected, float actual, float delta) {
		if (Float.compare(expected, actual) == 0) {
			return;
		}
		if (!(Math.abs(expected - actual) <= delta)) {
			Assert.failNotEquals(message, new Float(expected), new Float(actual));
		}
	}

	/**
	 * Asserts that two ints are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, int expected, int actual) {
		Assert.assertEquals(message, new Integer(expected), new Integer(actual));
	}

	/**
	 * Asserts that two longs are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, long expected, long actual) {
		Assert.assertEquals(message, new Long(expected), new Long(actual));
	}

	/**
	 * Asserts that two objects are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, Object expected, Object actual) {
		if ((expected == null) && (actual == null)) {
			return;
		}
		if ((expected != null) && expected.equals(actual)) {
			return;
		}
		Assert.failNotEquals(message, expected, actual);
	}

	/**
	 * Asserts that two shorts are equal. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertEquals(String message, short expected, short actual) {
		Assert.assertEquals(message, new Short(expected), new Short(actual));
	}

	/**
	 * Asserts that two Strings are equal.
	 */
	public void assertEquals(String expected, String actual) {
		Assert.assertEquals(null, expected, actual);
	}

	/**
	 * Asserts that two Strings are equal.
	 */
	public void assertEquals(String message, String expected, String actual) {
		if ((expected == null) && (actual == null)) {
			return;
		}
		if ((expected != null) && expected.equals(actual)) {
			return;
		}
		final String cleanMessage = message == null ? "" : message;
		throw new ComparisonFailure(cleanMessage, expected, actual);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws
	 * an AssertionFailedError.
	 */
	public void assertFalse(boolean condition) {
		Assert.assertFalse(null, condition);
	}

	/**
	 * Asserts that a condition is false. If it isn't it throws
	 * an AssertionFailedError with the given message.
	 */
	public void assertFalse(String message, boolean condition) {
		Assert.assertTrue(message, !condition);
	}

	/**
	 * Asserts that an object isn't null.
	 */
	public void assertNotNull(Object object) {
		Assert.assertNotNull(null, object);
	}

	/**
	 * Asserts that an object isn't null. If it is
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertNotNull(String message, Object object) {
		Assert.assertTrue(message, object != null);
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object an AssertionFailedError is thrown.
	 */
	public void assertNotSame(Object expected, Object actual) {
		Assert.assertNotSame(null, expected, actual);
	}

	/**
	 * Asserts that two objects do not refer to the same object. If they do
	 * refer to the same object an AssertionFailedError is thrown with the
	 * given message.
	 */
	public void assertNotSame(String message, Object expected, Object actual) {
		if (expected == actual) {
			Assert.failSame(message);
		}
	}

	/**
	 * Asserts that an object is null. If it isn't an {@link AssertionError} is
	 * thrown.
	 * Message contains: Expected: <null> but was: object
	 * 
	 * @param object
	 *            Object to check or <code>null</code>
	 */
	public void assertNull(Object object) {
		final String message = "Expected: <null> but was: " + String.valueOf(object);
		Assert.assertNull(message, object);
	}

	/**
	 * Asserts that an object is null. If it is not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertNull(String message, Object object) {
		Assert.assertTrue(message, object == null);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not
	 * the same an AssertionFailedError is thrown.
	 */
	public void assertSame(Object expected, Object actual) {
		Assert.assertSame(null, expected, actual);
	}

	/**
	 * Asserts that two objects refer to the same object. If they are not
	 * an AssertionFailedError is thrown with the given message.
	 */
	public void assertSame(String message, Object expected, Object actual) {
		if (expected == actual) {
			return;
		}
		Assert.failNotSame(message, expected, actual);
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws
	 * an AssertionFailedError.
	 */
	public void assertTrue(boolean condition) {
		Assert.assertTrue(null, condition);
	}

	/**
	 * Asserts that a condition is true. If it isn't it throws
	 * an AssertionFailedError with the given message.
	 */
	public void assertTrue(String message, boolean condition) {
		if (!condition) {
			Assert.fail(message);
		}
	}

	/**
	 * Fails a test with no message.
	 */
	public void fail() {
		Assert.fail(null);
	}

	/**
	 * Fails a test with the given message.
	 */
	public void fail(String message) {
		if (message == null) {
			throw new AssertionFailedError();
		}
		throw new AssertionFailedError(message);
	}

	public void failNotEquals(String message, Object expected, Object actual) {
		Assert.fail(Assert.format(message, expected, actual));
	}

	public void failNotSame(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null) {
			formatted = message + " ";
		}
		Assert.fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
	}

	public void failSame(String message) {
		String formatted = "";
		if (message != null) {
			formatted = message + " ";
		}
		Assert.fail(formatted + "expected not same");
	}

	public String format(String message, Object expected, Object actual) {
		String formatted = "";
		if ((message != null) && (message.length() > 0)) {
			formatted = message + " ";
		}
		return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
	}
}

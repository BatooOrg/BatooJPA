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
package org.batoo.jpa.common;

/**
 * Base exceptions thrown by Batoo JPA.
 * 
 * @author hceylan
 * @since $version
 */
public class BatooException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BatooException() {
		super();
	}

	/**
	 * @param message
	 *            the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BatooException(String message) {
		super(message);
	}

	/**
	 * @param message
	 *            the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BatooException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 *            the detail message.
	 * @param cause
	 *            the cause. (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression
	 *            whether or not suppression is enabled or disabled
	 * @param writableStackTrace
	 *            whether or not the stack trace should be writable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BatooException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param cause
	 *            the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt> value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BatooException(Throwable cause) {
		super(cause);
	}

}

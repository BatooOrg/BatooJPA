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

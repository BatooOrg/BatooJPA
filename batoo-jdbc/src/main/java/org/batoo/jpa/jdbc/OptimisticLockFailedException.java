/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.jdbc;

import javax.persistence.PersistenceException;

import org.batoo.common.BatooVersion;

/**
 * The exception thrown when the optimistic lock fails.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public class OptimisticLockFailedException extends PersistenceException {
	private static final long serialVersionUID = BatooVersion.SERIAL_VERSION_UID;

	/**
	 * 
	 * @since 2.0.1
	 */
	public OptimisticLockFailedException() {
		super("Row was updated or deleted by a different transaction");
	}
}

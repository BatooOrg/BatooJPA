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
package org.batoo.jpa.parser.metadata;

import java.util.Map;

import javax.persistence.LockModeType;

/**
 * The definition for named queries.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface NamedQueryMetadata extends BindableMetadata {

	/**
	 * Returns the list query hints.
	 * 
	 * @return the list query hints
	 * 
	 * @since 2.0.0
	 */
	Map<String, Object> getHints();

	/**
	 * Returns the lock mode.
	 * 
	 * @return the lock mode
	 * 
	 * @since 2.0.0
	 */
	LockModeType getLockMode();

	/**
	 * Returns the query.
	 * 
	 * @return the query
	 * 
	 * @since 2.0.0
	 */
	String getQuery();
}

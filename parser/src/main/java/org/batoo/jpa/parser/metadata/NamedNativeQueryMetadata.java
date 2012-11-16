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

/**
 * The definition for named native queries.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public interface NamedNativeQueryMetadata extends BindableMetadata {

	/**
	 * Returns the list query hints.
	 * 
	 * @return the list query hints
	 * 
	 * @since 2.0.0
	 */
	Map<String, Object> getHints();

	/**
	 * Returns the query.
	 * 
	 * @return the query
	 * 
	 * @since 2.0.0
	 */
	String getQuery();

	/**
	 * Returns the result class.
	 * 
	 * @return the result class.
	 * 
	 * @since 2.0.0
	 */
	String getResultClass();

	/**
	 * Returns the resultset mapping.
	 * 
	 * @return the resultset mapping
	 * 
	 * @since 2.0.0
	 */
	String getResultSetMapping();
}

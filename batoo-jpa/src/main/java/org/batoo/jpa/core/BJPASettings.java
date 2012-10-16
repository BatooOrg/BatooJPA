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
package org.batoo.jpa.core;

/**
 * @author hceylan
 * 
 * @since $version
 */
public interface BJPASettings {

	/**
	 * DDL operations, DROP | CREATE (*) | UPDATE | VERIFY | NONE
	 */
	final String DDL = "org.batoo.jpa.ddl";

	/**
	 * Boolean value, indicating that the all tables & sequences should be dropped on close, useful for stateless applications and testing.
	 */
	final String DROP_ON_CLOSE = "org.batoo.jpa.dropOnClose";

	/**
	 * The default for {@link #MAX_CONNECTIONS} that is 50.
	 */
	final Integer DEFAULT_MAX_CONNECTIONS = 50;

	/**
	 * The default for {@link #MIN_CONNECTIONS} that is 10.
	 */
	final Integer DEFAULT_MIN_CONNECTIONS = 1;

	/**
	 * The default for {@link #STATEMENT_CACHE_SIZE} that is 50.
	 */
	final Integer DEFAULT_STATEMENT_CACHE_SIZE = 50;

	/**
	 * The size of the datasource statement cache size
	 */
	final String STATEMENT_CACHE_SIZE = "org.batoo.jdbc.statement_cache_size";

	/**
	 * The max size of the connection pool.
	 */
	final String MAX_CONNECTIONS = "org.batoo.jdbc.max_connections";

	/**
	 * The min size of the connection pool.
	 */
	final String MIN_CONNECTIONS = "org.batoo.jdbc.min_connections";
}

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
package org.batoo.jpa;

/**
 * @author hceylan
 * 
 * @since 2.0.0
 */
public interface BJPASettings {

	/**
	 * DDL operations, DROP | CREATE (*) | UPDATE | VERIFY | NONE
	 */
	String DDL = "org.batoo.jpa.ddl";

	/**
	 * Boolean value, indicating that the all tables & sequences should be dropped on close, useful for stateless applications and testing.
	 */
	String DROP_ON_CLOSE = "org.batoo.jpa.dropOnClose";

	/**
	 * Boolean value, indicating how the sql statements should be printed, NONE | STDOUT | STDERR.
	 * <p>
	 * If you have a logging infrastructor, prefer org.batoo.jpa.SQL for logging.
	 */
	String SQL_LOGGING = "org.batoo.jpa.sql_logging";

	/**
	 * Long value indicating that the number of milliseconds to deem an SQL execution as slow.
	 * <p>
	 * If an execution is deemed as slow it will be logged as warning.
	 */
	String SLOW_SQL_THRESHOLD = "org.batoo.jpa.slow_sql_threshold";

	/**
	 * Default value for {@link #SLOW_SQL_THRESHOLD} that is 2500.
	 */
	Long DEFAULT_SLOW_SQL_THRESHOLD = 2500l;

	/**
	 * The default for {@link #MAX_CONNECTIONS} that is 50.
	 */
	Integer DEFAULT_MAX_CONNECTIONS = 50;

	/**
	 * The default for {@link #MIN_CONNECTIONS} that is 10.
	 */
	Integer DEFAULT_MIN_CONNECTIONS = 1;

	/**
	 * The default for {@link #FETCH_SIZE}
	 */
	Integer DEFAULT_FETCH_SIZE = 100;

	/**
	 * The default for {@link #INSERT_BATCH_SIZE}
	 */
	Integer DEFAULT_INSERT_BATCH_SIZE = 10;

	/**
	 * The default for {@link #REMOVE_BATCH_SIZE}
	 */
	Integer DEFAULT_REMOVE_BATCH_SIZE = 10;

	/**
	 * The default for {@link #STATEMENT_CACHE_SIZE} that is 50.
	 */
	Integer DEFAULT_STATEMENT_CACHE_SIZE = 50;

	/**
	 * The default for {@value #MAX_FETCH_JOIN_DEPTH} that is 1.
	 */
	Integer DEFAULT_MAX_FETCH_JOIN_DEPTH = 2;

	/**
	 * The size of the datasource statement cache size
	 */
	String STATEMENT_CACHE_SIZE = "org.batoo.jdbc.statement_cache_size";

	/**
	 * The max number of fetch joins allowed.
	 */
	String MAX_FETCH_JOIN_DEPTH = "org.batoo.jdbc.max_fetch_join_depth";

	/**
	 * The size of the datasource statement cache size
	 */
	String FETCH_SIZE = "org.batoo.jdbc.fetch_size";

	/**
	 * The size of the insert statements batch
	 */
	String INSERT_BATCH_SIZE = "org.batoo.jdbc.insert_batch_size";

	/**
	 * The size of the remove statements batch
	 */
	String REMOVE_BATCH_SIZE = "org.batoo.jdbc.remove_batch_size";

	/**
	 * The max size of the connection pool.
	 */
	String MAX_CONNECTIONS = "org.batoo.jdbc.max_connections";

	/**
	 * The min size of the connection pool.
	 */
	String MIN_CONNECTIONS = "org.batoo.jdbc.min_connections";

	/**
	 * The name of the sql to execute to import the initial data.
	 */
	String IMPORT_SQL = "org.batoo.jdbc.import_sql";

	/**
	 * Pluggable DataSource implementation
	 */
	String DATASOURCE_POOL = "org.batoo.jdbc.datasource.pool";

	/**
	 * Hint for the the pluggable data source
	 */
	String DATASOURCE_NAME = "org.batoo.jdbc.datasource.name";
}

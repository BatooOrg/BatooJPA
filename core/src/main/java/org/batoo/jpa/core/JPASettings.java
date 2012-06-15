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

/**
 * @author hceylan
 * 
 * @since $version
 */
public interface JPASettings extends BJPASettings {

	/**
	 * The name of a JDBC driver to use to connect to the database.
	 * <p/>
	 * Used in conjunction with {@link #JDBC_URL}, {@link #JDBC_USER} and {@link #JDBC_PASSWORD} to define how to make connections to the
	 * database in lieu of a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
	 * <p/>
	 * See section 8.2.1.9
	 */
	public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";

	/**
	 * The JDBC connection password.
	 * <p/>
	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_URL} and {@link #JDBC_USER} to define how to make connections to the
	 * database in lieu of a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
	 * <p/>
	 * See JPA 2 section 8.2.1.9
	 */
	public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";

	/**
	 * The JDBC connection url to use to connect to the database.
	 * <p/>
	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_USER} and {@link #JDBC_PASSWORD} to define how to make connections to the
	 * database in lieu of a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
	 * <p/>
	 * See section 8.2.1.9
	 */
	public static final String JDBC_URL = "javax.persistence.jdbc.url";

	/**
	 * The JDBC connection user name.
	 * <p/>
	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_URL} and {@link #JDBC_PASSWORD} to define how to make connections to the
	 * database in lieu of a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
	 * <p/>
	 * See section 8.2.1.9
	 */
	public static final String JDBC_USER = "javax.persistence.jdbc.user";

	/**
	 * The JNDI name of a JTA {@link javax.sql.DataSource}.
	 * <p/>
	 * See JPA 2 sections 9.4.3 and 8.2.1.5
	 */
	public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";

	/**
	 * Used to request (hint) a pessimistic lock scope.
	 * <p/>
	 * See JPA 2 sections 8.2.1.9 and 3.4.4.3
	 */
	public static final String LOCK_SCOPE = "javax.persistence.lock.scope";

	/**
	 * Used to request (hint) a pessimistic lock timeout (in milliseconds).
	 * <p/>
	 * See JPA 2 sections 8.2.1.9 and 3.4.4.3
	 */
	public static final String LOCK_TIMEOUT = "javax.persistence.lock.timeout";

	/**
	 * The JNDI name of a non-JTA {@link javax.sql.DataSource}.
	 * <p/>
	 * See JPA 2 sections 9.4.3 and 8.2.1.5
	 */
	public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";

	/**
	 * Used to coordinate with bean validators
	 * <p/>
	 * See JPA 2 section 8.2.1.9
	 */
	public static final String PERSIST_VALIDATION_GROUP = "javax.persistence.validation.group.pre-persist";

	/**
	 * THe name of the {@link javax.persistence.spi.PersistenceProvider} implementor
	 * <p/>
	 * See JPA 2 sections 9.4.3 and 8.2.1.4
	 */
	public static final String PROVIDER = "javax.persistence.provider";

	/**
	 * Used to coordinate with bean validators
	 * <p/>
	 * See JPA 2 section 8.2.1.9
	 */
	public static final String REMOVE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-remove";

	/**
	 * Used to indicate whether second-level (what JPA terms shared cache) caching is enabled as per the rules defined in JPA 2 section
	 * 3.1.7.
	 * <p/>
	 * See JPA 2 sections 9.4.3 and 8.2.1.7
	 * 
	 * @see javax.persistence.SharedCacheMode
	 */
	public static final String SHARED_CACHE_MODE = "javax.persistence.sharedCache.mode";

	/**
	 * NOTE : Not a valid EMF property...
	 * <p/>
	 * Used to indicate if the provider should attempt to retrieve requested data in the shared cache.
	 * 
	 * @see javax.persistence.CacheRetrieveMode
	 */
	public static final String SHARED_CACHE_RETRIEVE_MODE = "javax.persistence.cache.retrieveMode";

	/**
	 * NOTE : Not a valid EMF property...
	 * <p/>
	 * Used to indicate if the provider should attempt to store data loaded from the database in the shared cache.
	 * 
	 * @see javax.persistence.CacheStoreMode
	 */
	public static final String SHARED_CACHE_STORE_MODE = "javax.persistence.cache.storeMode";

	/**
	 * The type of transactions supported by the entity managers.
	 * <p/>
	 * See JPA 2 sections 9.4.3 and 8.2.1.2
	 */
	public static final String TRANSACTION_TYPE = "javax.persistence.transactionType";

	/**
	 * Used to coordinate with bean validators
	 * <p/>
	 * See JPA 2 section 8.2.1.9
	 */
	public static final String UPDATE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-update";

	/**
	 * Used to pass along any discovered validator factory.
	 */
	public static final String VALIDATION_FACTORY = "javax.persistence.validation.factory";

	/**
	 * Used to indicate what form of automatic validation is in effect as per rules defined in JPA 2 section 3.6.1.1
	 * <p/>
	 * See JPA 2 sections 9.4.3 and 8.2.1.8
	 * 
	 * @see javax.persistence.ValidationMode
	 */
	public static final String VALIDATION_MODE = "javax.persistence.validation.mode";
}

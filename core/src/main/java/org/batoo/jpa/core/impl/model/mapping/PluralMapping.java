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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.OrderColumn;
import org.batoo.jpa.core.impl.model.type.TypeImpl;

/**
 * The base class for {@link EmbeddedMapping} and {@link BasicMapping}.
 * 
 * @param <Z>
 *            the source type
 * @param <C>
 *            the collection type
 * @param <E>
 *            the destination type
 * 
 * @author hceylan
 * @since $version
 */
public interface PluralMapping<Z, C, E> extends JoinedMapping<Z, C, E> {

	/**
	 * Attaches the child.
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * @param key
	 *            the key, may be null
	 * @param child
	 *            the child
	 * @param index
	 *            the index
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void attach(ConnectionImpl connection, ManagedInstance<?> instance, Object key, Object child, int index) throws SQLException;

	/**
	 * Returns if merges are cascaded.
	 * 
	 * @return true if merges are cascaded, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	boolean cascadesMerge();

	/**
	 * detaches the child.
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * @param key
	 *            the key, may be null
	 * @param child
	 *            the child
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void detach(ConnectionImpl connection, ManagedInstance<?> instance, Object key, Object child) throws SQLException;

	/**
	 * Details all the children.
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the instance
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void detachAll(ConnectionImpl connection, ManagedInstance<?> instance) throws SQLException;

	/**
	 * Enhances the collection to the managed collection
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void enhance(ManagedInstance<?> instance);

	/**
	 * Returns the values of the mapping.
	 * 
	 * @param instance
	 * @return the value of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Object get(Object instance);

	/**
	 * Returns the class of the map key.
	 * 
	 * @return the class of the map key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	TypeImpl<?> getMapKeyClass();

	/**
	 * Returns child mapping of the <code>type</code>.
	 * 
	 * @param path
	 *            the path
	 * @return the child mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Mapping<?, ?, ?> getMapping(String path);

	/**
	 * Returns the order by of the association.
	 * 
	 * @return the order by of the association
	 * 
	 * @since $versionpublic RootMapping<?> getRoot()
	 * @author hceylan
	 */
	String getOrderBy();

	/**
	 * Returns the order column.
	 * 
	 * @return the order column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	OrderColumn getOrderColumn();

	/**
	 * Returns the root mapping.
	 * 
	 * @return the root mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	RootMapping<?> getRoot();

	/**
	 * Loads the collection eagerly.
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void load(ManagedInstance<?> instance);

	/**
	 * Loads and returns the collection.
	 * 
	 * @param instance
	 *            the managed instance owning the collection
	 * @return the loaded collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Collection<? extends E> loadCollection(ManagedInstance<?> instance);

	/**
	 * Loads and returns the map
	 * 
	 * @param instance
	 * @return the loaded map
	 * @param <K>
	 *            the key type of the map
	 * 
	 * @since $version
	 * @author hceylan
	 */
	<K> Map<? extends K, ? extends E> loadMap(ManagedInstance<?> instance);

	/**
	 * Sets the lazy instance for the collection
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void setLazy(ManagedInstance<?> instance);

	/**
	 * Sorts the managed list of the instance
	 * 
	 * @param instance
	 *            the owner instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	void sortList(Object instance);
}

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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.Joinable;
import org.batoo.jpa.core.impl.jdbc.OrderColumn;

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
	 * @param batch
	 *            the batch of children
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since $version
	 */
	void attach(Connection connection, ManagedInstance<?> instance, Joinable[] batch, int size) throws SQLException;

	/**
	 * Returns if merges are cascaded.
	 * 
	 * @return true if merges are cascaded, false otherwise
	 * 
	 * @since $version
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
	 */
	void detach(Connection connection, ManagedInstance<?> instance, Object key, Object child) throws SQLException;

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
	 */
	void detachAll(Connection connection, ManagedInstance<?> instance) throws SQLException;

	/**
	 * Enhances the collection to the managed collection
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 */
	void enhance(ManagedInstance<?> instance);

	/**
	 * Returns the values of the mapping.
	 * 
	 * @param instance
	 * @return the value of the instance
	 * 
	 * @since $version
	 */
	Object get(Object instance);

	/**
	 * Returns child mapping of the <code>type</code>.
	 * 
	 * @param path
	 *            the path
	 * @return the child mapping
	 * 
	 * @since $version
	 */
	Mapping<?, ?, ?> getMapping(String path);

	/**
	 * Returns the order by of the association.
	 * 
	 * @return the order by of the association
	 * 
	 * @since $versionpublic RootMapping<?> getRoot()
	 */
	String getOrderBy();

	/**
	 * Returns the order column.
	 * 
	 * @return the order column
	 * 
	 * @since $version
	 */
	OrderColumn getOrderColumn();

	/**
	 * Returns the root mapping.
	 * 
	 * @return the root mapping
	 * 
	 * @since $version
	 */
	RootMapping<?> getRoot();

	/**
	 * Loads the collection eagerly.
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
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
	 */
	<K> Map<? extends K, ? extends E> loadMap(ManagedInstance<?> instance);

	/**
	 * Sets the collection manually for the mapping.
	 * 
	 * @param instance
	 *            the managed instance
	 * @param children
	 *            the collection of children
	 * 
	 * @since $version
	 */
	public void setCollection(ManagedInstance<?> instance, Collection<? extends E> children);

	/**
	 * Sets the lazy instance for the collection
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 */
	void setLazy(ManagedInstance<?> instance);

	/**
	 * Sorts the managed list of the instance
	 * 
	 * @param instance
	 *            the owner instance
	 * 
	 * @since $version
	 */
	void sortList(Object instance);
}

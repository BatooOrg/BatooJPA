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

package org.batoo.common.util;

import java.util.List;
import java.util.Map;

/**
 * Extension of the {@code Map} interface that stores multiple values.
 * <p>
 * The original code is based on org.springframework.util.MultiValueMap
 * 
 * @param <K>
 *            The type of the key
 * @param <V>
 *            The value of the key
 * @author hceylan
 * @since 2.0.1
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

	/**
	 * Add the given single value to the current list of values for the given key.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value to be added
	 */
	void add(K key, V value);

	/**
	 * Return the first value for the given key.
	 * 
	 * @param key
	 *            the key
	 * @return the first value for the specified key, or <code>null</code>
	 */
	V getFirst(K key);

	/**
	 * Set the given single value under the given key.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value to set
	 */
	void set(K key, V value);

	/**
	 * Set the given values under.
	 * 
	 * @param values
	 *            the values.
	 */
	void setAll(Map<K, V> values);

	/**
	 * Returns the first values contained in this {@code MultiValueMap}.
	 * 
	 * @return a single value representation of this map
	 */
	Map<K, V> toSingleValueMap();

}

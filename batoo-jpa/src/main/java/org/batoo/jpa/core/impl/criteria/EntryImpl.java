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
package org.batoo.jpa.core.impl.criteria;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Entry for Map collections
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * 
 * @author hceylan
 * @since $version
 */
public class EntryImpl<K, V> implements Entry<K, V> {

	private final K key;
	private final V value;

	/**
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntryImpl(K key, V value) {
		super();

		this.key = key;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public final boolean equals(Object o) {
		if (!(o instanceof Map.Entry)) {
			return false;
		}

		final Map.Entry e = (Map.Entry) o;
		final Object k1 = this.getKey();
		final Object k2 = e.getKey();
		if ((k1 == k2) || ((k1 != null) && k1.equals(k2))) {
			final Object v1 = this.getValue();
			final Object v2 = e.getValue();
			if ((v1 == v2) || ((v1 != null) && v1.equals(v2))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public K getKey() {
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int hashCode() {
		return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.getKey() + "=" + this.getValue();
	}
}

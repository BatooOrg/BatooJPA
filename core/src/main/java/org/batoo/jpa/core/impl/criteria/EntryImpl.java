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

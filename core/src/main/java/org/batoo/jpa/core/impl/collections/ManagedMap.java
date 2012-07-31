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
package org.batoo.jpa.core.impl.collections;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.util.BatooUtils;

import com.google.common.collect.Maps;

/**
 * The set implementation of managed collection.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <K>
 *            The key type of the represented map
 * @param <V>
 *            The value type of the represented map
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedMap<X, K, V> extends ManagedCollection<V> implements Map<K, V> {

	private final HashMap<K, V> delegate = Maps.newHashMap();
	private HashMap<K, V> snapshot;

	private boolean initialized;

	/**
	 * Constructor for lazy initialization.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * @param lazy
	 *            if the collection is lazy
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedMap(PluralMapping<?, ?, V> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		super(mapping, managedInstance);

		this.initialized = !lazy;
	}

	/**
	 * Default constructor.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * @param values
	 *            the initial values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedMap(PluralMapping<?, ?, V> mapping, ManagedInstance<?> managedInstance, Map<? extends K, ? extends V> values) {
		this(mapping, managedInstance, false);

		this.delegate.putAll(values);

		this.initialized = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addChild(EntryImpl<Object, ManagedInstance<?>> child) {
		final K k = (K) child.getKey();
		final V v = (V) child.getValue().getInstance();

		if (!this.delegate.keySet().contains(k) && !this.delegate.values().contains(v)) {
			this.delegate.put(k, v);

			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addElement(EntryImpl<Object, ?> child) {
		final K k = (K) child.getKey();
		final V v = (V) child.getValue();

		if (!this.delegate.keySet().contains(k) && !this.delegate.values().contains(v)) {
			this.delegate.put(k, v);

			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.snapshot();

		this.delegate.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsKey(Object key) {
		this.initialize();

		return this.delegate.containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsValue(Object value) {
		this.initialize();

		return this.delegate.containsValue(value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		this.initialize();

		// TODO wrap
		return this.delegate.entrySet();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(ConnectionImpl connection, boolean removals, boolean force) throws SQLException {
		if (this.removed(connection, removals)) {
			return;
		}

		final ManagedInstance<?> managedInstance = this.getManagedInstance();
		final PluralMapping<?, ?, V> mapping = this.getMapping();

		// forced creation of relations for the new entities
		if (force) {
			for (final Entry<K, V> entry : this.delegate.entrySet()) {
				mapping.attach(connection, managedInstance, entry.getKey(), entry.getValue(), -1);
			}

			return;
		}

		if (this.snapshot == null) {
			return;
		}

		if (removals) {
			// delete the removals
			final Collection<K> childrenRemoved = BatooUtils.subtract(this.snapshot.keySet(), this.delegate.keySet());
			for (final K key : childrenRemoved) {
				mapping.detach(connection, managedInstance, key, this.snapshot.get(key));
			}
		}
		else {
			// create the additions
			final Collection<K> childrenAdded = BatooUtils.subtract(this.delegate.keySet(), this.snapshot.keySet());
			for (final K key : childrenAdded) {
				mapping.attach(connection, managedInstance, key, this.delegate.get(key), -1);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V get(Object key) {
		this.initialize();

		return this.delegate.get(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<V> getDelegate() {
		return this.delegate.values();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Collection<V> getSnapshot() {
		return this.snapshot != null ? this.snapshot.values() : null;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initialize() {
		if (!this.initialized) {
			if (this.getManagedInstance() == null) {
				throw new PersistenceException("No session to initialize the collection");
			}

			this.delegate.putAll(this.getMapping().<K> loadMap(this.getManagedInstance()));

			this.initialized = true;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEmpty() {
		this.initialize();

		return this.delegate.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<K> keySet() {
		this.initialize();

		// TODO wrap
		return this.delegate.keySet();
	}

	private UnsupportedOperationException noDuplicates() {
		return new UnsupportedOperationException("Duplicates are not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V put(K key, V value) {
		this.snapshot();

		if ((key == null) || (value == null)) {
			throw new NullPointerException();
		}

		if (this.delegate.values().contains(value)) {
			throw this.noDuplicates();
		}

		if (this.delegate.values().contains(key)) {
			throw this.noDuplicates();
		}

		final V v = this.delegate.put(key, value);

		this.changed();

		return v;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.snapshot();

		for (final java.util.Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refreshChildren() {
		super.reset();

		this.snapshot = null;
		this.delegate.clear();

		this.delegate.putAll(this.getMapping().<K> loadMap(this.getManagedInstance()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V remove(Object key) {
		this.snapshot();

		if (this.delegate.containsKey(key)) {
			this.changed();
		}

		return this.delegate.remove(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void removeChild(V child) {
		for (final Entry<K, V> entry : this.delegate.entrySet()) {
			if (entry.getValue().equals(child)) {
				this.delegate.remove(entry.getKey());
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int size() {
		this.initialize();

		return this.delegate.size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void snapshot() {
		this.initialize();

		if (this.snapshot == null) {
			this.snapshot = Maps.newHashMap(this.delegate);
			this.reset();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final Object id = this.getManagedInstance().getId() != null ? this.getManagedInstance().getId().getId() : null;
		final String instance = this.getManagedInstance().getType().getName() + "@" + id;

		return "ManagedMap [initialized=" + this.initialized + ", managedInstance=" + instance + ", delegate=" + this.delegate + ", snapshot=" + this.snapshot
			+ ", mapping=" + this.getMapping() + "]";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<V> values() {
		this.initialize();

		return this.delegate.values();
	}
}

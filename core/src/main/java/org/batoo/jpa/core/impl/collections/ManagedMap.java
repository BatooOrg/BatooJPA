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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.util.Pair;

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
	private SingularMapping<? super V, ?> keyMapping;
	private Pair<BasicMapping<? super V, ?>, BasicAttribute<?, ?>>[] keyMappins;

	private boolean initialized;
	private EmbeddableTypeImpl<K> keyClass;

	/**
	 * Constructor for lazy initialization.
	 * 
	 * @param association
	 *            the association
	 * @param managedInstance
	 *            the managed instance
	 * @param lazy
	 *            if the collection is lazy
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ManagedMap(PluralAssociationMapping<?, ?, V> association, ManagedInstance<?> managedInstance, boolean lazy) {
		super(association, managedInstance);

		this.initialized = !lazy;
		this.keyMapping = association.getKeyMapping();
		this.keyMappins = association.getKeyMappins();
		this.keyClass = (EmbeddableTypeImpl<K>) association.getKeyClass();
	}

	/**
	 * Default constructor.
	 * 
	 * @param association
	 *            the association
	 * @param managedInstance
	 *            the managed instance
	 * @param values
	 *            the initial values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedMap(PluralAssociationMapping<?, ?, V> association, ManagedInstance<?> managedInstance, Map<? extends K, ? extends V> values) {
		super(association, managedInstance);

		this.delegate.putAll(values);

		this.initialized = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addChild(Object child) {
		if (this.delegate.values().contains(child)) {
			return false;
		}

		Object key = null;
		if (this.keyMapping != null) {
			key = this.keyMapping.get(child);
		}
		else {
			key = this.keyClass.newInstance();
			for (final Pair<BasicMapping<? super V, ?>, BasicAttribute<?, ?>> pair : this.keyMappins) {
				pair.getSecond().set(key, pair.getFirst().get(child));
			}
		}

		this.delegate.put((K) key, (V) child);

		return true;
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

			this.putAll(this.getAssociation().loadCollection(this.getManagedInstance()));

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

		V v = this.delegate.get(key);
		if (v.equals(value) && (v != value)) {
			return this.delegate.put(key, value);
		}

		if (this.delegate.values().contains(key)) {
			throw this.noDuplicates();
		}

		v = this.delegate.put(key, value);

		this.changed();

		return v;
	}

	private void putAll(Collection<? extends V> children) {
		for (final V child : children) {
			this.addChild(child);
		}
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
	public void refreshChildren(Collection<? extends V> children) {
		super.reset();

		this.snapshot = null;

		this.delegate.clear();
		this.putAll(children);
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
			+ ", mapping=" + this.getAssociation() + "]";
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

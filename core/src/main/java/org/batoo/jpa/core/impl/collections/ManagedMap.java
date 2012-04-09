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
import java.util.Map;
import java.util.Set;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

import com.google.common.collect.Maps;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedMap<K, V> implements Map<K, V>, ManagedCollection<V> {

	private final SessionImpl session;
	private final ManagedInstance<?> managedInstance;
	private final CollectionMapping<?, Map<K, V>, V> mapping;

	private final Map<K, V> map = Maps.newHashMap();

	private Collection<V> snapshot;
	private boolean changed;

	/**
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the owner managed instance
	 * @param mapping
	 *            the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedMap(SessionImpl session, ManagedInstance<?> managedInstance, CollectionMapping<?, Map<K, V>, V> mapping) {
		super();

		this.session = session;
		this.managedInstance = managedInstance;
		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.initializeIfNecessary();

		this.changed = true;

		this.getMap().clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsKey(Object key) {
		this.initializeIfNecessary();

		return this.getMap().containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsValue(Object value) {
		this.initializeIfNecessary();

		return this.getMap().containsValue(value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		this.initializeIfNecessary();

		return this.getMap().entrySet();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V get(Object key) {
		this.initializeIfNecessary();

		return this.getMap().get(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Iterable<V> getCollection() {
		return this.map.values();
	}

	/**
	 * Returns the map.
	 * 
	 * @return the map
	 * @since $version
	 */
	public Map<K, V> getMap() {
		return this.map;
	}

	/**
	 * Initializes the collection if necessary
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected final void initializeIfNecessary() {
		if (this.snapshot == null) {
			this.snapshot = this.session.getEntityManager().findAll(this.managedInstance, this.mapping);
			// TODO putall
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEmpty() {
		this.initializeIfNecessary();

		return this.getMap().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<K> keySet() {
		this.initializeIfNecessary();

		return this.getMap().keySet();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V put(K key, V value) {
		this.initializeIfNecessary();

		this.changed = true;

		return this.getMap().put(key, value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.initializeIfNecessary();

		this.changed = true;

		this.getMap().putAll(m);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V remove(Object key) {
		this.initializeIfNecessary();

		this.changed = true;

		return this.getMap().remove(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void reset() {
		this.snapshot = null;
		this.map.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int size() {
		this.initializeIfNecessary();

		return this.getMap().size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<V> values() {
		this.initializeIfNecessary();

		return this.getMap().values();
	}

}

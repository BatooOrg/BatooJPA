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
package org.batoo.jpa.core.pool;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

/**
 * FIXME requires proper implementation
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * 
 * @author hceylan
 * @since $version
 */
@Beta
public class GenericKeyedPool<K, V> implements KeyedObjectPool<K, V> {

	private static final int MIN_SIZE = 1;
	private static final int MAX_SIZE = 15;

	private final KeyedPoolableObjectFactory<K, V> factory;
	private final Map<K, LinkedList<V>> poolMap;

	/**
	 * @param factory
	 *            the factory
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public GenericKeyedPool(KeyedPoolableObjectFactory<K, V> factory) {
		super();

		this.factory = factory;
		this.poolMap = Maps.newConcurrentMap();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addObject(K key) throws Exception, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
		final LinkedList<V> pool = this.getPool(key);

		while (pool.size() <= GenericKeyedPool.MIN_SIZE) {
			pool.addLast(this.factory.makeObject(key));
		}

		return pool.pollFirst();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() throws Exception, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear(K key) throws Exception, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() throws Exception {
		for (final Iterator<Entry<K, LinkedList<V>>> i = this.poolMap.entrySet().iterator(); i.hasNext();) {
			final Entry<K, LinkedList<V>> e = i.next();

			this.shrinkTo(e.getKey(), e.getValue(), 0);

			i.remove();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNumActive() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNumActive(K key) throws UnsupportedOperationException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNumIdle() throws UnsupportedOperationException {
		return this.poolMap.size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNumIdle(K key) throws UnsupportedOperationException {
		return this.getPool(key).size();
	}

	private LinkedList<V> getPool(K key) {
		if (this.poolMap.containsKey(key)) {
			return this.poolMap.get(key);
		}

		final LinkedList<V> pool = new LinkedList<V>();
		this.poolMap.put(key, pool);

		return pool;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void invalidateObject(K key, V obj) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void returnObject(K key, V obj) throws Exception {
		final LinkedList<V> pool = this.getPool(key);
		pool.addLast(obj);

		this.shrinkTo(key, pool, GenericKeyedPool.MAX_SIZE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param maxSize
	 *            the maximum size of the pool
	 * @param pool
	 *            the pool
	 * @param key
	 *            the key
	 * @throws Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void shrinkTo(K key, LinkedList<V> pool, int maxSize) throws Exception {
		while (pool.size() > maxSize) {
			final V obj = pool.getFirst();
			this.factory.destroyObject(key, obj);
		}
	}
}

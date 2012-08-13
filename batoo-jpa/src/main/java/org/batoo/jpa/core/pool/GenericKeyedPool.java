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
package org.batoo.jpa.core.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
	private final HashMap<K, LinkedList<V>> poolMap = Maps.newHashMap();

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
	public V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
		final LinkedList<V> pool = this.getPool(key);

		if (pool.size() <= GenericKeyedPool.MIN_SIZE) {
			synchronized (this) {
				while (pool.size() <= GenericKeyedPool.MIN_SIZE) {
					pool.addLast(this.factory.makeObject(key));
				}
			}
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
		LinkedList<V> pool = this.poolMap.get(key);
		if (pool != null) {
			return pool;
		}

		synchronized (this) {
			pool = this.poolMap.get(key);
			if (pool != null) {
				return pool;
			}

			this.poolMap.put(key, pool = new LinkedList<V>());

			return pool;
		}
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

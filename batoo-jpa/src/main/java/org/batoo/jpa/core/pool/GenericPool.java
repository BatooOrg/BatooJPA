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

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

import com.google.common.annotations.Beta;

/**
 * FIXME requires proper implementation
 * 
 * @param <T>
 *            the type of the pool
 * 
 * @author hceylan
 * @since $version
 */
@Beta
public class GenericPool<T> implements ObjectPool<T> {

	private static final int MIN_SIZE = 5;
	private static final int MAX_SIZE = 15;

	private final PoolableObjectFactory<T> factory;
	private final Deque<T> pool;
	private boolean active;

	/**
	 * @param factory
	 *            the factory of the pool
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public GenericPool(PoolableObjectFactory<T> factory) {
		super();

		this.factory = factory;
		this.pool = new LinkedBlockingDeque<T>();
		this.active = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
		while (this.pool.size() < GenericPool.MIN_SIZE) {
			this.pool.addLast(this.factory.makeObject());
		}

		return this.pool.pollFirst();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() throws Exception, UnsupportedOperationException {
		while (this.pool.size() > 0) {
			final T item = this.pool.pollFirst();
			if (item != null) {
				this.factory.destroyObject(item);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() throws Exception {
		this.active = false;

		this.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNumActive() throws UnsupportedOperationException {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getNumIdle() throws UnsupportedOperationException {
		return this.pool.size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void invalidateObject(T obj) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void returnObject(T obj) throws Exception {
		if (this.active) {
			this.pool.addLast(obj);
			this.shrinkTo(GenericPool.MAX_SIZE);
		}
		else {
			this.factory.destroyObject(obj);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFactory(PoolableObjectFactory<T> factory) throws IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param maxSize
	 * @throws Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void shrinkTo(int maxSize) throws Exception {
		while (this.pool.size() > maxSize) {
			final T obj = this.pool.getFirst();
			this.factory.destroyObject(obj);
		}
	}

}

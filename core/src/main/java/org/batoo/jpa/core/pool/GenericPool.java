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

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

import com.google.common.annotations.Beta;

/**
 * FIXME requires proper implementation
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
		while (this.pool.size() < MIN_SIZE) {
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
			this.shrinkTo(MAX_SIZE);
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

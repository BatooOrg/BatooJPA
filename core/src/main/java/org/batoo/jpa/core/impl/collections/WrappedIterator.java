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

import java.util.Iterator;

/**
 * A wrapper implementation of {@link Iterator} to track changes in Managed collections.
 * 
 * @param <E>
 *            the type of elements returned by this iterator
 * 
 * @author hceylan
 * @since $version
 */
public class WrappedIterator<E> implements Iterator<E> {

	private final Iterator<E> delegate;

	/**
	 * @param delegate
	 *            the delegate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public WrappedIterator(Iterator<E> delegate) {
		super();

		this.delegate = delegate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E next() {
		return this.delegate.next();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void remove() {
		this.delegate.remove();
	}

}

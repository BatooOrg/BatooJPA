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
package org.batoo.jpa.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class IdentityHashSet<E> implements Set<E> {

	private final Set<E> delegate;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentityHashSet() {
		super();

		this.delegate = Collections.newSetFromMap(new IdentityHashMap<E, Boolean>());
	}

	/**
	 * @param size
	 *            the expected size
	 * @since $version
	 * @author hceylan
	 */
	public IdentityHashSet(int size) {
		super();

		this.delegate = Collections.newSetFromMap(new IdentityHashMap<E, Boolean>(size));
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		return this.delegate.add(e);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return this.delegate.addAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		this.delegate.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.delegate.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.delegate.containsAll(c);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.delegate.equals(o);
	}

	/**
	 * @return
	 * @see java.util.Set#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return this.delegate.iterator();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.delegate.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.delegate.removeAll(c);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.delegate.retainAll(c);
	}

	/**
	 * @return
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return this.delegate.size();
	}

	/**
	 * @return
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.delegate.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.delegate.toArray(a);
	}

}

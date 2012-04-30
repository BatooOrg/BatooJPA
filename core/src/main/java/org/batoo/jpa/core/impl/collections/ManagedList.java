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
import java.util.List;
import java.util.ListIterator;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedList<E> extends AbstractManagedCollection<E> implements List<E> {

	private final List<E> list = Lists.newArrayList();

	/**
	 * @param session
	 *            the session
	 * @param managedInstance
	 *            the owner managed instance
	 * @param mapping
	 *            the mapping
	 * @param existing
	 *            the existing list may be null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedList(ManagedInstance<?> managedInstance, CollectionMapping<?, List<E>, E> association, boolean lazy) {
		super(managedInstance, association, lazy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void add(int index, E element) {
		this.initializeIfNecessary();

		this.list.add(index, element);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		this.initializeIfNecessary();

		return this.list.addAll(index, c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E get(int index) {
		this.initializeIfNecessary();

		return this.list.get(index);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<E> getCollection() {
		return this.list;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int indexOf(Object o) {
		this.initializeIfNecessary();

		return this.list.indexOf(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int lastIndexOf(Object o) {
		this.initializeIfNecessary();

		return this.list.lastIndexOf(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListIterator<E> listIterator() {
		this.initializeIfNecessary();

		return this.list.listIterator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		this.initializeIfNecessary();

		return this.list.listIterator(index);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E remove(int index) {
		this.initializeIfNecessary();

		return this.list.remove(index);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E set(int index, E element) {
		this.initializeIfNecessary();

		return this.list.set(index, element);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		this.initializeIfNecessary();

		return this.list.subList(fromIndex, toIndex);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedList [list=" + this.list + ", managedInstance=" + this.managedInstance + ", snapshot=" + this.snapshot + "]";
	}

}

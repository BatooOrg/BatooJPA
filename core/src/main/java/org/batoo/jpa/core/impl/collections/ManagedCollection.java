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
import java.util.Iterator;

import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

/**
 * Collection to manage persistent entity collections.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ManagedCollection<E> implements Collection<E> {

	private final SessionImpl session;
	private final ManagedInstance<?> managedInstance;

	private boolean changed;

	private Collection<E> snapshot;
	private final CollectionMapping<?, ? extends Collection<E>, E> mapping;

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
	public ManagedCollection(SessionImpl session, ManagedInstance<?> managedInstance,
		CollectionMapping<?, ? extends Collection<E>, E> mapping) {
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
	public boolean add(E e) {
		return this.changed |= this.getCollection().add(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		this.initializeIfNecessary();

		return this.changed |= this.getCollection().addAll(c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.initializeIfNecessary();

		this.changed = true;

		this.getCollection().clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object o) {
		this.initializeIfNecessary();

		return this.getCollection().contains(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		this.initializeIfNecessary();

		return this.getCollection().containsAll(c);
	}

	/**
	 * Subclasses must re-implement to provide the collection.
	 * 
	 * @return the collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Collection<E> getCollection() {
		return null;
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

			this.getCollection().addAll(this.snapshot);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEmpty() {
		this.initializeIfNecessary();

		return this.getCollection().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Iterator<E> iterator() {
		this.initializeIfNecessary();

		return this.getCollection().iterator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean remove(Object o) {
		this.initializeIfNecessary();

		return this.getCollection().remove(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		this.initializeIfNecessary();

		return this.getCollection().removeAll(c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		this.initializeIfNecessary();

		return this.getCollection().retainAll(c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int size() {
		this.initializeIfNecessary();

		return this.getCollection().size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object[] toArray() {
		this.initializeIfNecessary();

		return this.getCollection().toArray();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		this.initializeIfNecessary();

		return this.getCollection().toArray(a);
	}

}

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

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;

import com.google.common.collect.Lists;

/**
 * Collection to manage persistent entity collections.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractManagedCollection<E> implements Collection<E>, ManagedCollection<E> {

	protected final ManagedInstance<?> managedInstance;

	private boolean changed;

	protected Collection<E> snapshot;
	private final CollectionMapping<?, ? extends Collection<E>, E> mapping;

	private boolean initialized;

	/**
	 * @param managedInstance
	 *            the owner managed instance
	 * @param mapping
	 *            the mapping
	 * @param lazy
	 *            if the collection is lazy
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractManagedCollection(ManagedInstance<?> managedInstance, CollectionMapping<?, ? extends Collection<E>, E> mapping,
		boolean lazy) {
		super();

		this.managedInstance = managedInstance;
		this.mapping = mapping;
		this.initialized = !lazy;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AbstractManagedCollection<?> other = (AbstractManagedCollection<?>) obj;
		if (!this.mapping.equals(other.mapping)) {
			return false;
		}
		if (!this.getCollection().equals(other.getCollection())) {
			return false;
		}
		return true;
	}

	/**
	 * Subclasses must re-implement to provide the collection.
	 * 
	 * @return the collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public Collection<E> getCollection() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.mapping == null) ? 0 : this.mapping.hashCode());
		result = (prime * result) + ((this.getCollection() == null) ? 0 : this.getCollection().hashCode());
		return result;
	}

	/**
	 * Initializes the collection if necessary
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected final void initializeIfNecessary() {
		if (!this.initialized) {
			this.initialized = true;
			this.snapshot = this.managedInstance.getSession().getEntityManager().findAll(this.managedInstance, this.mapping);

			this.getCollection().addAll(this.snapshot);
		}
		else if (this.snapshot == null) {
			this.snapshot = Lists.newArrayList(this.getCollection());
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
	public void reset() {
		this.snapshot = this.getCollection();
		this.getCollection().clear();
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

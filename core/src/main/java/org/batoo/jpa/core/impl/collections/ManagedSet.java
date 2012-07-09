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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

import com.google.common.collect.Sets;

/**
 * The set implementation of managed collection.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * @author hceylan
 * @since $version
 */
public class ManagedSet<X, E> extends ManagedCollection<E> implements Set<E> {

	private final HashSet<E> delegate = Sets.newHashSet();
	private HashSet<E> snapshot;

	private final transient PluralAssociationMapping<?, ?, E> mapping;
	private final transient ManagedInstance<?> managedInstance;

	private boolean initialized;

	/**
	 * Constructor for lazy initialization.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * @param lazy
	 *            if the collection is lazy
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedSet(PluralAssociationMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		super();

		this.mapping = mapping;
		this.managedInstance = managedInstance;

		this.initialized = !lazy;
	}

	/**
	 * Default constructor.
	 * 
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * @param values
	 *            the initial values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedSet(PluralAssociationMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, Collection<? extends E> values) {
		super();

		this.delegate.addAll(values);
		this.mapping = mapping;
		this.managedInstance = managedInstance;

		this.initialized = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean add(E e) {
		this.initialize();
		return this.delegate.add(e);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		this.initialize();
		this.snapshot();
		return this.changed(this.delegate.addAll(c));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addChild(Object child) {
		if (!this.delegate.contains(child)) {
			return this.delegate.add((E) child);
		}

		return false;
	}

	private <T> T changed(T value) {
		this.managedInstance.markCollectionChanged(this.mapping);

		return value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.initialize();
		if (this.delegate.size() > 0) {
			this.changed(null);
		}
		this.delegate.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object o) {
		this.initialize();
		return this.delegate.contains(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		this.initialize();
		return this.delegate.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return this.delegate.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<E> getDelegate() {
		return this.delegate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initialize() {
		if (!this.initialized) {
			if (this.managedInstance == null) {
				throw new PersistenceException("No session to initialize the collection");
			}

			this.delegate.addAll(this.mapping.loadCollection(this.managedInstance));

			this.initialized = true;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEmpty() {
		this.initialize();
		return this.delegate.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Iterator<E> iterator() {
		this.initialize();
		return new WrappedIterator<E>(this.delegate.iterator()) {
			/**
			 * {@inheritDoc}
			 * 
			 */
			@Override
			public void remove() {
				ManagedSet.this.snapshot();
				ManagedSet.this.changed(null);

				super.remove();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean remove(Object o) {
		this.initialize();
		this.snapshot();
		return this.changed(this.delegate.remove(o));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		this.initialize();
		this.snapshot();
		return this.changed(this.delegate.removeAll(c));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		this.initialize();
		this.snapshot();
		return this.changed(this.delegate.retainAll(c));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int size() {
		this.initialize();
		return this.delegate.size();
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void snapshot() {
		if (this.snapshot != null) {
			this.snapshot = Sets.newHashSet(this.delegate);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object[] toArray() {
		this.initialize();
		return this.delegate.toArray();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		this.initialize();
		return this.delegate.toArray(a);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedSet [initialized=" + this.initialized + ", managedInstance=" + this.managedInstance + ", delegate=" + this.delegate + ", snapshot="
			+ this.snapshot + ", mapping=" + this.mapping + "]";
	}
}

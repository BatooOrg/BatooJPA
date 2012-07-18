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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.ObjectUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.JoinableTable;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * The list implementation of managed collection.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * @author hceylan
 * @since $version
 */
public class ManagedList<X, E> extends ManagedCollection<E> implements List<E> {

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	private final class ManagedListIterator extends WrappedListIterator<E> {
		private E last;

		/**
		 * 
		 * @since $version
		 * @author hceylan
		 */
		private ManagedListIterator(ListIterator<E> delegate) {
			super(delegate);
		}

		@Override
		public void add(E e) {
			ManagedList.this.snapshot();
			ManagedList.this.changed();

			if (e == null) {
				throw new NullPointerException();
			}

			if (ManagedList.this.delegate.contains(e)) {
				throw ManagedList.this.noDuplicates();
			}

			super.add(e);
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public E next() {
			return this.last = super.next();
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public E previous() {
			return this.last = super.previous();
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public void remove() {
			ManagedList.this.snapshot();
			ManagedList.this.changed();

			super.remove();
		}

		@Override
		public void set(E e) {
			ManagedList.this.snapshot();
			ManagedList.this.changed();

			if (e == null) {
				throw new NullPointerException();
			}

			if (ManagedList.this.delegate.contains(e) && ObjectUtils.notEqual(e, this.last)) {
				throw ManagedList.this.noDuplicates();
			}

			super.set(e);
		}
	}

	private final ArrayList<E> delegate = Lists.newArrayList();
	private ArrayList<E> snapshot;
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
	public ManagedList(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		super(mapping, managedInstance);

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
	public ManagedList(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, Collection<? extends E> values) {
		super(mapping, managedInstance);

		this.delegate.addAll(Lists.newArrayList(values));

		for (final E e : values) {
			if (e == null) {
				throw new NullPointerException();
			}
		}

		if (Sets.newHashSet(values).size() != values.size()) {
			throw this.noDuplicates();
		}

		this.initialized = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean add(E e) {
		this.snapshot();

		if (e == null) {
			throw new NullPointerException();
		}

		if (this.delegate.contains(e)) {
			throw this.noDuplicates();
		}

		this.delegate.add(e);
		this.changed();

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void add(int index, E element) {
		this.snapshot();

		if (element == null) {
			throw new NullPointerException();
		}

		if (this.contains(element)) {
			throw this.noDuplicates();
		}

		this.delegate.add(index, element);

		this.changed();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		this.snapshot();

		for (final E e : c) {
			if (this.delegate.contains(e)) {
				throw this.noDuplicates();
			}
		}

		if (this.delegate.addAll(c)) {
			this.changed();
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		this.snapshot();

		for (final E e : c) {
			if (this.delegate.contains(e)) {
				throw this.noDuplicates();
			}
		}

		if (this.delegate.addAll(index, c)) {
			this.changed();
			return true;
		}

		return false;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		this.snapshot();

		if (this.delegate.size() > 0) {
			this.changed();
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
		this.initialize();

		return this.delegate.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(ConnectionImpl connection, boolean removals, boolean force) throws SQLException {
		if (this.removed(connection, removals)) {
			return;
		}

		// for lists the index is maintained in the database
		final ManagedInstance<?> instance = this.getManagedInstance();
		final PluralMapping<?, ?, E> mapping = this.getMapping();
		final Object source = instance.getInstance();

		// forced creation of relations for the new entities
		if (force) {
			for (int i = 0; i < this.delegate.size(); i++) {
				final JoinableTable table = mapping.getTable();
				table.performInsert(connection, source, this.delegate.get(i), i);
			}

			return;
		}

		if (this.snapshot == null) {
			return;
		}

		if (removals) {
			mapping.getTable().performRemoveAll(connection, source);
		}
		else {
			// create the additions
			for (int i = 0; i < this.delegate.size(); i++) {
				mapping.getTable().performInsert(connection, source, this.delegate.get(i), i);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E get(int index) {
		this.initialize();

		return this.delegate.get(index);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ArrayList<E> getDelegate() {
		return this.delegate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Collection<E> getSnapshot() {
		return this.snapshot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		this.initialize();

		return this.delegate.hashCode();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int indexOf(Object o) {
		this.initialize();

		return this.delegate.indexOf(o);
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initialize() {
		if (!this.initialized) {
			if (this.getManagedInstance() == null) {
				throw new PersistenceException("No session to initialize the collection");
			}

			this.delegate.addAll(this.getMapping().loadCollection(this.getManagedInstance()));

			this.getMapping().sortList(this.getManagedInstance().getInstance());

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
	 * Returns if the list is initialized.
	 * 
	 * @return true if the list is initialized, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Iterator<E> iterator() {
		this.initialize();

		return new WrappedIterator<E>(this.delegate.iterator()) {

			@Override
			public void remove() {
				ManagedList.this.snapshot();
				ManagedList.this.changed();

				super.remove();
			};
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int lastIndexOf(Object o) {
		this.initialize();

		return this.delegate.lastIndexOf(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListIterator<E> listIterator() {
		this.initialize();

		return new ManagedListIterator(this.delegate.listIterator());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		this.initialize();

		return new ManagedListIterator(this.delegate.listIterator(index));
	}

	private UnsupportedOperationException noDuplicates() {
		return new UnsupportedOperationException("Duplicates are not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refreshChildren(Collection<? extends E> children) {
		super.reset();

		this.snapshot = null;

		this.delegate.clear();
		this.delegate.addAll(children);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E remove(int index) {
		this.snapshot();

		final E e = this.delegate.remove(index);

		this.changed();

		return e;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean remove(Object o) {
		this.snapshot();

		if (this.delegate.remove(o)) {
			this.changed();
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		this.snapshot();

		if (this.delegate.retainAll(c)) {
			this.changed();

			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void removeChild(E child) {
		this.delegate.remove(child);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		this.snapshot();

		if (this.delegate.retainAll(c)) {
			this.changed();

			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public E set(int index, E element) {
		this.snapshot();

		if (this.delegate.contains(element) && ObjectUtils.notEqual(element, this.delegate.get(index))) {
			throw this.noDuplicates();
		}

		this.changed();
		return this.delegate.set(index, element);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void snapshot() {
		this.initialize();

		if (this.snapshot == null) {
			this.snapshot = Lists.newArrayList(this.delegate);
			this.reset();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		this.initialize();

		return this.delegate.subList(fromIndex, toIndex);
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
		final Object id = this.getManagedInstance().getId() != null ? this.getManagedInstance().getId().getId() : null;
		final String instance = this.getManagedInstance().getType().getName() + "@" + id;

		return "ManagedList [initialized=" + this.initialized + ", managedInstance=" + instance + ", delegate=" + this.delegate + ", snapshot=" + this.snapshot
			+ ", mapping=" + this.getMapping() + "]";
	}
}

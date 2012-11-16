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
package org.batoo.jpa.core.impl.collections;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.ObjectUtils;
import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.Joinable;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
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
 * @since 2.0.0
 */
public class ManagedList<X, E> extends ManagedCollection<E> implements List<E> {

	/**
	 * 
	 * @since 2.0.0
	 */
	private final class ManagedListIterator extends WrappedListIterator<E> {
		private E last;

		/**
		 * 
		 * @since 2.0.0
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

	private ArrayList<E> delegate;
	private transient ArrayList<E> snapshot;
	private boolean initialized;

	/**
	 * 
	 * @since 2.0.0
	 */
	public ManagedList() {
		super();
	}

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
	 * @since 2.0.0
	 */
	public ManagedList(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		super(mapping, managedInstance);

		this.delegate = Lists.newArrayList();

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
	 * @since 2.0.0
	 */
	public ManagedList(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, Collection<? extends E> values) {
		super(mapping, managedInstance);

		this.delegate = Lists.newArrayList();

		final HashSet<Object> uniqueSet = Sets.newHashSet();

		if (values instanceof List) {
			final List<? extends E> valuesList = (List<? extends E>) values;
			for (int i = 0; i < valuesList.size(); i++) {
				final E child = valuesList.get(i);
				if (child == null) {
					throw new NullPointerException("Instance " + this.getManagedInstance() + " has null items in its collection " + this.getMapping().getPath());
				}

				this.delegate.add(child);
				uniqueSet.add(child);
			}
		}
		else {
			for (final E child : values) {
				if (child == null) {
					throw new NullPointerException("Instance " + this.getManagedInstance() + " has null items in its collection " + this.getMapping().getPath());
				}

				this.delegate.add(child);
				uniqueSet.add(child);
			}
		}

		if (uniqueSet.size() != this.delegate.size()) {
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

		if (c instanceof List) {
			final List<? extends E> list = (List<? extends E>) c;
			for (int i = 0; i < list.size(); i++) {
				if (this.delegate.contains(list.get(i))) {
					throw this.noDuplicates();
				}
			}
		}
		else {
			for (final E e : c) {
				if (this.delegate.contains(e)) {
					throw this.noDuplicates();
				}
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

		if (c instanceof List) {
			final List<? extends E> list = (List<? extends E>) c;
			for (int i = 0; i < list.size(); i++) {
				if (this.delegate.contains(list.get(i))) {
					throw this.noDuplicates();
				}
			}
		}
		else {
			for (final E e : c) {
				if (this.delegate.contains(e)) {
					throw this.noDuplicates();
				}
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
	public boolean addChild(EntryImpl<Object, ManagedInstance<?>> child) {
		final E e = (E) child.getValue().getInstance();

		if (!this.delegate.contains(e)) {
			return this.delegate.add(e);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addElement(EntryImpl<Object, ?> child) {
		final E e = (E) child.getValue();

		if (!this.delegate.contains(e)) {
			return this.delegate.add(e);
		}

		return false;
	}

	private void attachChildren(Connection connection, final ManagedInstance<?> instance, final PluralMapping<?, ?, E> mapping) throws SQLException {
		final int insertBatchSize = this.getInsertBatchSize();

		final Joinable[] batch = new Joinable[insertBatchSize];

		int i = 0;
		while (i < this.delegate.size()) {
			int batchSize = 0;
			while ((i < this.delegate.size()) && (batchSize < insertBatchSize)) {
				final E child = this.delegate.get(i);

				batch[batchSize] = new Joinable(null, child, i);
				batchSize++;

				i++;
			}

			if (batchSize > 0) {
				mapping.attach(connection, instance, batch, batchSize);
			}
		}
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
	public void flush(Connection connection, boolean removals, boolean force) throws SQLException {
		if (this.removed(connection, removals)) {
			return;
		}

		// for lists the index is maintained in the database
		final ManagedInstance<?> instance = this.getManagedInstance();
		final PluralMapping<?, ?, E> mapping = this.getMapping();

		// forced creation of relations for the new entities
		if (force) {
			this.attachChildren(connection, instance, mapping);

			return;
		}

		if (this.snapshot == null) {
			return;
		}

		if (removals) {
			mapping.detachAll(connection, instance);
		}
		else {
			this.attachChildren(connection, instance, mapping);
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void initialize() {
		if (!this.initialized) {
			final ManagedInstance<?> managedInstance = this.getManagedInstance();

			if (managedInstance == null) {
				throw new PersistenceException("No session to initialize the collection");
			}

			final PluralMapping<?, ?, E> mapping = this.getMapping();

			if (!(mapping instanceof PluralAssociationMapping) //
				|| !managedInstance.tryLoadFromCache((PluralAssociationMapping<?, ?, ?>) mapping)) {
				BatooUtils.addAll(mapping.loadCollection(managedInstance), this.delegate);

				this.initialized = true;

				if (this.getMapping().getOrderBy() != null) {
					mapping.sortList(managedInstance.getInstance());
				}

				managedInstance.updateCollectionCache(mapping);
			}

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
	 * @since 2.0.0
	 */
	@Override
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
	public void refreshChildren() {
		if (this.initialized) {
			super.reset();

			this.snapshot = null;

			this.delegate.clear();
			this.delegate.addAll(this.getMapping().loadCollection(this.getManagedInstance()));
		}
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

		if ((this.getManagedInstance() != null) && (this.snapshot == null)) {
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
		final String instance = this.getManagedInstance().getType().getName() + "@" + this.getManagedInstance().getId().getId();

		return "ManagedList [initialized=" + this.initialized + ", managedInstance=" + instance + ", delegate=" + this.delegate + ", snapshot=" + this.snapshot
			+ ", mapping=" + this.getMapping() + "]";
	}
}

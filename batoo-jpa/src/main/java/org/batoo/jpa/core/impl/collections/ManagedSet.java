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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.Joinable;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

import com.google.common.collect.Sets;

/**
 * The set implementation of managed collection.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <E>
 *            The element type of the represented collection
 * @since $version
 */
public class ManagedSet<X, E> extends ManagedCollection<E> implements Set<E> {

	private HashSet<E> delegate;
	private transient HashSet<E> snapshot;

	private boolean initialized;

	/**
	 * 
	 * @since $version
	 */
	public ManagedSet() {
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
	 * @since $version
	 */
	public ManagedSet(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		super(mapping, managedInstance);

		this.delegate = Sets.newHashSet();

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
	 */
	public ManagedSet(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance, Collection<? extends E> values) {
		super(mapping, managedInstance);

		this.delegate = Sets.newHashSet(values);

		this.initialized = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean add(E e) {
		this.snapshot();

		if (this.delegate.add(e)) {
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
	public boolean addAll(Collection<? extends E> c) {
		this.snapshot();

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

	private void attachChildren(Connection connection, final ManagedInstance<?> instance, final PluralMapping<?, ?, E> mapping, Collection<E> children)
		throws SQLException {
		final int insertBatchSize = this.getInsertBatchSize();

		final Joinable[] batch = new Joinable[insertBatchSize];

		final Iterator<E> i = children.iterator();
		while (i.hasNext()) {
			int batchSize = 0;
			while (i.hasNext() && (batchSize < insertBatchSize)) {
				final E child = i.next();

				batch[batchSize] = new Joinable(null, child, 0);
				batchSize++;
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

		final ManagedInstance<?> managedInstance = this.getManagedInstance();
		final PluralMapping<?, ?, E> mapping = this.getMapping();

		// forced creation of relations for the new entities
		if (force) {
			this.attachChildren(connection, managedInstance, mapping, this.delegate);

			return;
		}

		if (this.snapshot == null) {
			return;
		}

		if (removals) {
			// delete the removals
			final List<E> childrenRemoved = BatooUtils.subtract(this.snapshot, this.delegate);
			for (int i = 0; i < childrenRemoved.size(); i++) {
				mapping.detach(connection, managedInstance, null, childrenRemoved.get(i));
			}
		}
		else {
			// create the additions
			final Collection<E> childrenAdded = BatooUtils.subtract(this.delegate, this.snapshot);
			this.attachChildren(connection, managedInstance, mapping, childrenAdded);
		}
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
	public void initialize() {
		if (!this.initialized) {
			if (this.getManagedInstance() == null) {
				throw new PersistenceException("No session to initialize the collection");
			}

			this.delegate.addAll(this.getMapping().loadCollection(this.getManagedInstance()));

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
			/**
			 * {@inheritDoc}
			 * 
			 */
			@Override
			public void remove() {
				ManagedSet.this.snapshot();
				ManagedSet.this.changed();

				super.remove();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refreshChildren() {
		if (this.initialized) {
			this.reset();

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

		if (this.delegate.removeAll(c)) {
			this.changed();
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
			this.snapshot = Sets.newHashSet(this.delegate);
			this.reset();
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
		final String instance = this.getManagedInstance().getType().getName() + "@" + this.getManagedInstance().getId().getId();

		return "ManagedSet [initialized=" + this.initialized + ", managedInstance=" + instance + ", delegate=" + this.delegate + ", snapshot=" + this.snapshot
			+ ", mapping=" + this.getMapping() + "]";
	}
}

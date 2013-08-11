/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.batoo.common.BatooVersion;
import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;
import org.batoo.jpa.jdbc.Joinable;

import com.google.common.collect.Maps;

/**
 * The set implementation of managed collection.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <K>
 *            The key type of the represented map
 * @param <V>
 *            The value type of the represented map
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ManagedMap<X, K, V> extends ManagedCollection<V> implements Map<K, V> {
	private static final long serialVersionUID = BatooVersion.SERIAL_VERSION_UID;

	private HashMap<K, V> delegate;
	private HashMap<K, V> snapshot;

	private boolean initialized;

	/**
	 * 
	 * @since 2.0.0
	 */
	public ManagedMap() {
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
	public ManagedMap(PluralMappingEx<?, ?, V> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		super(mapping, managedInstance);

		this.delegate = Maps.newHashMap();

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
	public ManagedMap(PluralMappingEx<?, ?, V> mapping, ManagedInstance<?> managedInstance, Map<? extends K, ? extends V> values) {
		this(mapping, managedInstance, false);

		this.delegate = Maps.newHashMap(values);

		this.initialized = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean addChild(EntryImpl<Object, ManagedInstance<?>> child) {
		final K k = (K) child.getKey();
		final V v = (V) child.getValue().getInstance();

		if (!this.delegate.keySet().contains(k) && !this.delegate.values().contains(v)) {
			this.delegate.put(k, v);

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
	public boolean addElement(EntryImpl<Object, ?> child) {
		final K k = (K) child.getKey();
		final V v = (V) child.getValue();

		if (!this.delegate.keySet().contains(k) && !this.delegate.values().contains(v)) {
			this.delegate.put(k, v);

			return true;
		}

		return false;
	}

	private void attachChildren(Connection connection, final ManagedInstance<?> instance, final PluralMappingEx<?, ?, V> mapping, Collection<K> keySet)
		throws SQLException {
		final int insertBatchSize = this.getInsertBatchSize();

		final Joinable[] batch = new Joinable[insertBatchSize];

		final Iterator<K> i = keySet.iterator();
		while (i.hasNext()) {
			int batchSize = 0;
			while (i.hasNext() && (batchSize < insertBatchSize)) {
				final K key = i.next();
				final V child = this.delegate.get(key);

				batch[batchSize] = new Joinable(key, child, 0);
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

		this.delegate.clear();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsKey(Object key) {
		this.initialize();

		return this.delegate.containsKey(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean containsValue(Object value) {
		this.initialize();

		return this.delegate.containsValue(value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		this.initialize();

		return Collections.unmodifiableSet(this.delegate.entrySet());
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

		final ManagedInstance<?> instance = this.getManagedInstance();
		final PluralMappingEx<?, ?, V> mapping = this.getMapping();

		// forced creation of relations for the new entities
		if (force) {
			this.attachChildren(connection, instance, mapping, this.delegate.keySet());

			return;
		}

		if (this.snapshot == null) {
			return;
		}

		if (removals) {
			// delete the removals
			final Map<K, V> childrenRemoved = BatooUtils.subtract(this.snapshot, this.delegate);

			for (final K key : childrenRemoved.keySet()) {
				mapping.detach(connection, instance, key, this.snapshot.get(key));
			}
		}
		else {
			// create the additions
			final Map<K, V> childrenAdded = BatooUtils.subtract(this.delegate, this.snapshot);

			this.attachChildren(connection, instance, mapping, childrenAdded.keySet());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V get(Object key) {
		this.initialize();

		return this.delegate.get(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<V> getDelegate() {
		return this.delegate.values();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Collection<V> getSnapshot() {
		return this.snapshot != null ? this.snapshot.values() : null;
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

			this.delegate.putAll(this.getMapping().<K> loadMap(this.getManagedInstance()));

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
	public Set<K> keySet() {
		this.initialize();

		return Collections.unmodifiableSet(this.delegate.keySet());
	}

	private UnsupportedOperationException noDuplicates() {
		return new UnsupportedOperationException("Duplicates are not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V put(K key, V value) {
		this.snapshot();

		if ((key == null) || (value == null)) {
			throw new NullPointerException();
		}

		if (this.delegate.values().contains(value)) {
			throw this.noDuplicates();
		}

		if (this.delegate.values().contains(key)) {
			throw this.noDuplicates();
		}

		final V v = this.delegate.put(key, value);

		this.changed();

		return v;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.snapshot();

		for (final java.util.Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
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

			this.delegate.putAll(this.getMapping().<K> loadMap(this.getManagedInstance()));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public V remove(Object key) {
		this.snapshot();

		if (this.delegate.containsKey(key)) {
			this.changed();
		}

		return this.delegate.remove(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void removeChild(V child) {
		for (final Entry<K, V> entry : this.delegate.entrySet()) {
			if (entry.getValue().equals(child)) {
				this.delegate.remove(entry.getKey());
				break;
			}
		}
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
			this.snapshot = Maps.newHashMap(this.delegate);
			this.reset();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		final String instance = this.getManagedInstance().getType().getName() + "@" + this.getManagedInstance().getId().getId();

		return "ManagedMap [initialized=" + this.initialized + ", managedInstance=" + instance + ", delegate=" + this.delegate + ", snapshot=" + this.snapshot
			+ ", mapping=" + this.getMapping() + "]";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<V> values() {
		this.initialize();

		return Collections.unmodifiableCollection(this.delegate.values());
	}
}

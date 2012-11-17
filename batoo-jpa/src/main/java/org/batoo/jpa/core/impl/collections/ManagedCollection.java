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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;

import com.google.common.collect.Lists;

/**
 * Marker interface for managed collections.
 * 
 * @param <E>
 *            the element type of the collection
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class ManagedCollection<E> implements Serializable {

	private transient boolean changed;
	private final transient ManagedInstance<?> managedInstance;
	private final transient PluralMapping<?, ?, E> mapping;
	private transient AssociationMapping<?, ?, ?> inverse;
	private final transient int insertBatchSize;

	/**
	 * 
	 * @since 2.0.0
	 */
	public ManagedCollection() {
		super();

		this.mapping = null;
		this.managedInstance = null;
		this.inverse = null;
		this.changed = false;

		this.insertBatchSize = this.managedInstance.getSession().getEntityManager().getJdbcAdaptor().getInsertBatchSize();
	}

	/**
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since 2.0.0
	 */
	public ManagedCollection(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance) {
		super();

		this.mapping = mapping;
		this.managedInstance = managedInstance;
		this.insertBatchSize = this.managedInstance.getSession().getEntityManager().getJdbcAdaptor().getInsertBatchSize();

		if (mapping instanceof PluralAssociationMapping) {
			this.inverse = ((PluralAssociationMapping<?, ?, E>) mapping).getInverse();
		}
	}

	/**
	 * Adds the child to the managed list without initialize checks.
	 * 
	 * @param child
	 *            the child to add
	 * @return if the child is added
	 * 
	 * @since 2.0.0
	 */
	public abstract boolean addChild(EntryImpl<Object, ManagedInstance<?>> child);

	/**
	 * Adds the child to the managed list without initialize checks.
	 * 
	 * @param child
	 *            the child to add
	 * @return if the child is added
	 * 
	 * @since 2.0.0
	 */
	public abstract boolean addElement(EntryImpl<Object, ?> child);

	/**
	 * Marks the collection as changed.
	 * 
	 * @since 2.0.0
	 */
	protected final void changed() {
		if (!this.changed && (this.managedInstance != null)) {
			this.changed = true;

			this.managedInstance.setChanged(this.mapping);
		}
	}

	/**
	 * Flushes the collection
	 * 
	 * @param connection
	 *            the connection
	 * @param removals
	 *            true if the removals should be flushed and false for the additions
	 * @param force
	 *            true to force, effective only for insertions and for new entities.
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since 2.0.0
	 */
	public abstract void flush(Connection connection, boolean removals, boolean force) throws SQLException;

	/**
	 * Returns the delegate collection.
	 * 
	 * @return the delegate collection
	 * 
	 * @since 2.0.0
	 */
	public abstract Collection<E> getDelegate();

	/**
	 * Returns the insertBatchSize of the ManagedCollection.
	 * 
	 * @return the insertBatchSize of the ManagedCollection
	 * 
	 * @since 2.0.0
	 */
	protected int getInsertBatchSize() {
		return this.insertBatchSize;
	}

	/**
	 * Returns the managed instance of the managed collection.
	 * 
	 * @return the managed instance of the managed collection
	 * 
	 * @since 2.0.0
	 */
	public ManagedInstance<?> getManagedInstance() {
		return this.managedInstance;
	}

	/**
	 * Returns the mapping of the managed collection.
	 * 
	 * @return the mapping of the managed collection
	 * 
	 * @since 2.0.0
	 */
	protected PluralMapping<?, ?, E> getMapping() {
		return this.mapping;
	}

	/**
	 * Return the items that are added.
	 * 
	 * @return the items that are added
	 * 
	 * @since 2.0.0
	 */
	protected abstract Collection<E> getSnapshot();

	/**
	 * Initializes the managed collection.
	 * 
	 * @since 2.0.0
	 */
	public abstract void initialize();

	/**
	 * Returns if the list is initialized.
	 * 
	 * @return true if the list is initialized, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public abstract boolean isInitialized();

	/**
	 * Merges the collection with the entity
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the new entity
	 * @param requiresFlush
	 *            if an implicit flush is required
	 * @param processed
	 *            registry of processed entities
	 * @param instances
	 *            the persisted instances
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void mergeWith(EntityManagerImpl entityManager, Object instance, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed,
		LinkedList<ManagedInstance<?>> instances) {
		final ArrayList<E> mergedChildren = Lists.newArrayList();

		final Object children = this.mapping.get(instance);

		// if it is a managed collection and not initialized then skip the merge
		if ((children instanceof ManagedCollection) && !((ManagedCollection<E>) children).isInitialized()) {
			return;
		}

		final Collection<E> collection;
		if (children instanceof Collection) {
			collection = (Collection<E>) children;
		}
		else {
			collection = ((Map<?, E>) children).values();
		}

		// merge all the new children
		for (final E child : collection) {
			mergedChildren.add(entityManager.mergeImpl(child, requiresFlush, processed, instances, this.mapping.cascadesMerge()));
		}

		// make a snapshot
		this.snapshot();
		final Collection<E> delegate = this.getDelegate();

		boolean changed = false;

		final SessionImpl session = entityManager.getSession();

		final PluralAssociationMapping<?, ?, ?> inversePluralMapping = (this.inverse != null) && (this.inverse.getAttribute() instanceof PluralAttributeImpl) ? //
			(PluralAssociationMapping<?, ?, ?>) this.inverse : null;

		// TODO needs to be overriden by ManagedMap
		// add the new children
		for (int i = 0; i < mergedChildren.size(); i++) {
			final E child = mergedChildren.get(i);
			if (!delegate.contains(child)) {
				this.getDelegate().add(child);

				if (this.inverse != null) {
					if (inversePluralMapping != null) {
						final Collection inverseCollection = (Collection<?>) inversePluralMapping.get(child);
						if (!inverseCollection.contains(this.managedInstance.getInstance())) {
							inverseCollection.add(this.managedInstance.getInstance());
						}
					}
					else {
						this.inverse.set(session.get(child).getInstance(), this.managedInstance.getInstance());
					}
				}

				changed = true;
			}
		}

		// remove the non existent children
		final ArrayList<E> delegateList = Lists.newArrayList(delegate);
		for (int i = 0; i < delegateList.size(); i++) {
			final E child = delegateList.get(i);
			if (!mergedChildren.contains(child)) {
				this.removeChild(child);

				if (this.inverse != null) {
					if (inversePluralMapping != null) {
						final Collection inverseCollection = (Collection<?>) inversePluralMapping.get(child);
						inverseCollection.remove(this.managedInstance.getInstance());
					}
					else {
						this.inverse.set(session.get(child).getInstance(), null);
					}
				}

				changed = true;
			}
		}

		if (changed) {
			this.changed();
		}
	}

	/**
	 * Persists the entities that have been added to the collection.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public void persistAdditions(EntityManagerImpl entityManager) {
		final Collection<E> added = BatooUtils.subtract(this.getDelegate(), this.getSnapshot());
		for (final E e : added) {
			entityManager.persist(e);
		}
	}

	/**
	 * Refreshes the children of the managed collection.
	 * 
	 * @since 2.0.0
	 */
	public abstract void refreshChildren();

	/**
	 * Removes the child from the managed list without initialize checks.
	 * 
	 * @param child
	 *            the child to add
	 * 
	 * @since 2.0.0
	 */
	protected abstract void removeChild(E child);

	/**
	 * @param connection
	 *            the connection
	 * @param removals
	 *            true if the removals should be flushed and false for the additions
	 * @return returns true if the instance has been removed
	 * @throws SQLException
	 *             thrown in case of an SQL error
	 * 
	 * @since 2.0.0
	 */
	protected boolean removed(Connection connection, boolean removals) throws SQLException {
		// if the instance removed remove all the relations
		if (removals && (this.managedInstance.getStatus() == Status.REMOVED)) {
			this.mapping.detachAll(connection, this.managedInstance);

			return true;
		}

		return false;
	}

	/**
	 * Removes the entities that have been orphaned by removal from the collection.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * 
	 * @since 2.0.0
	 */
	public void removeOrphans(EntityManagerImpl entityManager) {
		final List<E> removed = BatooUtils.subtract(this.getSnapshot(), this.getDelegate());
		for (int i = 0; i < removed.size(); i++) {
			entityManager.remove(removed.get(i));
		}
	}

	/**
	 * Clears the changed status.
	 * 
	 * @since 2.0.0
	 */
	protected void reset() {
		this.changed = false;
	}

	/**
	 * Makes a snapshot of the collection
	 * 
	 * @since 2.0.0
	 */
	protected abstract void snapshot();
}

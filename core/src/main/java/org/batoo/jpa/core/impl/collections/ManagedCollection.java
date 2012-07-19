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
import java.util.Collection;
import java.util.IdentityHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
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
 * @since $version
 */
public abstract class ManagedCollection<E> {

	private final transient PluralMapping<?, ?, E> mapping;
	private final transient ManagedInstance<?> managedInstance;
	private boolean changed;
	private AssociationMapping<?, ?, ?> inverse;

	/**
	 * @param mapping
	 *            the mapping
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedCollection(PluralMapping<?, ?, E> mapping, ManagedInstance<?> managedInstance) {
		super();

		this.mapping = mapping;
		this.managedInstance = managedInstance;

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
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean addChild(EntryImpl<Object, ManagedInstance<?>> child);

	/**
	 * Adds the child to the managed list without initialize checks.
	 * 
	 * @param child
	 *            the child to add
	 * @return if the child is added
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean addElement(EntryImpl<Object, ?> child);

	/**
	 * Marks the collection as changed.
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	public abstract void flush(ConnectionImpl connection, boolean removals, boolean force) throws SQLException;

	/**
	 * Returns the delegate collection.
	 * 
	 * @return the delegate collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Collection<E> getDelegate();

	/**
	 * Returns the managed instance of the managed collection.
	 * 
	 * @return the managed instance of the managed collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<?> getManagedInstance() {
		return this.managedInstance;
	}

	/**
	 * Returns the mapping of the managed collection.
	 * 
	 * @return the mapping of the managed collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected PluralMapping<?, ?, E> getMapping() {
		return this.mapping;
	}

	/**
	 * Return the items that are added.
	 * 
	 * @return the items that are added
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract Collection<E> getSnapshot();

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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void mergeWith(EntityManagerImpl entityManager, Object instance, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed) {
		final Collection<E> mergedChildren = Lists.newArrayList();

		final Object children = this.mapping.get(instance);

		// if it is a collection, handle like collection
		if (children instanceof Collection) {
			final Collection<E> collection = (Collection<E>) children;

			// merge all the new children
			for (final E child : collection) {
				mergedChildren.add(entityManager.mergeImpl(child, requiresFlush, processed, this.mapping.cascadesMerge()));
			}
		}
		// handle like a map
		else {
			// TODO Map implementation
		}

		// make a snapshot
		this.snapshot();
		final Collection<E> delegate = this.getDelegate();

		boolean changed = false;

		final SessionImpl session = entityManager.getSession();

		// TODO needs to be overriden by ManagedMap
		// add the new children
		for (final E child : mergedChildren) {
			if (!delegate.contains(child)) {
				this.getDelegate().add(child);

				if (this.inverse != null) {
					this.inverse.set(session.get(child), this.managedInstance.getInstance());
				}

				changed = true;
			}
		}

		// remove the non existent children
		for (final E child : Lists.newArrayList(delegate)) {
			if (!mergedChildren.contains(child)) {
				this.removeChild(child);

				if (this.inverse != null) {
					this.inverse.set(session.get(child), null);
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
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void persistAdditions(EntityManagerImpl entityManager) {
		final Collection<E> added = CollectionUtils.subtract(this.getDelegate(), this.getSnapshot());
		for (final E e : added) {
			entityManager.persistImpl(e, Lists.newArrayList());
		}
	}

	/**
	 * Refreshes the children of the managed collection.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void refreshChildren();

	/**
	 * Removes the child from the managed list without initialize checks.
	 * 
	 * @param child
	 *            the child to add
	 * 
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
	 */
	protected boolean removed(ConnectionImpl connection, boolean removals) throws SQLException {
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
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void removeOrphans(EntityManagerImpl entityManager) {
		final Collection<E> removed = CollectionUtils.subtract(this.getSnapshot(), this.getDelegate());
		for (final E e : removed) {
			entityManager.remove(e);
		}
	}

	/**
	 * Clears the changed status.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void reset() {
		this.changed = false;
	}

	/**
	 * Makes a snapshot of the collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract void snapshot();
}

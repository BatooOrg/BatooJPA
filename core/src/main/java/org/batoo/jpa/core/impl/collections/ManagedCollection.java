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
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

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

	private final transient PluralAssociationMapping<?, ?, E> association;
	private final transient ManagedInstance<?> managedInstance;
	private boolean changed;

	/**
	 * @param association
	 *            the association
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedCollection(PluralAssociationMapping<?, ?, E> association, ManagedInstance<?> managedInstance) {
		super();

		this.association = association;
		this.managedInstance = managedInstance;
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
	public abstract boolean addChild(Object child);

	/**
	 * Marks the collection as changed.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected final void changed() {
		if (!this.changed && (this.managedInstance != null)) {
			this.changed = true;

			this.managedInstance.setAssociationChanged(this.association);
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
	@SuppressWarnings("unchecked")
	public void flush(ConnectionImpl connection, boolean removals, boolean force) throws SQLException {
		final Object source = this.managedInstance.getInstance();

		// if the instance removed remove all the relations
		if (removals && (this.managedInstance.getStatus() == Status.REMOVED)) {
			Collection<E> children = this.getSnapshot();
			if (children == null) {
				children = this.getDelegate();
			}

			for (final E child : children) {
				this.association.getJoinTable().performRemove(connection, source, child);
			}

			return;
		}

		// forced creation of relations for the new entities
		if (force) {
			for (final E child : this.getDelegate()) {
				this.association.getJoinTable().performInsert(connection, source, child);
			}

			return;
		}

		final Collection<E> snapshot = this.getSnapshot();
		if (snapshot == null) {
			return;
		}

		if (removals) {
			// delete the removals
			final Collection<E> childrenRemoved = CollectionUtils.subtract(snapshot, this.getDelegate());
			for (final E child : childrenRemoved) {
				this.association.getJoinTable().performRemove(connection, source, child);
			}
		}
		else {
			// create the additions
			final Collection<E> childrenAdded = CollectionUtils.subtract(this.getDelegate(), snapshot);
			for (final E child : childrenAdded) {
				this.association.getJoinTable().performInsert(connection, source, child);
			}
		}
	}

	/**
	 * Returns the association of the managed collection.
	 * 
	 * @return the association of the managed collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected PluralAssociationMapping<?, ?, E> getAssociation() {
		return this.association;
	}

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
	 * @param entity
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
	public void mergeWith(EntityManagerImpl entityManager, Object entity, MutableBoolean requiresFlush, IdentityHashMap<Object, Object> processed) {
		final Collection<E> mergedChildren = Lists.newArrayList();

		final Object children = this.association.get(entity);

		// if it is a collection, handle like collection
		if (children instanceof Collection) {
			final Collection<E> collection = (Collection<E>) children;

			// merge all the new children
			for (final E child : collection) {
				mergedChildren.add(entityManager.mergeImpl(child, requiresFlush, processed, this.association.cascadesMerge()));
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

		// add the new children
		for (final E child : mergedChildren) {
			if (!delegate.contains(child)) {
				this.addChild(child);

				if (this.association.getInverse() != null) {
					this.association.getInverse().set(session.get(child), this.managedInstance.getInstance());
				}

				changed = true;
			}
		}

		// remove the non existent children
		for (final E child : Lists.newArrayList(delegate)) {
			if (!mergedChildren.contains(child)) {
				this.removeChild(child);

				if (this.association.getInverse() != null) {
					this.association.getInverse().set(session.get(child), null);
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
	 * @param children
	 *            the new children
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void refreshChildren(Collection<? extends E> children);

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

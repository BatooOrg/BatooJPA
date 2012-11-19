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
package org.batoo.jpa.core.impl.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.manager.CallbackAvailability;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public final class Prioritizer {

	/**
	 * Sorts the managed instances based on their dependencies.
	 * <p>
	 * Below is the array of callbacks:
	 * <ul>
	 * <li>Element 0, has PreRemove callback
	 * <li>Element 1, has PrePersist or PreUpdate
	 * <li>Element 2, has PostRemove
	 * <li>Element 3, has PostPersist, PostRemove
	 * </ul>
	 * 
	 * @param updates
	 *            the list of instances to be updated
	 * @param removals
	 *            the list of instances to be removed
	 * @param sortedRemovals
	 *            the sorted array of instances to be removed
	 * @param sortedUpdates
	 *            the sorted array of instances to be updated
	 * @param callbackAvailability
	 *            array of callbacks
	 * 
	 * @since 2.0.0
	 */
	public static void sort(ArrayList<ManagedInstance<?>> updates, ArrayList<ManagedInstance<?>> removals, ManagedInstance<?>[] sortedUpdates,
		ManagedInstance<?>[] sortedRemovals, CallbackAvailability callbackAvailability) {

		Prioritizer.sort(updates, sortedUpdates, true, callbackAvailability);
		Prioritizer.sort(removals, sortedRemovals, false, callbackAvailability);
	}

	private static void sort(ArrayList<ManagedInstance<?>> updates, ManagedInstance<?>[] sortedUpdates, boolean forUpdates,
		CallbackAvailability callbackAvailability) {
		int instanceNo = 0;

		final HashSet<EntityTypeImpl<?>> entities = Sets.newHashSet();
		for (int i = 0; i < updates.size(); i++) {
			entities.add(updates.get(i).getType());
		}

		for (final EntityTypeImpl<?> entity : entities) {
			entity.updateAvailability(callbackAvailability, forUpdates);
		}

		// quick sort based on entity relations
		while (true) {
			boolean removed = false;

			for (final Iterator<EntityTypeImpl<?>> i = entities.iterator(); i.hasNext();) {
				boolean hasDependency = false;

				final EntityTypeImpl<?> e1 = i.next();

				for (final EntityTypeImpl<?> e2 : entities) {

					final EntityTypeImpl<?> dependent = forUpdates ? e1 : e2;
					final EntityTypeImpl<?> dependency = forUpdates ? e2 : e1;

					if (dependent.getDependenciesFor(dependency).length != 0) {
						hasDependency = true;
						break;
					}
				}

				if (!hasDependency) {
					i.remove();

					for (final Iterator<ManagedInstance<?>> i2 = updates.iterator(); i2.hasNext();) {
						final ManagedInstance<?> instance = i2.next();
						if (instance.getType() == e1) {
							i2.remove();

							sortedUpdates[instanceNo++] = instance;
						}
					}

					removed = true;
				}
			}

			if (!removed) {
				break;
			}
		}

		while ((updates.size() > 0)) {

			boolean found = false;

			for (final Iterator<ManagedInstance<?>> i = updates.iterator(); i.hasNext();) {
				boolean hasDependency = false;

				final ManagedInstance<?> mi1 = i.next();
				final EntityTypeImpl<?> e1 = mi1.getType();

				final Object i1 = mi1.getInstance();

				innerLoop:
				for (int j = 0; j < updates.size(); j++) {
					final ManagedInstance<?> mi2 = updates.get(j);
					if (mi1 == mi2) {
						continue;
					}

					final EntityTypeImpl<?> e2 = mi2.getType();
					final Object i2 = mi2.getInstance();

					for (final AssociationMappingImpl<?, ?, ?> association : e1.getDependenciesFor(e2)) {
						final Object dependent = forUpdates ? i1 : i2;
						final Object dependency = forUpdates ? i2 : i1;
						if (association.references(dependent, dependency)) {
							hasDependency = true;
							break innerLoop;
						}
					}

				}

				if (!hasDependency) {
					sortedUpdates[instanceNo++] = mi1;

					i.remove();

					found = true;
				}
			}

			if (!found) {
				throw new PersistenceException("Circular dependencies not yet supported");
			}
		}
	}

	/**
	 * No instantiation.
	 * 
	 * @since 2.0.0
	 */
	private Prioritizer() {
		super();
	}
}

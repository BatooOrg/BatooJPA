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
package org.batoo.jpa.core.impl.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class Prioritizer {

	/**
	 * Sorts the managed instances based on their dependencies.
	 * 
	 * @param updates
	 *            the list of instances to be updated
	 * @param removals
	 *            the list of instances to be removed
	 * @param sortedRemovals
	 *            the sorted array of instances to be removed
	 * @param sortedUpdates
	 *            the sorted array of instances to be updated
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void sort(ArrayList<ManagedInstance<?>> updates, ArrayList<ManagedInstance<?>> removals, ManagedInstance<?>[] sortedUpdates,
		ManagedInstance<?>[] sortedRemovals) {

		Prioritizer.sort(updates, sortedUpdates, true);
		Prioritizer.sort(removals, sortedRemovals, false);
	}

	private static void sort(ArrayList<ManagedInstance<?>> updates, ManagedInstance<?>[] sortedUpdates, boolean forward) {
		int instanceNo = 0;

		final HashSet<EntityTypeImpl<?>> entities = Sets.newHashSet();
		for (final ManagedInstance<?> instance : updates) {
			entities.add(instance.getType());
		}

		// quick sort based on entity relations
		while (true) {
			boolean removed = false;

			for (final Iterator<EntityTypeImpl<?>> i = entities.iterator(); i.hasNext();) {
				boolean hasDependency = false;

				final EntityTypeImpl<?> e1 = i.next();

				for (final EntityTypeImpl<?> e2 : entities) {

					final EntityTypeImpl<?> dependent = forward ? e1 : e2;
					final EntityTypeImpl<?> dependency = forward ? e2 : e1;

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

					for (final AssociationMapping<?, ?, ?> association : e1.getDependenciesFor(e2)) {
						final Object dependent = forward ? i1 : i2;
						final Object dependency = forward ? i2 : i1;
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
	 * @since $version
	 * @author hceylan
	 */
	private Prioritizer() {
		super();
	}
}

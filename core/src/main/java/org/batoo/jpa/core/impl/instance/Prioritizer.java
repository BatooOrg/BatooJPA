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
import java.util.Iterator;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class Prioritizer {

	/**
	 * Sorts the managed instances based on their dependencies.
	 * 
	 * @param instances
	 *            the list of instances
	 * @return the sorted array of instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static ManagedInstance<?>[] sort(ArrayList<ManagedInstance<?>> instances0) {
		final ManagedInstance<?>[] sorted = new ManagedInstance[instances0.size()];

		for (int ii = 0; ii < 10000; ii++) {
			final ArrayList<ManagedInstance<?>> instances = Lists.newArrayList(instances0);
			Prioritizer.sort0(instances, sorted);
		}

		return sorted;
	}

	private static void sort0(final ArrayList<ManagedInstance<?>> instances, final ManagedInstance<?>[] sorted) {
		int instanceNo = 0;

		// final separate out the final ones we know final that have final no dependencies
		for (final Iterator<ManagedInstance<?>> i = instances.iterator(); i.hasNext();) {
			final ManagedInstance<?> instance = i.next();

			if (instance.getType().getDependencyCount() == 0) {
				i.remove();

				sorted[instanceNo++] = instance;
			}
		}

		while ((instances.size() > 0)) {

			boolean found = false;

			for (final Iterator<ManagedInstance<?>> i = instances.iterator(); i.hasNext();) {
				boolean dependent = false;

				final ManagedInstance<?> mi1 = i.next();
				final EntityTypeImpl<?> e1 = mi1.getType();

				if (e1.getDependencyCount() > 0) {
					final Object i1 = mi1.getInstance();

					innerLoop:
					for (int j = 0; j < instances.size(); j++) {
						final ManagedInstance<?> mi2 = instances.get(j);
						if (mi1 == mi2) {
							continue;
						}

						final EntityTypeImpl<?> e2 = mi2.getType();
						final AttributeImpl<?, ?>[] attributes = e1.getDependenciesFor(e2);
						final Object i2 = mi2.getInstance();

						for (final AttributeImpl<?, ?> attribute : attributes) {
							if (attribute.references(i1, i2)) {
								dependent = true;
								break innerLoop;
							}
						}

					}
				}

				if (!dependent) {
					sorted[instanceNo++] = mi1;

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

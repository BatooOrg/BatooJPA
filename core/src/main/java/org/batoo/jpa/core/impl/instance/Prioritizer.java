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

import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class Prioritizer {

	/**
	 * Singleton global instance
	 */
	public static final Prioritizer INSTANCE = new Prioritizer();

	/**
	 * No instantiation.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Prioritizer() {
		super();
	}

	private boolean references(ManagedInstance<?> i1, ManagedInstance<?> i2) {
		final EntityTypeImpl<?> type = i1.getType();
		final EntityTypeImpl<?> associate = i2.getType();

		final AttributeImpl<?, ?>[] attributes = type.getAssociationsFor(associate);

		if (attributes.length == 0) {
			return false;
		}

		final Object instance = i1.getInstance();
		final Object reference = i2.getInstance();

		for (final AttributeImpl<?, ?> attribute : attributes) {
			if (attribute.references(instance, reference)) {
				return true;
			}
		}

		return false;
	}

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
	public ManagedInstance<?>[] sort(ArrayList<ManagedInstance<?>> instances) {
		final ManagedInstance<?>[] sorted = new ManagedInstance[instances.size()];

		// separate out the independent instances
		int instanceNo = 0;
		for (final Iterator<ManagedInstance<?>> i = instances.iterator(); i.hasNext();) {
			final ManagedInstance<?> i1 = i.next();

			boolean hasDependency = false;
			for (final ManagedInstance<?> i2 : instances) {
				if (i1 == i2) {
					continue;
				}

				if (this.references(i1, i2)) {
					hasDependency = true;
					break;
				}
			}

			// if has no reference then it is independent, remove it from
			if (!hasDependency) {
				sorted[instanceNo++] = i1;
				i.remove();
			}
		}

		// if all independent then return the result
		if (instances.size() == 0) {
			return sorted;
		}

		// sort the remaining ones based on topology
		final PriorityList priorityList = new PriorityList(instances.size());

		for (int i = 0; i < instances.size(); i++) {
			priorityList.addInstance(instances.get(i));
		}

		for (int i = 0; i < instances.size(); i++) {
			final ManagedInstance<?> i1 = instances.get(i);

			for (int j = i + 1; j < instances.size(); j++) {
				final ManagedInstance<?> i2 = instances.get(j);

				if (this.references(i1, i2)) {
					priorityList.addDependency(i1, i2);
				}

				if (this.references(i2, i1)) {
					priorityList.addDependency(i2, i1);
				}
			}
		}

		final ManagedInstance<?>[] rest = priorityList.sort();
		for (final ManagedInstance<?> instance : rest) {
			sorted[instanceNo++] = instance;
		}

		return sorted;
	}
}

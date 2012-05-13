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
import java.util.HashMap;
import java.util.HashSet;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.OwnerManyToOneMapping;
import org.batoo.jpa.core.impl.mapping.OwnerOneToOneMapping;
import org.batoo.jpa.core.impl.metamodel.AttributeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class Prioritizer {

	private static final BLogger LOG = BLogger.getLogger(Prioritizer.class);

	/**
	 * Singleton global instance
	 */
	public static final Prioritizer INSTANCE = new Prioritizer();

	private final HashMap<EntityTypeImpl<?>, HashMap<EntityTypeImpl<?>, AttributeImpl<?, ?>[]>> associationMap = Maps.newHashMap();

	/**
	 * No instantiation.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Prioritizer() {
		super();
	}

	/**
	 * Returns the associations for the types.
	 * 
	 * @param source
	 *            the source type
	 * @param associate
	 *            the associate type
	 * @return the associations for the types
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private AttributeImpl<?, ?>[] getAssociationsFor(EntityTypeImpl<?> source, EntityTypeImpl<?> associate) {
		final HashMap<EntityTypeImpl<?>, AttributeImpl<?, ?>[]> sourceMap = this.associationMap.get(source);

		// if source cannot be located then prepare
		if (sourceMap == null) {
			return this.prepareAssociations(source, associate);
		}

		final AttributeImpl<?, ?>[] associations = sourceMap.get(associate);

		// if associate cannot be found the prepare
		if (associations != null) {
			return associations;
		}

		return this.prepareAssociations(source, associate);
	}

	/**
	 * Prepares and returns the associations for the types.
	 * 
	 * @param type
	 *            the source type
	 * @param associate
	 *            the associate type
	 * @return the associations for the types
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private synchronized AttributeImpl<?, ?>[] prepareAssociations(EntityTypeImpl<?> source, EntityTypeImpl<?> associate) {
		HashMap<EntityTypeImpl<?>, AttributeImpl<?, ?>[]> sourceMap = this.associationMap.get(source);
		if (sourceMap == null) {
			sourceMap = Maps.newHashMap();
		}

		AttributeImpl<?, ?>[] associations = sourceMap.get(associate);
		if (associations != null) {
			return associations; // other thread got it before us
		}

		// prepare the related associations
		final HashSet<AttributeImpl<?, ?>> attributes = Sets.newHashSet();

		final Association<?, ?>[] allAssociations = source.getAssociations();
		for (final Association<?, ?> association : allAssociations) {

			// determine the java type
			final AttributeImpl<?, ?> attribute = association.getDeclaringAttribute();

			// only owner associations impose priority
			if (!association.isOwner()) {
				continue;
			}

			// only relations kept in the row impose priority
			if (!(association instanceof OwnerManyToOneMapping) //
				&& !(association instanceof OwnerOneToOneMapping)) {
				continue;
			}

			final Class<?> javaType = attribute.getJavaType();

			if (javaType.isAssignableFrom(associate.getBindableJavaType())) {
				attributes.add(attribute);
			}
		}

		associations = new AttributeImpl[attributes.size()];
		attributes.toArray(associations);
		sourceMap.put(associate, associations);

		if (this.associationMap.get(source) == null) {
			this.associationMap.put(source, sourceMap);
		}

		return associations;
	}

	private boolean references(ManagedInstance<?> i1, ManagedInstance<?> i2) {
		final EntityTypeImpl<?> type = i1.getType();
		final EntityTypeImpl<?> associate = i2.getType();

		final AttributeImpl<?, ?>[] attributes = this.getAssociationsFor(type, associate);

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
		final PriorityList priorityList = new PriorityList(instances.size());

		for (int i = 0; i < instances.size(); i++) {
			final ManagedInstance<?> i1 = instances.get(i);

			priorityList.addInstance(i1);

			for (int j = i + 1; j < instances.size(); j++) {
				final ManagedInstance<?> i2 = instances.get(j);

				if (this.references(i1, i2)) {
					priorityList.addDependency(i, j);
				}

				if (this.references(i2, i1)) {
					priorityList.addDependency(j, i);
				}
			}
		}

		return priorityList.sort();
	}
}

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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.batoo.jpa.core.impl.metamodel.PluralAttributeImpl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class Prioritizer implements Comparator<ManagedInstance<?>> {

	/**
	 * Singleton global instance
	 */
	public static final Prioritizer INSTANCE = new Prioritizer();

	private final HashMap<EntityTypeImpl<?>, HashMap<EntityTypeImpl<?>, Set<Association<?, ?>>>> associationMap = Maps.newHashMap();

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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compare(ManagedInstance<?> o1, ManagedInstance<?> o2) {
		if (this.references(o1, o2)) {
			return -1;
		}

		if (this.references(o2, o1)) {
			return 1;
		}

		return 0;
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
	private Set<Association<?, ?>> getAssociationsFor(EntityTypeImpl<?> source, EntityTypeImpl<?> associate) {
		final HashMap<EntityTypeImpl<?>, Set<Association<?, ?>>> sourceMap = this.associationMap.get(source);

		// if source cannot be located then prepare
		if (sourceMap == null) {
			return this.prepareAssociations(source, associate);
		}

		final Set<Association<?, ?>> associations = sourceMap.get(associate);

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
	private synchronized Set<Association<?, ?>> prepareAssociations(EntityTypeImpl<?> source, EntityTypeImpl<?> associate) {
		HashMap<EntityTypeImpl<?>, Set<Association<?, ?>>> sourceMap = this.associationMap.get(source);
		if (sourceMap == null) {
			sourceMap = Maps.newHashMap();
		}

		Set<Association<?, ?>> associations = sourceMap.get(associate);
		if (associations != null) {
			return associations; // other thread got it before us
		}

		// prepare the related associations
		associations = Sets.newHashSet();

		final Association<?, ?>[] allAssociations = source.getAssociations();
		for (final Association<?, ?> association : allAssociations) {

			// determine the java type
			Class<?> javaType;
			if (association instanceof PluralAttributeImpl) {
				final PluralAttributeImpl<?, ?, ?> pluralAttribute = (PluralAttributeImpl<?, ?, ?>) association;
				javaType = pluralAttribute.getBindableJavaType();
			}
			else {
				javaType = association.getJavaType();
			}

			if (association.isOwner() && javaType.isAssignableFrom(associate.getBindableJavaType())) {
				associations.add(association);
			}
		}

		sourceMap.put(associate, associations);

		if (this.associationMap.get(source) == null) {
			this.associationMap.put(source, sourceMap);
		}

		return associations;
	}

	private boolean references(ManagedInstance<?> o1, ManagedInstance<?> o2) {
		final EntityTypeImpl<?> type = o1.getType();
		final EntityTypeImpl<?> associate = o2.getType();

		final Set<Association<?, ?>> associations = this.getAssociationsFor(type, associate);

		final Object instance = o1.getInstance();
		final Object reference = o1.getInstance();

		for (final Association<?, ?> association : associations) {
			if (association.getDeclaringAttribute().references(instance, reference)) {
				return true;
			}
		}

		return false;
	}
}
